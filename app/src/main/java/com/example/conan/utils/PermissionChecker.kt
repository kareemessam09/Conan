package com.example.conan.utils

import android.annotation.SuppressLint
import android.app.usage.UsageStatsManager
import android.content.Context
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.core.content.ContextCompat

object PermissionChecker {

    @SuppressLint("ServiceCast")
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )

        // Check if our accessibility service is enabled
        val serviceComponent = "com.example.puritypath.service.ContentBlockerAccessibilityService"
        return enabledServices?.contains(serviceComponent) == true
    }

    fun isNotificationsEnabled(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    fun canDrawOverlays(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun hasUsageStatsPermission(context: Context): Boolean {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val currentTime = System.currentTimeMillis()
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            currentTime - 1000 * 60,
            currentTime
        )
        return stats != null && stats.isNotEmpty()
    }

    fun getMissingPermissions(context: Context): List<String> {
        val missingPermissions = mutableListOf<String>()

        if (!isAccessibilityServiceEnabled(context)) {
            missingPermissions.add("Accessibility Service")
        }

        if (!canDrawOverlays(context)) {
            missingPermissions.add("Display Over Other Apps")
        }

        if (!hasUsageStatsPermission(context)) {
            missingPermissions.add("Usage Statistics")
        }

        if (!isNotificationsEnabled(context)) {
            missingPermissions.add("Notifications")
        }

        return missingPermissions
    }
}
