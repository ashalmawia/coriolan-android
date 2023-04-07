package com.ashalmawia.coriolan.data.storage.sqlite.contract

import android.content.ContentValues
import android.database.Cursor
import com.ashalmawia.coriolan.model.Term
import com.ashalmawia.coriolan.data.storage.sqlite.long

object ContractTranslations {

    const val TRANSLATIONS = "Translations"

    const val TRANSLATIONS_CARD_ID = "Transl_CardId"
    const val TRANSLATIONS_TERM_ID = "Transl_TermId"


    val createQuery = """
        CREATE TABLE $TRANSLATIONS(
            $TRANSLATIONS_CARD_ID INTEGER NOT NULL,
            $TRANSLATIONS_TERM_ID INTEGER NOT NULL,
            
            PRIMARY KEY ($TRANSLATIONS_CARD_ID, $TRANSLATIONS_TERM_ID),
            FOREIGN KEY ($TRANSLATIONS_CARD_ID) REFERENCES ${ContractCards.CARDS} (${ContractCards.CARDS_ID})
               ON DELETE CASCADE
               ON UPDATE CASCADE,
            FOREIGN KEY ($TRANSLATIONS_TERM_ID) REFERENCES ${ContractTerms.TERMS} (${ContractTerms.TERMS_ID})
               ON DELETE RESTRICT
               ON UPDATE CASCADE
        );""".trimMargin()


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