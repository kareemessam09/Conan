package com.example.conan.di

import com.example.conan.data.repository.QuoteRepositoryImpl
import com.example.conan.data.repository.StreakRepositoryImpl
import com.example.conan.data.repository.TriggerLogRepositoryImpl
import com.example.conan.data.repository.UserPreferencesRepositoryImpl
import com.example.conan.domain.repository.QuoteRepository
import com.example.conan.domain.repository.StreakRepository
import com.example.conan.domain.repository.TriggerLogRepository
import com.example.conan.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindQuoteRepository(
        quoteRepositoryImpl: QuoteRepositoryImpl
    ): QuoteRepository

    @Binds
    @Singleton
    abstract fun bindStreakRepository(
        streakRepositoryImpl: StreakRepositoryImpl
    ): StreakRepository

    @Binds
    @Singleton
    abstract fun bindTriggerLogRepository(
        triggerLogRepositoryImpl: TriggerLogRepositoryImpl
    ): TriggerLogRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        userPreferencesRepositoryImpl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository
}
