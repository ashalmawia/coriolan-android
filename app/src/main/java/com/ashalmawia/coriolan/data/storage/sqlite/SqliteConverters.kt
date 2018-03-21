package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.ContentValues
import com.ashalmawia.coriolan.learning.scheduler.sr.SRState
import com.ashalmawia.coriolan.model.Expression
import com.ashalmawia.coriolan.model.ExpressionType
import com.ashalmawia.coriolan.model.Language

// ********** EXPRESSION ********************

fun createLanguageContentValues(value: String): ContentValues {
    val cv = ContentValues()
    cv.put(SQLITE_COLUMN_LANG_VALUE, value)
    return cv
}

// ********** EXPRESSION ********************

fun createExpressionContentValues(value: String, type: ExpressionType, language: Language): ContentValues {
    val cv = ContentValues()
    cv.put(SQLITE_COLUMN_VALUE, value)
    cv.put(SQLITE_COLUMN_TYPE, type.value)
    cv.put(SQLITE_COLUMN_LANGUAGE_ID, language.id)
    return cv
}

// ********** DOMAIIN ********************

fun createDomainContentValues(name: String, langOriginal: Language, langTranslations: Language): ContentValues {
    val cv = ContentValues()
    cv.put(SQLITE_COLUMN_NAME, name)
    cv.put(SQLITE_COLUMN_LANG_ORIGINAL, langOriginal.id)
    cv.put(SQLITE_COLUMN_LANG_TRANSLATIONS, langTranslations.id)
    return cv
}

// ********** CARD ********************

fun createCardContentValues(domainId: Long, deckId: Long, original: Expression, cardId: Long? = null): ContentValues {
    val cv = ContentValues()
    if (cardId != null) {
        cv.put(SQLITE_COLUMN_ID, cardId)
    }
    cv.put(SQLITE_COLUMN_FRONT_ID, original.id)
    cv.put(SQLITE_COLUMN_DECK_ID, deckId)
    cv.put(SQLITE_COLUMN_DOMAIN_ID, domainId)
    return cv
}

fun generateCardsReverseContentValues(cardId: Long, translations: List<Expression>): List<ContentValues> {
    return translations.map { toCardsReverseContentValues(cardId, it) }
}

private fun toCardsReverseContentValues(cardId: Long, expression: Expression): ContentValues {
    val cv = ContentValues()
    cv.put(SQLITE_COLUMN_CARD_ID, cardId)
    cv.put(SQLITE_COLUMN_EXPRESSION_ID, expression.id)
    return cv
}

// ********** DECK ********************

fun createDeckContentValues(domainId: Long, name: String): ContentValues {
    val cv = ContentValues()
    cv.put(SQLITE_COLUMN_NAME, name)
    cv.put(SQLITE_COLUMN_DOMAIN_ID, domainId)
    return cv
}

// ********** STATE ********************

fun createSRStateContentValues(cardId: Long, state: SRState): ContentValues {
    val cv = ContentValues()
    cv.put(SQLITE_COLUMN_CARD_ID, cardId)
    cv.put(SQLITE_COLUMN_DUE, state.due)
    cv.put(SQLITE_COLUMN_PERIOD, state.period)
    return cv
}