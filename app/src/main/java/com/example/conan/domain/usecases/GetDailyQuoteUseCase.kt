package com.example.conan.domain.usecases

import com.example.conan.data.models.Quote
import com.example.conan.data.models.QuoteCategory
import com.example.conan.domain.repository.QuoteRepository
import com.example.conan.domain.repository.UserPreferencesRepository
import com.example.conan.service.CustomMessageQuoteService
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetDailyQuoteUseCase @Inject constructor(
    private val quoteRepository: QuoteRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val customMessageQuoteService: CustomMessageQuoteService
) {
    suspend operator fun invoke(): Quote? {
        val preferences = userPreferencesRepository.userPreferences.first()
        val selectedCategories = preferences.selectedQuoteCategories

        return when {
            selectedCategories.isEmpty() -> {
                // If no categories selected, get any quote including custom messages
                getAllAvailableQuotes().randomOrNull()
            }
            selectedCategories.contains(QuoteCategory.CUSTOM) -> {
                // If CUSTOM category is selected, include custom messages
                getQuotesFromSelectedCategories(selectedCategories)
            }
            else -> {
                // Normal flow for other categories
                quoteRepository.getRandomQuoteByCategories(selectedCategories)
            }
        }
    }

    private suspend fun getAllAvailableQuotes(): List<Quote> {
        val dbQuote = quoteRepository.getRandomQuote()
        val customQuotes = customMessageQuoteService.getCustomMessagesAsQuotes()
        return listOfNotNull(dbQuote) + customQuotes
    }

    private suspend fun getQuotesFromSelectedCategories(categories: Set<QuoteCategory>): Quote? {
        val allQuotes = mutableListOf<Quote>()

        // Add quotes from each selected category
        categories.forEach { category ->
            when (category) {
                QuoteCategory.CUSTOM -> {
                    // Add custom messages
                    allQuotes.addAll(customMessageQuoteService.getCustomMessagesAsQuotes())
                }
                else -> {
                    val categoryQuote = quoteRepository.getRandomQuoteByCategory(category)
                    categoryQuote?.let { allQuotes.add(it) }
                }
            }
        }

        return allQuotes.randomOrNull()
    }
}
