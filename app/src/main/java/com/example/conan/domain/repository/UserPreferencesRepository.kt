package com.example.conan.domain.repository

import com.example.conan.data.models.BlockingStrength
import com.example.conan.data.models.QuoteCategory
import com.example.conan.data.models.UserPreferences
import com.example.conan.data.models.Language
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun updateDetectionEnabled(enabled: Boolean)
    suspend fun updateNotificationsEnabled(enabled: Boolean)
    suspend fun updatePanicButtonEnabled(enabled: Boolean)
    suspend fun updateCustomMessages(messages: List<String>)
    suspend fun updateDailyReminderTime(time: String)
    suspend fun updateBlockingStrength(strength: BlockingStrength)
    suspend fun updateSelectedQuoteCategories(categories: Set<QuoteCategory>)
    suspend fun updateOnboardingCompleted(completed: Boolean)
    suspend fun updateSelectedLanguage(language: Language)
}
