package com.example.conan.data.db.dao

import androidx.room.*
import com.example.conan.data.models.Quote
import com.example.conan.data.models.QuoteCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {
    @Query("SELECT * FROM quotes ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuote(): Quote?

    @Query("SELECT * FROM quotes WHERE category = :category ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuoteByCategory(category: QuoteCategory): Quote?

    @Query("SELECT * FROM quotes WHERE category IN (:categories) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuoteByCategories(categories: Set<QuoteCategory>): Quote?

    @Query("SELECT * FROM quotes WHERE category = :category")
    suspend fun getQuotesByCategory(category: QuoteCategory): List<Quote>

    @Query("SELECT * FROM quotes")
    suspend fun getAllQuotesList(): List<Quote>

    @Query("SELECT * FROM quotes WHERE isCustom = 1")
    fun getCustomQuotes(): Flow<List<Quote>>

    @Query("SELECT * FROM quotes")
    fun getAllQuotes(): Flow<List<Quote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: Quote): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<Quote>)

    @Delete
    suspend fun deleteQuote(quote: Quote)

    @Query("DELETE FROM quotes WHERE isCustom = 1 AND id = :quoteId")
    suspend fun deleteCustomQuote(quoteId: Long)
}
