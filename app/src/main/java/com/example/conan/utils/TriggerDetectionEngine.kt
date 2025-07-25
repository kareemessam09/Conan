package com.example.conan.utils

import android.util.Log

/**
 * Optimized trigger detection system with best practices
 * Consolidates all detection logic in one place with proper error handling and performance optimization
 */
object TriggerDetectionEngine {
    private const val TAG = "TriggerDetection"

    // Cache for processed content to avoid redundant checks
    private var lastProcessedContent: String = ""
    private var lastProcessedResult: String? = null

    /**
     * Main detection method with comprehensive pattern matching
     */
    fun detectTrigger(
        content: String,
        packageName: String,
        sensitivity: AppConstants.DetectionSensitivity
    ): String? {
        if (content.isEmpty() || content.length < AppConstants.ServiceConfig.MIN_CONTENT_LENGTH) {
            return null
        }

        // Use cache to avoid redundant processing
        if (content == lastProcessedContent) {
            return lastProcessedResult
        }

        val normalizedContent = content.lowercase().trim()

        try {
            // Strategy 1: URL-based detection (highest priority)
            val urlTrigger = detectInUrls(normalizedContent)
            if (urlTrigger != null) {
                cacheResult(normalizedContent, urlTrigger)
                return urlTrigger
            }

            // Strategy 2: Keyword detection with multiple patterns
            val keywordTrigger = detectKeywords(normalizedContent, sensitivity)
            if (keywordTrigger != null) {
                cacheResult(normalizedContent, keywordTrigger)
                return keywordTrigger
            }

            cacheResult(normalizedContent, null)
            return null

        } catch (e: Exception) {
            return null
        }
    }

    private fun detectInUrls(content: String): String? {
        val urlPatterns = listOf(
            { keyword: String -> content.contains("$keyword.com") },
            { keyword: String -> content.contains("$keyword.org") },
            { keyword: String -> content.contains("www.$keyword") },
            { keyword: String -> content.contains("https://$keyword") },
            { keyword: String -> content.contains("/$keyword/") }
        )

        return AppConstants.TriggerKeywords.getKeywordsForSensitivity(AppConstants.DetectionSensitivity.HIGH)
            .find { keyword -> urlPatterns.any { pattern -> pattern(keyword) } }
    }

    private fun detectKeywords(content: String, sensitivity: AppConstants.DetectionSensitivity): String? {
        val keywords = AppConstants.TriggerKeywords.getKeywordsForSensitivity(sensitivity)

        return keywords.find { keyword ->
            detectKeywordWithStrategies(content, keyword)
        }
    }

    private fun detectKeywordWithStrategies(content: String, keyword: String): Boolean {
        val keywordLower = keyword.lowercase()

        // Strategy 1: Word boundary detection (most accurate)
        if (isWordBoundaryMatch(content, keywordLower)) return true

        return false
    }

    private fun isWordBoundaryMatch(content: String, keyword: String): Boolean {
        return try {
            val pattern = "\\b${Regex.escape(keyword)}\\b".toRegex(RegexOption.IGNORE_CASE)
            pattern.containsMatchIn(content)
        } catch (e: Exception) {
            false
        }
    }

    private fun cacheResult(content: String, result: String?) {
        lastProcessedContent = content
        lastProcessedResult = result
    }

    /**
     * Clear cache (call when needed to free memory)
     */
    fun clearCache() {
        lastProcessedContent = ""
        lastProcessedResult = null
    }
}