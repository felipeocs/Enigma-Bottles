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

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun get(key: String, lang: String): String {
        val ctx = appContext ?: return key
        try {
            val config = Configuration(ctx.resources.configuration)
            config.setLocale(Locale.forLanguageTag(lang))
            val localeContext = ctx.createConfigurationContext(config)
            val resourceId = localeContext.resources.getIdentifier(key, "string", localeContext.packageName)
            if (resourceId != 0) {
                return localeContext.resources.getString(resourceId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Fallback to default resources
        try {
            val defaultResId = ctx.resources.getIdentifier(key, "string", ctx.packageName)
            if (defaultResId != 0) {
                return ctx.resources.getString(defaultResId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return key
    }
}
