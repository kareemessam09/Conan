package com.example.conan.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.conan.data.models.BlockingStrength
import com.example.conan.data.models.QuoteCategory
import com.example.conan.data.models.UserPreferences
import com.example.conan.data.models.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesDataStore @Inject constructor(private val context: Context) {

    private object PreferencesKeys {
        val IS_DETECTION_ENABLED = booleanPreferencesKey("is_detection_enabled")
        val IS_NOTIFICATIONS_ENABLED = booleanPreferencesKey("is_notifications_enabled")
        val IS_PANIC_BUTTON_ENABLED = booleanPreferencesKey("is_panic_button_enabled")
        val CUSTOM_MESSAGES = stringSetPreferencesKey("custom_messages")
        val DAILY_REMINDER_TIME = stringPreferencesKey("daily_reminder_time")
        val BLOCKING_STRENGTH = stringPreferencesKey("blocking_strength")
        val SELECTED_QUOTE_CATEGORIES = stringSetPreferencesKey("selected_quote_categories")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
    }

    val userPreferences: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->
            // Handle blocking strength with fallback for old MEDIUM values
            val blockingStrengthString = preferences[PreferencesKeys.BLOCKING_STRENGTH] ?: BlockingStrength.HIGH.name
            val blockingStrength = try {
                BlockingStrength.valueOf(blockingStrengthString)
            } catch (e: IllegalArgumentException) {
                // If stored value is invalid (like old MEDIUM), default to HIGH
                android.util.Log.w("UserPreferencesDataStore", "Invalid blocking strength '$blockingStrengthString', defaulting to HIGH")
                BlockingStrength.HIGH
            }

            // Handle language with fallback
            val languageString = preferences[PreferencesKeys.SELECTED_LANGUAGE] ?: Language.ENGLISH.name
            val selectedLanguage = try {
                Language.valueOf(languageString)
            } catch (e: IllegalArgumentException) {
                android.util.Log.w("UserPreferencesDataStore", "Invalid language '$languageString', defaulting to ENGLISH")
                Language.ENGLISH
            }

            UserPreferences(
                isDetectionEnabled = preferences[PreferencesKeys.IS_DETECTION_ENABLED] ?: true,
                isNotificationsEnabled = preferences[PreferencesKeys.IS_NOTIFICATIONS_ENABLED] ?: false,
                isPanicButtonEnabled = preferences[PreferencesKeys.IS_PANIC_BUTTON_ENABLED] ?: true,
                customMessages = preferences[PreferencesKeys.CUSTOM_MESSAGES]?.toList() ?: emptyList(),
                dailyReminderTime = preferences[PreferencesKeys.DAILY_REMINDER_TIME] ?: "08:00",
                blockingStrength = blockingStrength,
                selectedQuoteCategories = preferences[PreferencesKeys.SELECTED_QUOTE_CATEGORIES]?.mapNotNull {
                    try {
                        QuoteCategory.valueOf(it)
                    } catch (e: IllegalArgumentException) {
                        // Handle invalid quote categories gracefully
                        null
                    }
                }?.toSet()
                    ?: setOf(QuoteCategory.QURAN, QuoteCategory.MOTIVATION),
                hasCompletedOnboarding = preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false,
                selectedLanguage = selectedLanguage
            )
        }

    suspend fun updateDetectionEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DETECTION_ENABLED] = enabled
        }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updatePanicButtonEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_PANIC_BUTTON_ENABLED] = enabled
        }
    }

    suspend fun updateCustomMessages(messages: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CUSTOM_MESSAGES] = messages.toSet()
        }
    }

    suspend fun updateDailyReminderTime(time: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_REMINDER_TIME] = time
        }
    }

    suspend fun updateBlockingStrength(strength: BlockingStrength) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BLOCKING_STRENGTH] = strength.name
        }
    }

    suspend fun updateSelectedQuoteCategories(categories: Set<QuoteCategory>) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_QUOTE_CATEGORIES] = categories.map { it.name }.toSet()
        }
    }

    suspend fun updateOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] = completed
        }
    }

    suspend fun updateSelectedLanguage(language: Language) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_LANGUAGE] = language.name
        }
    }
}
