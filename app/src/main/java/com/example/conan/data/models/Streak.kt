package com.example.conan.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "streaks")
data class Streak(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime? = null, // null means streak is ongoing
    val dayCount: Int = 0,
    val isActive: Boolean = true
)
