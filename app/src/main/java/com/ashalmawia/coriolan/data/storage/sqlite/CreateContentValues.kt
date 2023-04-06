package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.ContentValues
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Extras
import com.ashalmawia.coriolan.model.Term
import com.ashalmawia.coriolan.model.Language
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.joda.time.DateTime

object CreateContentValues : ExtrasDeserializer {

    private val objectMapper = jacksonObjectMapper()

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

    fun createTermContentValues(value: String, language: Language, extras: Extras?) = createTermContentValues(value, language.id, extras)

    fun createTermContentValues(value: String, languageId: Long, extras: Extras?, id: Long? = null): ContentValues {
        val cv = ContentValues()
        if (id != null) {
            cv.put(SQLITE_COLUMN_ID, id)
        }
        cv.put(SQLITE_COLUMN_VALUE, value)
        cv.put(SQLITE_COLUMN_EXTRAS, serialize(extras))
        cv.put(SQLITE_COLUMN_LANGUAGE_ID, languageId)
        return cv
    }

// ********** TERM EXTRAS ************

    private fun serialize(extras: Extras?): String? {
        return extras?.run { objectMapper.writeValueAsString(extras) }
    }

    override fun deserialize(value: String?): Extras {
        return if (value.isNullOrBlank()) {
            Extras.empty()
        } else {
            objectMapper.readValue(value, Extras::class.java)
        }
    }

// ********** DOMAIN ********************

    fun createDomainContentValues(name: String?, langOriginal: Language, langTranslations: Language) = createDomainContentValues(name, langOriginal.id, langTranslations.id)

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

    fun createCardContentValues(domainId: Long, deckId: Long, original: Term, cardType: CardType, cardId: Long? = null) =
            createCardContentValues(domainId, deckId, original.id, cardType, cardId)

    fun createCardContentValues(domainId: Long, deckId: Long, originalId: Long, cardType: CardType, cardId: Long? = null): ContentValues {
        val cv = ContentValues()
        if (cardId != null) {
            cv.put(SQLITE_COLUMN_ID, cardId)
        }
        cv.put(SQLITE_COLUMN_FRONT_ID, originalId)
        cv.put(SQLITE_COLUMN_DECK_ID, deckId)
        cv.put(SQLITE_COLUMN_DOMAIN_ID, domainId)
        cv.put(SQLITE_COLUMN_TYPE, cardType.value)
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

// ********** LEARNING PROGRESS ********************

    fun createAllLearningProgressContentValues(
            cardId: Long, learningProgress: LearningProgress): List<ContentValues> {
        return learningProgress.states.map {
            (exerciseId, _) -> createCardStateContentValues(cardId, exerciseId, learningProgress)
        }
    }

    fun createCardStateContentValues(cardId: Long, exerciseId: ExerciseId, learningProgress: LearningProgress): ContentValues {
        val state = learningProgress.stateFor(exerciseId)
        return createCardStateContentValues(cardId, exerciseId, state.due, state.period)
    }

    fun createCardStateContentValues(cardId: Long, exerciseId: ExerciseId, due: DateTime, period: Int): ContentValues {
        val cv = ContentValues()
        cv.put(SQLITE_COLUMN_CARD_ID, cardId)
        cv.put(SQLITE_COLUMN_EXERCISE, exerciseId.value)
        cv.put(SQLITE_COLUMN_DUE_DATE, due)
        cv.put(SQLITE_COLUMN_PERIOD, period)
        return cv
    }

}