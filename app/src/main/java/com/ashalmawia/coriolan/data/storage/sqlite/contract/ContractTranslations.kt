package com.ashalmawia.coriolan.data.storage.sqlite.contract

import android.content.ContentValues
import android.database.Cursor
import com.ashalmawia.coriolan.model.Term
import com.ashalmawia.coriolan.data.storage.sqlite.long

object ContractTranslations {

    const val TRANSLATIONS = "Translations"

    const val TRANSLATIONS_CARD_ID = "Transl_CardId"
    const val TRANSLATIONS_TERM_ID = "Transl_TermId"


    fun Cursor.translationsCardId(): Long { return long(TRANSLATIONS_CARD_ID) }
    fun Cursor.translationsTermId(): Long { return long(TRANSLATIONS_TERM_ID) }


    @JvmName("generateTranslatinosContentValuesTerms")
    fun generateTranslationsContentValues(cardId: Long, translations: List<Term>): List<ContentValues> {
        return translations.map { toTranslationsContentValues(cardId, it.id) }
    }

    @JvmName("generateTranslatinosContentValuesIds")
    fun generateTranslationsContentValues(cardId: Long, translationsIds: List<Long>): List<ContentValues> {
        return translationsIds.map { toTranslationsContentValues(cardId, it) }
    }

    private fun toTranslationsContentValues(cardId: Long, termId: Long): ContentValues {
        val cv = ContentValues()
        cv.put(TRANSLATIONS_CARD_ID, cardId)
        cv.put(TRANSLATIONS_TERM_ID, termId)
        return cv
    }

}