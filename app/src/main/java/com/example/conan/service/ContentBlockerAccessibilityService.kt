package com.example.conan.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.conan.data.models.BlockingStrength
import com.example.conan.domain.usecases.LogTriggerEventUseCase
import com.example.conan.domain.repository.UserPreferencesRepository
import com.example.conan.data.models.TriggerAction
import com.example.conan.utils.AppConstants
import com.example.conan.utils.TriggerDetectionEngine
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import androidx.localbroadcastmanager.content.LocalBroadcastManager

@AndroidEntryPoint
class ContentBlockerAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var logTriggerEventUseCase: LogTriggerEventUseCase

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var lastCheckTime = 0L
    private var isReceiverRegistered = false
    private var isDetectionEnabled = true
    private var detectionDisableJob: Job? = null
    private val triggerCache = mutableMapOf<String, Long>()
    private var lastDialogTime = 0L

    companion object {
        private const val TAG = "ContentBlocker"
        private const val RETRY_ATTEMPTS = 5
        private const val RETRY_DELAY_MS = 1000L
        private const val TRIGGER_COOLDOWN_MS = 5000L
        private const val DIALOG_DEBOUNCE_MS = 2000L
    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                    AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED or
                    AccessibilityEvent.TYPE_VIEW_SCROLLED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            packageNames = AppConstants.MonitoredApps.ALL_MONITORED.toTypedArray()
            notificationTimeout = 100
        }

        serviceInfo = info
        registerBroadcastReceivers()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerBroadcastReceivers() {
        if (isReceiverRegistered) {
            return
        }

        val filter = IntentFilter().apply {
            addAction(AppConstants.Actions.CLOSE_APP)
            addAction(AppConstants.Actions.DISABLE_DETECTION)
        }

        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter)
            isReceiverRegistered = true
        } catch (e: Exception) {
        }

        // Fallback: Register system-wide broadcast
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(broadcastReceiver, filter, RECEIVER_NOT_EXPORTED)
            } else {
                registerReceiver(broadcastReceiver, filter)
            }
        } catch (e: Exception) {

        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            when (action) {
                AppConstants.Actions.CLOSE_APP -> {
                    val packageName = intent.getStringExtra("package_name") ?: return
                    val blockingStrength = intent.getStringExtra("blocking_strength")
                    android.util.Log.d(TAG, "📱 Received close app command for $packageName, strength: $blockingStrength")
                    serviceScope.launch {
                        if (blockingStrength == "HIGH") {
                            terminateApp(packageName)
                        } else {
                            performGlobalAction(GLOBAL_ACTION_HOME)
                        }
                    }
                }
                AppConstants.Actions.DISABLE_DETECTION -> {
                    handleDisableDetection()
                }
            }
        }
    }

    private fun terminateApp(packageName: String) {
        try {
            val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.killBackgroundProcesses(packageName)
            performGlobalAction(GLOBAL_ACTION_HOME)
        } catch (e: Exception) {
            performGlobalAction(GLOBAL_ACTION_HOME) // Fallback
        }
    }

    private fun handleDisableDetection() {
        detectionDisableJob?.cancel()
        isDetectionEnabled = false

        detectionDisableJob = serviceScope.launch {
            try {
                delay(AppConstants.ServiceConfig.DETECTION_DISABLE_DURATION_MS)
                isDetectionEnabled = true
                // Sync with UserPreferencesRepository
                try {
                    userPreferencesRepository.updateDetectionEnabled(true)
                } catch (e: Exception) {
                }
            } catch (e: CancellationException) {
            }
        }
    }

    private fun getDynamicThrottleMs(packageName: String): Long {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val availableMemoryMb = memoryInfo.availMem / (1024 * 1024)
        val cpuCores = Runtime.getRuntime().availableProcessors()

        return if (AppConstants.MonitoredApps.BROWSERS.contains(packageName)) {
            when {
                availableMemoryMb > 2000 && cpuCores > 4 -> 500L
                availableMemoryMb > 1000 -> 600L
                else -> 800L
            }
        } else {
            when {
                availableMemoryMb > 2000 && cpuCores > 4 -> 50L
                availableMemoryMb > 1000 -> 100L
                else -> 200L
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            serviceScope.launch { handleAccessibilityEvent(it) }
        }
    }

    private suspend fun handleAccessibilityEvent(event: AccessibilityEvent) {
        try {
            val packageName = event.packageName?.toString() ?: run {
                android.util.Log.w(TAG, "⚠️ No package name in event")
                return
            }


            if (!isDetectionEnabled) {
                return
            }

            val shouldMonitor = AppConstants.MonitoredApps.shouldMonitorApp(packageName)

            if (!shouldMonitor) {
                return
            }

            val currentTime = System.currentTimeMillis()
            val throttleMs = if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
                getDynamicThrottleMs(packageName) / 2
            } else {
                getDynamicThrottleMs(packageName)
            }
            if (currentTime - lastCheckTime < throttleMs) {
                return
            }
            lastCheckTime = currentTime

            val preferences = userPreferencesRepository.userPreferences.first()
            if (!preferences.isDetectionEnabled) {
                return
            }

            checkContentForTriggers(packageName, preferences.blockingStrength, preferences.detectionSensitivity, event)

        } catch (exception: Exception) {
        }
    }

    private suspend fun checkContentForTriggers(
        packageName: String,
        blockingStrength: BlockingStrength,
        detectionSensitivity: AppConstants.DetectionSensitivity,
        event: AccessibilityEvent
    ) {
        try {
            val sourceNode = event.source
            var allContent = if (sourceNode != null) {
                extractAllTextContent(sourceNode)
            } else {
                ""
            }

            if (allContent.isEmpty()) {
                val rootNode = rootInActiveWindow ?: run {
                    return
                }
                allContent = extractAllTextContent(rootNode)
            }

            var attempt = 1
            while (allContent.isEmpty() && attempt < RETRY_ATTEMPTS) {
                delay(RETRY_DELAY_MS)
                allContent = if (sourceNode != null) {
                    extractAllTextContent(sourceNode)
                } else {
                    rootInActiveWindow?.let { extractAllTextContent(it) } ?: ""
                }
                attempt++
            }


            val foundTrigger = TriggerDetectionEngine.detectTrigger(
                content = allContent,
                packageName = packageName,
                sensitivity = detectionSensitivity
            )

            if (foundTrigger != null) {
                val cacheKey = "$packageName:$foundTrigger"
                val lastTriggerTime = triggerCache[cacheKey] ?: 0L
                if (System.currentTimeMillis() - lastTriggerTime < TRIGGER_COOLDOWN_MS) {
                    return
                }
                triggerCache[cacheKey] = System.currentTimeMillis()
                handleTriggerDetected(packageName, foundTrigger, blockingStrength)
            } else {
            }

        } catch (exception: Exception) {
        }
    }

    private suspend fun handleTriggerDetected(
        packageName: String,
        keyword: String,
        blockingStrength: BlockingStrength
    ) {
        try {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastDialogTime < DIALOG_DEBOUNCE_MS) {
                return
            }
            lastDialogTime = currentTime


            logTriggerEventUseCase(
                appName = packageName,
                triggerKeyword = keyword,
                userAction = TriggerAction.DISMISSED_WITH_OK
            )

            showEmergencyDialog(packageName, keyword, blockingStrength)


        } catch (exception: Exception) {
        }
    }

    private fun showEmergencyDialog(
        packageName: String,
        keyword: String,
        blockingStrength: BlockingStrength
    ) {
        val intent = Intent(this, com.example.conan.presentation.emergency.EmergencyOverlayActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("SHOW_TRIGGER_POPUP", true)
            putExtra("TRIGGER_SOURCE", packageName)
            putExtra("TRIGGER_KEYWORD", keyword)
            putExtra("BLOCKING_STRENGTH", blockingStrength.name)
            putExtra("OVERLAY_MODE", true)
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
        }
    }

    private fun extractAllTextContent(node: AccessibilityNodeInfo): String {
        val textBuilder = StringBuilder()
        try {

            val maxDepth = if (node.packageName?.toString()?.let { AppConstants.MonitoredApps.BROWSERS.contains(it) } == true) {
                AppConstants.ServiceConfig.MAX_TEXT_EXTRACTION_DEPTH
            } else {
                AppConstants.ServiceConfig.MAX_TEXT_EXTRACTION_DEPTH / 2
            }
            extractTextRecursive(node, textBuilder, 0, maxDepth)
        } catch (e: Exception) {
        }
        val result = textBuilder.toString().trim().take(AppConstants.ServiceConfig.MAX_TEXT_LENGTH)
        return result
    }

    private fun isRelevantNode(node: AccessibilityNodeInfo): Boolean {
        val className = node.className?.toString() ?: ""
        val isTextNode = className.contains("TextView") ||
                className.contains("EditText") ||
                className.contains("android.webkit.WebView") ||
                node.text != null ||
                node.contentDescription != null
        return isTextNode || node.childCount > 0
    }

    private fun extractTextRecursive(
        node: AccessibilityNodeInfo,
        textBuilder: StringBuilder,
        depth: Int,
        maxDepth: Int
    ) {
        if (!node.isVisibleToUser) {
            return
        }

        try {
            val className = node.className?.toString() ?: ""
            if (className.contains("TextView") || className.contains("EditText") || className.contains("android.webkit.WebView")) {
                listOf(
                    node.text?.toString(),
                    node.contentDescription?.toString(),
                    node.tooltipText?.toString(),
                    node.hintText?.toString()
                ).forEach { text ->
                    if (!text.isNullOrEmpty() && textBuilder.length < AppConstants.ServiceConfig.MAX_TEXT_LENGTH) {
                        textBuilder.append(text).append(" ")
                    }
                }
            } else if (isRelevantNode(node)) {
                listOf(
                    node.contentDescription?.toString(),
                    node.tooltipText?.toString(),
                    node.hintText?.toString()
                ).forEach { text ->
                    if (!text.isNullOrEmpty() && textBuilder.length < AppConstants.ServiceConfig.MAX_TEXT_LENGTH) {
                        textBuilder.append(text).append(" ")
                    }
                }
            }

            if (depth >= maxDepth && node.childCount == 0) {
                return
            }

            for (i in 0 until node.childCount) {
                try {
                    node.getChild(i)?.let { child ->
                        extractTextRecursive(child, textBuilder, depth + 1, maxDepth)
                    }
                } catch (e: Exception) {
                }
            }
        } catch (e: Exception) {
        }
    }

    override fun onInterrupt() {
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            if (isReceiverRegistered) {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
                try {
                    unregisterReceiver(broadcastReceiver) // Unregister system-wide receiver
                } catch (e: Exception) {
                }
                isReceiverRegistered = false
            }
        } catch (e: Exception) {
        }

        detectionDisableJob?.cancel()
        serviceScope.cancel()
        triggerCache.clear()
    }
}