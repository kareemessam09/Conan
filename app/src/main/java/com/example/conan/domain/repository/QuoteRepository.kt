package com.example.conan.domain.repository

import com.example.conan.data.models.Quote
import com.example.conan.data.models.QuoteCategory
import kotlinx.coroutines.flow.Flow

interface QuoteRepository {
    suspend fun getRandomQuote(): Quote?
    suspend fun getRandomQuoteByCategory(category: QuoteCategory): Quote?
    suspend fun getRandomQuoteByCategories(categories: Set<QuoteCategory>): Quote?
    fun getCustomQuotes(): Flow<List<Quote>>
    fun getAllQuotes(): Flow<List<Quote>>
    suspend fun insertQuote(quote: Quote): Long
    suspend fun insertQuotes(quotes: List<Quote>)
    suspend fun deleteQuote(quote: Quote)
    suspend fun deleteCustomQuote(quoteId: Long)
    suspend fun populateDefaultQuotes()
}
