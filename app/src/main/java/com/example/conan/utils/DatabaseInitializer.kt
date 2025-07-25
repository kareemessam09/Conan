package com.example.conan.utils

import android.content.Context
import com.example.conan.domain.repository.QuoteRepository
import com.example.conan.domain.repository.StreakRepository
import com.example.conan.domain.repository.UserPreferencesRepository
import com.example.conan.service.DailyReminderWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer @Inject constructor(
    private val quoteRepository: QuoteRepository,
    private val streakRepository: StreakRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun initializeDatabase() {
        scope.launch {
            try {
                // Check if this is first launch
                val preferences = userPreferencesRepository.userPreferences.first()

                if (!preferences.hasCompletedOnboarding) {
                    // Populate default quotes
                    quoteRepository.populateDefaultQuotes()

                    // Start initial streak
                    streakRepository.startNewStreak()

                    // Mark onboarding as completed
                    userPreferencesRepository.updateOnboardingCompleted(true)
                }

                // Always check and schedule notifications if enabled
                if (preferences.isNotificationsEnabled) {
                    DailyReminderWorker.scheduleDaily(context, preferences.dailyReminderTime)
                }

            } catch (e: Exception) {
                // Handle initialization error gracefully - don't crash the app

                // Try to mark onboarding as completed even if other operations failed
                try {
                    userPreferencesRepository.updateOnboardingCompleted(true)
                } catch (fallbackError: Exception) {
                }
            }
        }
    }
}
