package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.ContentValues
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.model.ExtraType
import com.ashalmawia.coriolan.model.Term
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

// ********** TERMS ********************

fun createTermContentValues(value: String, language: Language)
    = createTermContentValues(value, language.id)

fun createTermContentValues(value: String, languageId: Long, id: Long? = null): ContentValues {
    val cv = ContentValues()
    if (id != null) {
        cv.put(SQLITE_COLUMN_ID, id)
    }
    cv.put(SQLITE_COLUMN_VALUE, value)
    cv.put(SQLITE_COLUMN_LANGUAGE_ID, languageId)
    return cv
}

// ********** TERM EXTRAS ************

fun createTermExtrasContentValues(exprerssionId: Long, type: ExtraType, value: String, id: Long? = null) =
        createTermExtrasContentValues(exprerssionId, type.value, value, id)

fun createTermExtrasContentValues(exprerssionId: Long, type: Int, value: String, id: Long? = null) =
        ContentValues().apply {
            if (id != null) {
                put(SQLITE_COLUMN_ID, id)
            }
            put(SQLITE_COLUMN_TERM_ID, exprerssionId)
            put(SQLITE_COLUMN_TYPE, type)
            put(SQLITE_COLUMN_VALUE, value)
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

fun createCardContentValues(domainId: Long, deckId: Long, original: Term, cardId: Long? = null)
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
    cv.put(SQLITE_COLUMN_CARD_ID, cardId)
    cv.put(SQLITE_COLUMN_TERM_ID, termId)
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

// ********** CARD STATE ********************

fun createCardStateContentValues(cardId: Long, state: State) : ContentValues {
    return createCardStateContentValues(cardId, state.spacedRepetition.due, state.spacedRepetition.period)
}

fun createCardStateContentValues(cardId: Long, due: DateTime, period: Int): ContentValues {
    val cv = ContentValues()
    cv.put(SQLITE_COLUMN_CARD_ID, cardId)
    cv.put(SQLITE_COLUMN_STATE_SR_DUE, due)
    cv.put(SQLITE_COLUMN_STATE_SR_PERIOD, period)
    return cv
}