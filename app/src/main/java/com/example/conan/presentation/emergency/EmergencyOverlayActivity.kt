package com.example.conan.presentation.emergency

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.conan.ui.theme.PurityPathTheme
import com.example.conan.utils.AppConstants
import kotlinx.coroutines.launch
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.hilt.android.AndroidEntryPoint
import com.example.conan.domain.repository.UserPreferencesRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay

@AndroidEntryPoint
class EmergencyOverlayActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    private var triggerSource: String = "Unknown App"
    private val activityScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get trigger information from intent
        triggerSource = intent.getStringExtra("TRIGGER_SOURCE") ?: "Unknown App"
        val triggerKeyword = intent.getStringExtra("TRIGGER_KEYWORD") ?: "inappropriate content"
        val blockingStrength = intent.getStringExtra("BLOCKING_STRENGTH") ?: "HIGH"
        val isOverlayMode = intent.getBooleanExtra("OVERLAY_MODE", true)

        android.util.Log.d("EmergencyOverlay", "🚨 OVERLAY CREATED - Keyword: $triggerKeyword, Source: $triggerSource, Strength: $blockingStrength, OverlayMode: $isOverlayMode")

        // Make the activity stay on top and persist even when other apps close
        window.addFlags(
            android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    android.view.WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        // Set as top-most activity
        if (isOverlayMode) {
            window.setType(android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        }

        setContent {
            PurityPathTheme {
                EmergencyOverlayContent(
                    triggerSource = triggerSource,
                    triggerKeyword = triggerKeyword,
                    blockingStrength = blockingStrength,
                    onCloseApp = {
                        android.util.Log.d("EmergencyOverlay", "User chose to close app")
                        closeAppBasedOnStrength(blockingStrength)
                        finish()
                    },
                    onContinue = {
                        android.util.Log.d("EmergencyOverlay", "User chose to continue")
                        showQuoteAndDisableDetection()
                    }
                )
            }
        }
    }
    @Composable
    private fun EmergencyOverlayContent(
        triggerSource: String,
        triggerKeyword: String,
        blockingStrength: String,
        onCloseApp: () -> Unit,
        onContinue: () -> Unit
    ) {
        // Animation for card entrance
        val scale = remember { Animatable(0.8f) }
        val alpha = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0D1117),
                            Color(0xFF161B22),
                            Color(0xFF0D1117)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Subtle background glow effect
            Box(
                modifier = Modifier
                    .size(400.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF6D00).copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            radius = 200f
                        ),
                        shape = CircleShape
                    )
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .wrapContentHeight()
                    .scale(scale.value)
                    .alpha(alpha.value)
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color(0xFF4A5568).copy(alpha = 0.5f),
                        spotColor = Color(0xFF2D3748).copy(alpha = 0.8f)
                    )
                    .semantics { contentDescription = "Content warning dialog" },
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A202C)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFFFF6D00).copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Warning icon with glow effect
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFF6D00).copy(alpha = 0.2f),
                                        Color.Transparent
                                    ),
                                    radius = 40f
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Protect icon",
                            tint = Color(0xFFFF6D00),
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    // Title with enhanced styling
                    Text(
                        text = "Sensitive Content",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 28.sp),
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFE2E8F0),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.semantics { contentDescription = "Protect Your Path title" }
                    )

                    // Message container with subtle background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFF2D3748).copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Content alert: \"$triggerKeyword\" detected",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                            color = Color(0xFFE2E8F0),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.semantics { contentDescription = "Content alert: $triggerKeyword detected" }
                        )
                    }

                    // App info with status indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    color = Color(0xFFFF6D00),
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Source: ${getAppDisplayName(triggerSource)}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = Color(0xFF718096),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.semantics { contentDescription = "Source app: ${getAppDisplayName(triggerSource)}" }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Action buttons with improved styling
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Close app button
                        Button(
                            onClick = onCloseApp,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .semantics { contentDescription = "Close app button" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE53E3E),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 8.dp
                            )
                        ) {
                            Text(
                                text = "Stay Halal",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Continue button with gradient background
                        Button(
                            onClick = onContinue,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF2D3748),
                                            Color(0xFF4A5568)
                                        )
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .semantics { contentDescription = "Continue button" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color(0xFFE2E8F0)
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = Color(0xFF718096)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Continue",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
    private fun getAppDisplayName(packageName: String): String {
        return when (packageName) {
            "com.android.chrome" -> "Chrome"
            "org.mozilla.firefox" -> "Firefox"
            "com.opera.browser" -> "Opera"
            "com.microsoft.emmx" -> "Edge"
            "com.instagram.android" -> "Instagram"
            "com.facebook.katana" -> "Facebook"
            "com.twitter.android" -> "Twitter"
            "com.google.android.youtube" -> "YouTube"
            "com.google.android.googlequicksearchbox" -> "Google Search"
            else -> packageName.substringAfterLast(".")
        }
    }

    private fun closeAppBasedOnStrength(blockingStrength: String) {
        when (blockingStrength) {
            "LOW" -> {
                android.util.Log.d("EmergencyOverlay", "LOW blocking: Going to home")
                goToHomeScreen()
            }
            "HIGH" -> {
                android.util.Log.d("EmergencyOverlay", "HIGH blocking: Terminating app completely")
                terminateAppCompletely()
            }
            else -> {
                android.util.Log.d("EmergencyOverlay", "Default blocking: Going to home")
                goToHomeScreen()
            }
        }
    }

    private fun goToHomeScreen() {
        try {
            val homeIntent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
                addCategory(android.content.Intent.CATEGORY_HOME)
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(homeIntent)
            android.util.Log.d("EmergencyOverlay", "✅ Navigated to home screen")
        } catch (e: Exception) {
            android.util.Log.e("EmergencyOverlay", "❌ Error going to home", e)
        }
    }

    private fun terminateAppCompletely() {
        try {
            // Send close command to accessibility service
            val closeIntent = android.content.Intent(AppConstants.Actions.CLOSE_APP).apply {
                putExtra("package_name", triggerSource)
                putExtra("blocking_strength", "HIGH")
            }
            LocalBroadcastManager.getInstance(this@EmergencyOverlayActivity).sendBroadcast(closeIntent)
            android.util.Log.d("EmergencyOverlay", "✅ Sent CLOSE_APP broadcast for $triggerSource")

            // Also go to home screen
            goToHomeScreen()
        } catch (e: Exception) {
            android.util.Log.e("EmergencyOverlay", "❌ Error sending CLOSE_APP broadcast", e)
            goToHomeScreen() // Fallback
        }
    }

    private fun showQuoteAndDisableDetection() {
        setContent {
            PurityPathTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                MotivationalQuoteDialog(
                    onQuoteFinished = {
                        android.util.Log.d("EmergencyOverlay", "Quote finished, closing dialog")
                        terminateAppCompletely()
                        finish()
                    }
                )
            }
            }
        }
    }

    private fun disableDetectionTemporarily() {
        // Send DISABLE_DETECTION broadcast
        try {
            val disableIntent = android.content.Intent(AppConstants.Actions.DISABLE_DETECTION)
            LocalBroadcastManager.getInstance(this@EmergencyOverlayActivity).sendBroadcast(disableIntent)
            android.util.Log.d("EmergencyOverlay", "✅ Sent DISABLE_DETECTION broadcast with action: ${AppConstants.Actions.DISABLE_DETECTION}")
        } catch (e: Exception) {
            android.util.Log.e("EmergencyOverlay", "❌ Error sending DISABLE_DETECTION broadcast", e)
            // Fallback: Update UserPreferencesRepository
            activityScope.launch {
                try {
                    userPreferencesRepository.updateDetectionEnabled(false)
                    android.util.Log.d("EmergencyOverlay", "✅ Fallback: Disabled detection via UserPreferencesRepository")
                    activityScope.launch {
                        delay(AppConstants.ServiceConfig.DETECTION_DISABLE_DURATION_MS)
                        userPreferencesRepository.updateDetectionEnabled(true)
                        android.util.Log.d("EmergencyOverlay", "✅ Fallback: Re-enabled detection via UserPreferencesRepository")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("EmergencyOverlay", "❌ Error updating UserPreferencesRepository", e)
                }
            }
        }
    }

    @Composable
    private fun MotivationalQuoteDialog(onQuoteFinished: () -> Unit) {
        val quotes = listOf(
            "{أَلَمْ يَعْلَم بِأَنَّ اللَّهَ يَرَىٰ}", // Al-Alaq 14
            "{وَأَمَّا مَنْ خَافَ مَقَامَ رَبِّهِ وَنَهَى النَّفْسَ عَنِ الْهَوَىٰ (40) فَإِنَّ الْجَنَّةَ هِيَ الْمَأْوَىٰ (41)}", // An-Nazi'at 40-41
            "{قُل لِّلْمُؤْمِنِينَ يَغُضُّوا مِنْ أَبْصَارِهِمْ وَيَحْفَظُوا فُرُوجَهُمْ ۚ ذَٰلِكَ أَزْكَىٰ لَهُمْ}", // An-Nur 30
            "{وَلَا تَقْرَبُوا الزِّنَىٰ ۖ إِنَّهُ كَانَ فَاحِشَةً وَسَاءَ سَبِيلًا}", // Al-Isra 32
            "{إِنَّ اللَّهَ كَانَ عَلَيْكُمْ رَقِيبًا}", // An-Nisa 1
            "{وَاللَّهُ يُرِيدُ أَن يَتُوبَ عَلَيْكُمْ}", // An-Nisa 27
            "{إِنَّ ٱلَّذِينَ يَخْشَوْنَ رَبَّهُم بِٱلْغَيْبِ لَهُم مَّغْفِرَةٌ وَأَجْرٌ كَبِيرٌ}", // Al-Mulk 12
            "{وَمَن يَتَّقِ ٱللَّهَ يَجْعَل لَّهُۥ مَخْرَجًۭا * وَيَرْزُقْهُ مِنْ حَيْثُ لَا يَحْتَسِبُ}", // At-Talaq 2-3
            "{يَٰٓأَيُّهَا ٱلَّذِينَ ءَامَنُوا۟ لَا تَتَّبِعُوا۟ خُطُوَٰتِ ٱلشَّيْطَٰنِ}", // An-Nur 21
            "{إِنَّ رَبَّكَ لَبِٱلْمِرْصَادِ}" // Al-Fajr 14
        )

        val selectedQuote = remember { quotes.random() }
        val progress = remember { Animatable(0f) }
        val scope = rememberCoroutineScope()
        val showButtons = remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            scope.launch {
                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 5000)
                )
                showButtons.value = true
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0D1117),
                            Color(0xFF161B22),
                            Color(0xFF0D1117)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Subtle background glow effect
            Box(
                modifier = Modifier
                    .size(400.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF2D3748).copy(alpha = 0.3f),
                                Color.Transparent
                            ),
                            radius = 200f
                        ),
                        shape = CircleShape
                    )
            )

            Card(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(0.92f)
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color(0xFF4A5568).copy(alpha = 0.5f),
                        spotColor = Color(0xFF2D3748).copy(alpha = 0.8f)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A202C)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFF2D3748)
                )
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Bismillah with elegant styling
                    Text(
                        text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4FD1C7),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color(0xFF4FD1C7).copy(alpha = 0.3f),
                                blurRadius = 8f
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Quote container with subtle background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFF2D3748).copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(20.dp)
                    ) {
                        Text(
                            text = selectedQuote,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFFE2E8F0),
                            fontWeight = FontWeight.Medium,
                            lineHeight = 32.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Status section with icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = Color(0xFF4FD1C7),
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Detection will be disabled for 3 minutes",
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF4FD1C7),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Progress bar with custom styling
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(
                                color = Color(0xFF2D3748),
                                shape = RoundedCornerShape(3.dp)
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress.value)
                                .height(6.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF4FD1C7),
                                            Color(0xFF38B2AC)
                                        )
                                    ),
                                    shape = RoundedCornerShape(3.dp)
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Think about your future self",
                        fontSize = 12.sp,
                        color = Color(0xFF718096),
                        fontStyle = FontStyle.Italic
                    )

                    // Action buttons with improved styling
                    if (showButtons.value) {
                        Spacer(modifier = Modifier.height(32.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Exit button
                            Button(
                                onClick = { onQuoteFinished() },
                                modifier = Modifier
                                    .weight(0.8f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2D3748),
                                    contentColor = Color(0xFFE2E8F0)
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = Color(0xFF4A5568)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Exit App",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Disable detection button
                            Button(
                                onClick = {
                                    disableDetectionTemporarily()
                                    finish()
                                },
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4FD1C7),
                                    contentColor = Color(0xFF1A202C)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 4.dp,
                                    pressedElevation = 8.dp
                                )
                            ) {
                                Text(
                                    text = "Disable Detection",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancel()
        android.util.Log.d("EmergencyOverlay", "🔌 Activity destroyed")
    }
}