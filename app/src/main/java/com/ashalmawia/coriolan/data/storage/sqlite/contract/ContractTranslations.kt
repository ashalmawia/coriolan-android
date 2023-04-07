package com.ashalmawia.coriolan.data.storage.sqlite.contract

import android.content.ContentValues
import android.database.Cursor
import com.ashalmawia.coriolan.model.Term
import com.ashalmawia.coriolan.data.storage.sqlite.long

object ContractTranslations {

    const val CARDS_REVERSE = "Reverse"

    const val CARDS_REVERSE_CARD_ID = "Reverse_CardId"
    const val CARDS_REVERSE_TERM_ID = "Reverse_TermId"


    fun Cursor.reverseCardId(): Long { return long(CARDS_REVERSE_CARD_ID) }
    fun Cursor.reverseTermId(): Long { return long(CARDS_REVERSE_TERM_ID) }


    @JvmName("generateCardsReverseContentValuesTerms")
    fun generateCardsReverseContentValues(cardId: Long, translations: List<Term>): List<ContentValues> {
        return translations.map { toCardsReverseContentValues(cardId, it.id) }
    }

    @JvmName("generateCardsReverseContentValuesIds")
    fun generateCardsReverseContentValues(cardId: Long, translationsIds: List<Long>): List<ContentValues> {
        return translationsIds.map { toCardsReverseContentValues(cardId, it) }
    }

    private fun toCardsReverseContentValues(cardId: Long, termId: Long): ContentValues {
        val cv = ContentValues()
        cv.put(CARDS_REVERSE_CARD_ID, cardId)
        cv.put(CARDS_REVERSE_TERM_ID, termId)
        return cv
    }

}