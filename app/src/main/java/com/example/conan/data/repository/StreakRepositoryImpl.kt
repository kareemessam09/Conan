package com.example.conan.data.repository

import com.example.conan.data.db.dao.StreakDao
import com.example.conan.data.models.Streak
import com.example.conan.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreakRepositoryImpl @Inject constructor(
    private val streakDao: StreakDao
) : StreakRepository {

    override fun getCurrentStreak(): Flow<Streak?> = streakDao.getCurrentStreak()

    override fun getAllStreaks(): Flow<List<Streak>> = streakDao.getAllStreaks()

    override suspend fun getLongestStreak(): Int? = streakDao.getLongestStreak()

    override suspend fun startNewStreak(): Long {
        // End any current streak first
        endCurrentStreak()

        val newStreak = Streak(
            startDate = LocalDateTime.now(),
            dayCount = 1,
            isActive = true
        )
        return streakDao.insertStreak(newStreak)
    }

    override suspend fun updateStreak(streak: Streak) = streakDao.updateStreak(streak)

    override suspend fun endCurrentStreak() {
        streakDao.endCurrentStreak(LocalDateTime.now())
    }

    override suspend fun incrementCurrentStreak(): Streak? {
        val currentStreak = getCurrentStreak().first()
        return currentStreak?.let { streak ->
            val now = LocalDateTime.now()
            val daysSinceStart = ChronoUnit.DAYS.between(streak.startDate.toLocalDate(), now.toLocalDate()).toInt() + 1
            val updatedStreak = streak.copy(dayCount = daysSinceStart)
            updateStreak(updatedStreak)
            updatedStreak
        }
    }

    override suspend fun resetStreak() {
        endCurrentStreak()
        startNewStreak()
    }
}
