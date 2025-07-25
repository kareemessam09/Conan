package com.example.conan.domain.repository

import com.example.conan.data.models.Streak
import kotlinx.coroutines.flow.Flow

interface StreakRepository {
    fun getCurrentStreak(): Flow<Streak?>
    fun getAllStreaks(): Flow<List<Streak>>
    suspend fun getLongestStreak(): Int?
    suspend fun startNewStreak(): Long
    suspend fun updateStreak(streak: Streak)
    suspend fun endCurrentStreak()
    suspend fun incrementCurrentStreak(): Streak?
    suspend fun resetStreak()
}
