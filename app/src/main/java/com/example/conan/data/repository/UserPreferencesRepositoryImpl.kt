package com.example.conan.data.repository

import com.example.conan.data.datastore.UserPreferencesDataStore
import com.example.conan.data.models.BlockingStrength
import com.example.conan.data.models.QuoteCategory
import com.example.conan.data.models.UserPreferences
import com.example.conan.data.models.Language
import com.example.conan.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : UserPreferencesRepository {

    override val userPreferences: Flow<UserPreferences> = userPreferencesDataStore.userPreferences

    override suspend fun updateDetectionEnabled(enabled: Boolean) =
        userPreferencesDataStore.updateDetectionEnabled(enabled)

    override suspend fun updateNotificationsEnabled(enabled: Boolean) =
        userPreferencesDataStore.updateNotificationsEnabled(enabled)

    override suspend fun updatePanicButtonEnabled(enabled: Boolean) =
        userPreferencesDataStore.updatePanicButtonEnabled(enabled)

    override suspend fun updateCustomMessages(messages: List<String>) =
        userPreferencesDataStore.updateCustomMessages(messages)

    override suspend fun updateDailyReminderTime(time: String) =
        userPreferencesDataStore.updateDailyReminderTime(time)

    override suspend fun updateBlockingStrength(strength: BlockingStrength) =
        userPreferencesDataStore.updateBlockingStrength(strength)

    override suspend fun updateSelectedQuoteCategories(categories: Set<QuoteCategory>) =
        userPreferencesDataStore.updateSelectedQuoteCategories(categories)

    override suspend fun updateOnboardingCompleted(completed: Boolean) =
        userPreferencesDataStore.updateOnboardingCompleted(completed)

    override suspend fun updateSelectedLanguage(language: Language) =
        userPreferencesDataStore.updateSelectedLanguage(language)
}
