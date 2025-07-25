package com.example.conan.data.db

import androidx.room.TypeConverter
import com.example.conan.data.models.QuoteCategory
import com.example.conan.data.models.TriggerAction
import com.example.conan.data.models.BlockingStrength
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun fromQuoteCategory(category: QuoteCategory): String {
        return category.name
    }

    @TypeConverter
    fun toQuoteCategory(categoryName: String): QuoteCategory {
        return QuoteCategory.valueOf(categoryName)
    }

    @TypeConverter
    fun fromTriggerAction(action: TriggerAction): String {
        return action.name
    }

    @TypeConverter
    fun toTriggerAction(actionName: String): TriggerAction {
        return TriggerAction.valueOf(actionName)
    }

    @TypeConverter
    fun fromBlockingStrength(strength: BlockingStrength): String {
        return strength.name
    }

    @TypeConverter
    fun toBlockingStrength(strengthName: String): BlockingStrength {
        return BlockingStrength.valueOf(strengthName)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }

    @TypeConverter
    fun fromQuoteCategorySet(categories: Set<QuoteCategory>): String {
        return categories.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toQuoteCategorySet(categoriesString: String): Set<QuoteCategory> {
        return if (categoriesString.isEmpty()) {
            emptySet()
        } else {
            categoriesString.split(",").map { QuoteCategory.valueOf(it) }.toSet()
        }
    }
}
