package com.ashalmawia.coriolan.data

import android.content.Context
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Language

object LanguagesRegistry {

    // todo stub values until we introduce domains
    private var original: Language = Language(1L, "English")
    private var translations: Language = Language(2L, "Russian")

    fun preinitialize(context: Context) {
        original = readOriginal(context)!!
        translations = readTranslations(context)!!
    }

    fun original(): Language {
        return original
    }

    private fun readOriginal(context: Context): Language? {
        val id = Preferences.get(context).getOriginalLanguageId()
        return if (id == null) null else Repository.get(context).languageById(id)
    }

    fun translations(): Language {
        return translations
    }

    private fun readTranslations(context: Context): Language? {
        val id = Preferences.get(context).getOriginalLanguageId()
        return if (id == null) null else Repository.get(context).languageById(id)
    }

    fun createStubOriginalAndTranslationsIfNeeded(context: Context) {
        if (readOriginal(context) == null) {
            addStubOriginal(context)
        }
        if (readTranslations(context) == null) {
            addStubTranslations(context)
        }
    }

    private fun addStubOriginal(context: Context) {
        val lang = Repository.get(context).addLanguage("English")
        Preferences.get(context).setOriginalLanguageId(lang.id)
    }

    private fun addStubTranslations(context: Context) {
        val lang = Repository.get(context).addLanguage("Russian")
        Preferences.get(context).setTranslationsLanguageId(lang.id)
    }
}