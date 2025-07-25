package com.example.conan.domain.usecases

import com.example.conan.data.models.Streak
import com.example.conan.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageStreakUseCase @Inject constructor(
    private val streakRepository: StreakRepository
) {
    fun getCurrentStreak(): Flow<Streak?> = streakRepository.getCurrentStreak()

    suspend fun incrementStreak(): Streak? = streakRepository.incrementCurrentStreak()

    suspend fun startNewStreak(): Long = streakRepository.startNewStreak()

    suspend fun resetStreak() = streakRepository.resetStreak()

    suspend fun endCurrentStreak() = streakRepository.endCurrentStreak()

    suspend fun getLongestStreak(): Int = streakRepository.getLongestStreak() ?: 0
}
