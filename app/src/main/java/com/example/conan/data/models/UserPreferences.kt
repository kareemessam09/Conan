package com.example.conan.data.models

import com.example.conan.utils.AppConstants

data class UserPreferences(
    val isDetectionEnabled: Boolean = true,
    val isNotificationsEnabled: Boolean = true,
    val isPanicButtonEnabled: Boolean = true,
    val customMessages: List<String> = emptyList(),
    val dailyReminderTime: String = "08:00", // HH:mm format
    val blockingStrength: BlockingStrength = BlockingStrength.HIGH, // Changed from MEDIUM to HIGH
    val selectedQuoteCategories: Set<QuoteCategory> = setOf(
        QuoteCategory.MOTIVATION,
        QuoteCategory.QURAN,
        QuoteCategory.HADITH,
        QuoteCategory.STRENGTH,
        QuoteCategory.CUSTOM
    ),
    val hasCompletedOnboarding: Boolean = false,
    val detectionSensitivity: AppConstants.DetectionSensitivity = AppConstants.DetectionSensitivity.HIGH,
    val selectedLanguage: Language = Language.ENGLISH
)

enum class BlockingStrength {
    LOW,    // Just close Chrome window (minimize/hide it)
    HIGH    // Totally terminate Chrome (kill process completely)
}

enum class Language(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    ARABIC("ar", "العربية")
}
