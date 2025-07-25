package com.example.conan.service

import com.example.conan.data.models.Quote
import com.example.conan.data.models.QuoteCategory
import com.example.conan.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomMessageQuoteService @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {

    suspend fun getCustomMessagesAsQuotes(): List<Quote> {
        val userPrefs = userPreferencesRepository.userPreferences.first()
        return userPrefs.customMessages.mapIndexed { index, message ->
            Quote(
                id = -1000L - index, // Use very negative IDs to avoid conflicts with DB quotes
                text = message,
                source = "Personal Message",
                category = QuoteCategory.CUSTOM,
                isCustom = true
            )
        }
    }

}
