package com.example.conan.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import com.example.conan.data.models.Language
import java.util.*

object LocaleManager {

    /**
     * Sets the app's locale based on the selected language
     */
    fun setLocale(context: Context, language: Language): Context {
        val locale = Locale(language.code)
        Locale.setDefault(locale)

        return updateResources(context, locale)
    }

    /**
     * Updates the resources with the new locale
     */
    private fun updateResources(context: Context, locale: Locale): Context {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(configuration)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
            context
        }
    }

    /**
     * Gets the current locale from the context
     */
    fun getCurrentLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
    }

    /**
     * Recreates the activity to apply language changes
     */
    fun recreateActivity(activity: Activity) {
        activity.recreate()
    }

    /**
     * Gets the Language enum from a locale code
     */
    fun getLanguageFromCode(code: String): Language {
        return Language.values().find { it.code == code } ?: Language.ENGLISH
    }
}
