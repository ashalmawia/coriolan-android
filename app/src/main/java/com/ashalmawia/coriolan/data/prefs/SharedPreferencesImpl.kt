package com.ashalmawia.coriolan.data.prefs

import android.content.Context

class SharedPreferencesImpl(context: Context) : Preferences {

    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    override fun getDefaultDeckId(): Long? {
        return prefs.getLongOrNull(DEFAULT_DECK_ID)
    }

    override fun setDefaultDeckId(id: Long) {
        prefs.edit().putLong(DEFAULT_DECK_ID, id).apply()
    }

    override fun getOriginalLanguageId(): Long? {
        return prefs.getLongOrNull(ORIGINAL_LANGUAGE_ID)
    }

    override fun setOriginalLanguageId(id: Long) {
        prefs.edit().putLong(ORIGINAL_LANGUAGE_ID, id).apply()
    }

    override fun getTranslationsLanguageId(): Long? {
        return prefs.getLongOrNull(TRANSLATIONS_LANGUAGE_ID)
    }

    override fun setTranslationsLanguageId(id: Long) {
        prefs.edit().putLong(TRANSLATIONS_LANGUAGE_ID, id).apply()
    }
}

private const val DEFAULT_DECK_ID = "default_deck_id"
private const val ORIGINAL_LANGUAGE_ID = "original_lang_id"
private const val TRANSLATIONS_LANGUAGE_ID = "tras_lang_id"