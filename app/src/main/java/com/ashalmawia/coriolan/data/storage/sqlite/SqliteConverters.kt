package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.ContentValues
import com.ashalmawia.coriolan.learning.exercise.sr.SRState
import com.ashalmawia.coriolan.model.Expression
import com.ashalmawia.coriolan.model.Language
import org.joda.time.DateTime

// ********** LANGUAGE ********************

fun createLanguageContentValues(value: String, id: Long? = null): ContentValues {
    val cv = ContentValues()
    if (id != null) {
        cv.put(SQLITE_COLUMN_ID, id)
    }
    cv.put(SQLITE_COLUMN_LANG_VALUE, value)
    return cv
}

// ********** EXPRESSION ********************

fun createExpressionContentValues(value: String, language: Language)
    = createExpressionContentValues(value, language.id)

fun createExpressionContentValues(value: String, languageId: Long, id: Long? = null): ContentValues {
    val cv = ContentValues()
    if (id != null) {
        cv.put(SQLITE_COLUMN_ID, id)
    }
    cv.put(SQLITE_COLUMN_VALUE, value)
    cv.put(SQLITE_COLUMN_LANGUAGE_ID, languageId)
    return cv
}

// ********** DOMAIN ********************

fun createDomainContentValues(name: String?, langOriginal: Language, langTranslations: Language)
    = createDomainContentValues(name, langOriginal.id, langTranslations.id)

fun createDomainContentValues(name: String?, langOriginalId: Long, langTranslationsId: Long, id: Long? = null): ContentValues {
    val cv = ContentValues()
    if (id != null) {
        cv.put(SQLITE_COLUMN_ID, id)
    }
    cv.put(SQLITE_COLUMN_NAME, name)
    cv.put(SQLITE_COLUMN_LANG_ORIGINAL, langOriginalId)
    cv.put(SQLITE_COLUMN_LANG_TRANSLATIONS, langTranslationsId)
    return cv
}

// ********** CARD ********************

fun createCardContentValues(domainId: Long, deckId: Long, original: Expression, cardId: Long? = null)
    = createCardContentValues(domainId, deckId, original.id, cardId)

fun createCardContentValues(domainId: Long, deckId: Long, originalId: Long, cardId: Long? = null): ContentValues {
    val cv = ContentValues()
    if (cardId != null) {
        cv.put(SQLITE_COLUMN_ID, cardId)
    }
    cv.put(SQLITE_COLUMN_FRONT_ID, originalId)
    cv.put(SQLITE_COLUMN_DECK_ID, deckId)
    cv.put(SQLITE_COLUMN_DOMAIN_ID, domainId)
    return cv
}

@JvmName("generateCardsReverseContentValuesExpressions")
fun generateCardsReverseContentValues(cardId: Long, translations: List<Expression>): List<ContentValues> {
    return translations.map { toCardsReverseContentValues(cardId, it.id) }
}

@JvmName("generateCardsReverseContentValuesIds")
fun generateCardsReverseContentValues(cardId: Long, translationsIds: List<Long>): List<ContentValues> {
    return translationsIds.map { toCardsReverseContentValues(cardId, it) }
}

private fun toCardsReverseContentValues(cardId: Long, expressionId: Long): ContentValues {
    val cv = ContentValues()
    cv.put(SQLITE_COLUMN_CARD_ID, cardId)
    cv.put(SQLITE_COLUMN_EXPRESSION_ID, expressionId)
    return cv
}

// ********** DECK ********************

fun createDeckContentValues(domainId: Long, name: String, id: Long? = null): ContentValues {
    val cv = ContentValues()
    if (id != null) {
        cv.put(SQLITE_COLUMN_ID, id)
    }
    cv.put(SQLITE_COLUMN_NAME, name)
    cv.put(SQLITE_COLUMN_DOMAIN_ID, domainId)
    return cv
}

// ********** STATE ********************

fun createSRStateContentValues(cardId: Long, state: SRState)
    = createSRStateContentValues(cardId, state.due, state.period)

fun createSRStateContentValues(cardId: Long, due: DateTime, period: Int): ContentValues {
    val cv = ContentValues()
    cv.put(SQLITE_COLUMN_CARD_ID, cardId)
    cv.put(SQLITE_COLUMN_DUE, due)
    cv.put(SQLITE_COLUMN_PERIOD, period)
    return cv
}