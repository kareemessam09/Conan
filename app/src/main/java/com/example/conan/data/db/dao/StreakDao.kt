package com.example.conan.data.db.dao

import androidx.room.*
import com.example.conan.data.models.Streak
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface StreakDao {
    @Query("SELECT * FROM streaks WHERE isActive = 1 ORDER BY startDate DESC LIMIT 1")
    fun getCurrentStreak(): Flow<Streak?>

    @Query("SELECT * FROM streaks ORDER BY startDate DESC")
    fun getAllStreaks(): Flow<List<Streak>>

    @Query("SELECT MAX(dayCount) FROM streaks WHERE isActive = 0")
    suspend fun getLongestStreak(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreak(streak: Streak): Long

    @Update
    suspend fun updateStreak(streak: Streak)

    @Query("UPDATE streaks SET isActive = 0, endDate = :endDate WHERE isActive = 1")
    suspend fun endCurrentStreak(endDate: LocalDateTime)

    @Query("DELETE FROM streaks WHERE id = :streakId")
    suspend fun deleteStreak(streakId: Long)
}
