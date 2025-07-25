package com.example.conan.data.repository

import com.example.conan.data.db.dao.QuoteDao
import com.example.conan.data.models.Quote
import com.example.conan.data.models.QuoteCategory
import com.example.conan.domain.repository.QuoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuoteRepositoryImpl @Inject constructor(
    private val quoteDao: QuoteDao
) : QuoteRepository {

    override suspend fun getRandomQuote(): Quote? = quoteDao.getRandomQuote()

    override suspend fun getRandomQuoteByCategory(category: QuoteCategory): Quote? =
        quoteDao.getRandomQuoteByCategory(category)

    override suspend fun getRandomQuoteByCategories(categories: Set<QuoteCategory>): Quote? =
        quoteDao.getRandomQuoteByCategories(categories)

    override fun getCustomQuotes(): Flow<List<Quote>> = quoteDao.getCustomQuotes()

    override fun getAllQuotes(): Flow<List<Quote>> = quoteDao.getAllQuotes()

    override suspend fun insertQuote(quote: Quote): Long = quoteDao.insertQuote(quote)

    override suspend fun insertQuotes(quotes: List<Quote>) = quoteDao.insertQuotes(quotes)

    override suspend fun deleteQuote(quote: Quote) = quoteDao.deleteQuote(quote)

    override suspend fun deleteCustomQuote(quoteId: Long) = quoteDao.deleteCustomQuote(quoteId)

    override suspend fun populateDefaultQuotes() {
        val defaultQuotes = listOf(
            // Quranic verses
            Quote(
                text = "وَمَن يَتَوَكَّلْ عَلَى ٱللَّهِ فَهُوَ حَسْبُهُ ۚ إِنَّ ٱللَّهَ بَٰلِغُ أَمْرِهِ",
                source = "سورة الطلاق - الآية 3",
                category = QuoteCategory.QURAN
            ),
            Quote(
                text = "إِنَّهُۥ هُوَ ٱلْخَلَّاقُ ٱلْعَلِيمُ",
                source = "سورة يس - الآية 81",
                category = QuoteCategory.QURAN
            ),
            Quote(
                text = "فَٱذْكُرُونِىٓ أَذْكُرْكُمْ وَٱشْكُرُوا۟ لِى وَلَا تَكْفُرُونِ",
                source = "سورة البقرة - الآية 152",
                category = QuoteCategory.QURAN
            ),
            Quote(
                text = "وَمَن يَتَّقِ ٱللَّهَ يَجْعَل لَّهُۥ مَخْرَجًۭا",
                source = "سورة الطلاق - الآية 2",
                category = QuoteCategory.QURAN
            ),
            Quote(
                text = "وَلَا تَقْرَبُوا۟ ٱلزِّنَىٰٓ ۖ إِنَّهُۥ كَانَ فَـٰحِشَةًۭ وَسَآءَ سَبِيلًا",
                source = "سورة الإسراء - الآية 32",
                category = QuoteCategory.QURAN
            ),
            Quote(
                text = "يَعْلَمُ خَآئِنَةَ ٱلْأَعْيُنِ وَمَا تُخْفِى ٱلصُّدُورُ",
                source = "سورة غافر - الآية 19",
                category = QuoteCategory.QURAN
            ),
            Quote(
                text = "قُل لِّلْمُؤْمِنِينَ يَغُضُّوا۟ مِنْ أَبْصَـٰرِهِمْ وَيَحْفَظُوا۟ فُرُوجَهُمْ ۚ ذَٰلِكَ أَزْكَىٰ لَهُمْ",
                source = "سورة النور - الآية 30",
                category = QuoteCategory.QURAN
            ),
            Quote(
                text = "إِنَّ رَبَّكَ لَبِٱلْمِرْصَادِ",
                source = "سورة الفجر - الآية 14",
                category = QuoteCategory.QURAN
            ),
            Quote(
                text = "إِنَّ مِمَّا أَدْرَكَ النَّاسُ مِنْ كَلَامِ النُّبُوَّةِ الأُولَى: إِذَا لَمْ تَسْتَحِ فَاصْنَعْ مَا شِئْتَ",
                source = "رواه البخاري",
                category = QuoteCategory.HADITH
            ),
        Quote(
            text = "الدُّنْيَا سِجْنُ المُؤْمِنِ وَجَنَّةُ الكَافِرِ",
            source = "رواه مسلم",
            category = QuoteCategory.HADITH
        ),
        Quote(
            text = "اتَّقِ اللَّهَ حَيْثُمَا كُنْتَ، وَأَتْبِعِ السَّيِّئَةَ الحَسَنَةَ تَمْحُهَا، وَخَالِقِ النَّاسَ بِخُلُقٍ حَسَنٍ",
            source = "رواه الترمذي",
            category = QuoteCategory.HADITH
        ),
        Quote(
            text = "مَنْ يَسْتَعْفِفْ يُعِفَّهُ اللَّهُ، وَمَنْ يَسْتَغْنِ يُغْنِهِ اللَّهُ",
            source = "رواه البخاري ومسلم",
            category = QuoteCategory.HADITH
        ),
        Quote(
            text = "أَحَبُّ الأَعْمَالِ إِلَى اللَّهِ أَدْوَمُهَا وَإِنْ قَلَّ",
            source = "رواه البخاري ومسلم",
            category = QuoteCategory.HADITH
        ),
        Quote(
            text = "لَا تَتْبَعِ النَّظْرَةَ النَّظْرَةَ، ف��إِنَّ لَكَ الأُولَى وَلَيْسَتْ لَكَ الآخِرَةُ",
            source = "رواه أبو داود والترمذي",
            category = QuoteCategory.HADITH
        ),
        Quote(
            text = "حُفَّتِ الجَنَّةُ بِالمَكَارِهِ، وَحُفَّتِ النَّارُ بِالشَّهَوَاتِ",
            source = "رواه مسلم",
            category = QuoteCategory.HADITH
        )

        )

        insertQuotes(defaultQuotes)
    }
}
