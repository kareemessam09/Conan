package com.example.conan.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.conan.data.models.Quote
import com.example.conan.data.models.Streak
import com.example.conan.data.models.TriggerLog
import com.example.conan.data.db.dao.QuoteDao
import com.example.conan.data.db.dao.StreakDao
import com.example.conan.data.db.dao.TriggerLogDao

@Database(
    entities = [Quote::class, Streak::class, TriggerLog::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class PurityPathDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
    abstract fun streakDao(): StreakDao
    abstract fun triggerLogDao(): TriggerLogDao

    companion object {
        const val DATABASE_NAME = "purity_path_database"
    }
}
