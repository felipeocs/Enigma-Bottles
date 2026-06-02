package com.enigmabottle.data

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

enum class Language(val displayName: String, val code: String) {
    PORTUGUESE("Português", "pt"),
    ENGLISH("English", "en"),
    SPANISH("Español", "es"),
    FRENCH("Français", "fr"),
    GERMAN("Deutsch", "de");

    companion object {
        fun fromCode(code: String): Language = values().find { it.code == code } ?: PORTUGUESE
    }
}

object TextRes {
    private var appContext: Context? = null
    private var currentConfiguredLang: String? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun get(key: String, lang: String): String {
        val ctx = appContext ?: return key
        try {
            if (currentConfiguredLang != lang) {
                val locale = Locale(lang)
                Locale.setDefault(locale)
                val resources = ctx.resources
                val config = resources.configuration
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    config.setLocales(android.os.LocaleList(locale))
                } else {
                    @Suppress("DEPRECATION")
                    config.locale = locale
                }
                @Suppress("DEPRECATION")
                resources.updateConfiguration(config, resources.displayMetrics)
                currentConfiguredLang = lang
            }
            val resourceId = ctx.resources.getIdentifier(key, "string", ctx.packageName)
            if (resourceId != 0) {
                return ctx.resources.getString(resourceId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return key
    }
}
