package com.example.conan.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quotes")
data class Quote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val source: String, // Quran verse reference or hadith
    val category: QuoteCategory,
    val isCustom: Boolean = false
)

enum class QuoteCategory {
    MOTIVATION,
    QURAN,
    HADITH,
    STRENGTH,
    CUSTOM
}
