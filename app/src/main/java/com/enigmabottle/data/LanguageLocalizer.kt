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
    private var baseContext: Context? = null
    private var localizedContext: Context? = null
    private var currentLangCode: String = ""

    fun init(context: Context) {
        baseContext = context
        localizedContext = null
        currentLangCode = ""
    }

    fun get(key: String, lang: String): String {
        val base = baseContext ?: return key
        val targetLang = if (lang.isEmpty()) "pt" else lang
        try {
            if (currentLangCode != targetLang || localizedContext == null) {
                currentLangCode = targetLang
                val locale = Locale(targetLang)
                Locale.setDefault(locale)
                val configuration = Configuration(base.resources.configuration)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    configuration.setLocales(android.os.LocaleList(locale))
                } else {
                    @Suppress("DEPRECATION")
                    configuration.setLocale(locale)
                }
                localizedContext = base.createConfigurationContext(configuration)
            }
            val ctx = localizedContext ?: base
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
