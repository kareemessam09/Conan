package com.example.conan.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.example.conan.domain.usecases.GetDailyQuoteUseCase
import com.example.conan.domain.repository.UserPreferencesRepository
import com.example.conan.utils.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class DailyReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val getDailyQuoteUseCase: GetDailyQuoteUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val preferences = userPreferencesRepository.userPreferences.first()

            if (preferences.isNotificationsEnabled) {
                val quote = getDailyQuoteUseCase()

                notificationHelper.showDailyReminderNotification(
                    title = "Stay Strong Today",
                    message = quote?.text ?: "Remember your commitment to purity and growth.",
                    source = quote?.source
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "daily_reminder_work"

        fun scheduleDaily(context: Context, reminderTime: String) {
            val workManager = WorkManager.getInstance(context)

            // Parse time (HH:mm format)
            val timeParts = reminderTime.split(":")
            val hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()

            // Calculate initial delay to reach the scheduled time
            val currentTimeMillis = System.currentTimeMillis()
            val calendar = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, hour)
                set(java.util.Calendar.MINUTE, minute)
                set(java.util.Calendar.SECOND, 0)

                // If the time has passed today, schedule for tomorrow
                if (timeInMillis <= currentTimeMillis) {
                    add(java.util.Calendar.DAY_OF_MONTH, 1)
                }
            }

            val initialDelay = calendar.timeInMillis - currentTimeMillis

            val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
                1, TimeUnit.DAYS
            )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .setRequiresBatteryNotLow(false)
                        .build()
                )
                .build()

            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyWorkRequest
            )
        }

        fun cancelDaily(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
