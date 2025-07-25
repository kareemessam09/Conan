package com.example.conan.di

import android.content.Context
import androidx.room.Room
import com.example.conan.data.db.PurityPathDatabase
import com.example.conan.data.db.dao.QuoteDao
import com.example.conan.data.db.dao.StreakDao
import com.example.conan.data.db.dao.TriggerLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PurityPathDatabase {
        return Room.databaseBuilder(
            context,
            PurityPathDatabase::class.java,
            PurityPathDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideQuoteDao(database: PurityPathDatabase): QuoteDao = database.quoteDao()

    @Provides
    fun provideStreakDao(database: PurityPathDatabase): StreakDao = database.streakDao()

    @Provides
    fun provideTriggerLogDao(database: PurityPathDatabase): TriggerLogDao = database.triggerLogDao()
}
