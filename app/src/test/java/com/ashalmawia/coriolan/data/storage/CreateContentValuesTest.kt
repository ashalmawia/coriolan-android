package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.storage.sqlite.*
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_DECK_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_DOMAIN_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_FRONT_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_REVERSE_CARD_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_REVERSE_TERM_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_TYPE
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DECKS_DOMAIN_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DECKS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DECKS_NAME
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DOMAINS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DOMAINS_LANG_ORIGINAL
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DOMAINS_LANG_TRANSLATIONS
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DOMAINS_NAME
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.LANGUAGES_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.LANGUAGES_VALUE
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.STATES_CARD_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.STATES_DUE_DATE
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.STATES_EXERCISE
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.STATES_PERIOD
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.TERMS_EXTRAS
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.TERMS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.TERMS_LANGUAGE_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.TERMS_VALUE
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Extras
import com.ashalmawia.coriolan.model.mockLanguage
import com.ashalmawia.coriolan.model.mockLearningProgress
import com.ashalmawia.coriolan.model.mockTerm
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.joda.time.DateTime
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreateContentValuesTest {

    @Test
    fun createLanguageContentValuesTest() {
        // given
        val value = "Russian"

        // when
        val cv = CreateContentValues.createLanguageContentValues(value)

        // then
        assertEquals("values count is correct", 1, cv.size())
        assertEquals("$LANGUAGES_VALUE is correct", value, cv.get(LANGUAGES_VALUE))
    }

    @Test
    fun createLanguageContentValuesTest__hasId() {
        // given
        val value = "Russian"
        val id = 5L

        // when
        val cv = CreateContentValues.createLanguageContentValues(value, id)

        // then
        assertEquals("values count is correct", 2, cv.size())
        assertEquals("$LANGUAGES_ID is correct", id, cv.get(LANGUAGES_ID))
        assertEquals("$LANGUAGES_VALUE is correct", value, cv.get(LANGUAGES_VALUE))
    }

    @Test
    fun createTermContentValuesTest__emptyExtras() {
        // given
        val value = "some value"
        val lang = mockLanguage(777L, "Russian")

        // when
        val cv = CreateContentValues.createTermContentValues(value, lang, null)

        // then
        assertEquals("values count is correct", 3, cv.size())
        assertEquals("$TERMS_VALUE is correct", value, cv.get(TERMS_VALUE))
        assertEquals("$TERMS_LANGUAGE_ID is correct", lang.id, cv.get(TERMS_LANGUAGE_ID))
        assertEquals("$TERMS_EXTRAS is correct", null, cv.get(TERMS_EXTRAS))

        // when
        val id = 7L
        val cv1 = CreateContentValues.createTermContentValues(value, lang.id, null, id)

        // then
        assertEquals("values count is correct", 4, cv1.size())
        assertEquals("$TERMS_ID is correct", id, cv1.get(TERMS_ID))
        assertEquals("$TERMS_VALUE is correct", value, cv1.get(TERMS_VALUE))
        assertEquals("$TERMS_LANGUAGE_ID is correct", lang.id, cv1.get(TERMS_LANGUAGE_ID))
        assertEquals("$TERMS_EXTRAS is correct", null, cv.get(TERMS_EXTRAS))
    }

    @Test
    fun createTermContentValuesTest__nonEmtpyExtras() {
        // given
        val value = "some value"
        val lang = mockLanguage(777L, "Russian")
        val extras = Extras("transcription")
        val objectMapper = jacksonObjectMapper()

        // when
        val cv = CreateContentValues.createTermContentValues(value, lang, extras)

        // then
        assertEquals("values count is correct", 3, cv.size())
        assertEquals("$TERMS_VALUE is correct", value, cv.get(TERMS_VALUE))
        assertEquals("$TERMS_LANGUAGE_ID is correct", lang.id, cv.get(TERMS_LANGUAGE_ID))
        assertEquals("$TERMS_EXTRAS is correct",
                objectMapper.writeValueAsString(extras), cv.get(TERMS_EXTRAS))

        // when
        val id = 7L
        val cv1 = CreateContentValues.createTermContentValues(value, lang.id, extras, id)

        // then
        assertEquals("values count is correct", 4, cv1.size())
        assertEquals("$TERMS_ID is correct", id, cv1.get(TERMS_ID))
        assertEquals("$TERMS_VALUE is correct", value, cv1.get(TERMS_VALUE))
        assertEquals("$TERMS_LANGUAGE_ID is correct", lang.id, cv1.get(TERMS_LANGUAGE_ID))
        assertEquals("$TERMS_EXTRAS is correct",
                objectMapper.writeValueAsString(extras), cv.get(TERMS_EXTRAS))
    }

    @Test
    fun createDomainContentValuesTest() {
        // given
        val name = "Some name"
        val langOriginal = mockLanguage(11L, "Russian")
        val langTranslations = mockLanguage(12L, "French")

        // when
        val cv = CreateContentValues.createDomainContentValues(name, langOriginal, langTranslations)

        // then
        assertEquals("values count is correct", 3, cv.size())
        assertEquals("$DOMAINS_NAME is correct", name, cv.get(DOMAINS_NAME))
        assertEquals("$DOMAINS_LANG_ORIGINAL is correct", langOriginal.id, cv.get(DOMAINS_LANG_ORIGINAL))
        assertEquals("$DOMAINS_LANG_TRANSLATIONS is correct", langTranslations.id, cv.get(DOMAINS_LANG_TRANSLATIONS))

        // when
        val id = 5L
        val cv1 = CreateContentValues.createDomainContentValues(name, langOriginal.id, langTranslations.id, id)

        // then
        assertEquals("values count is correct", 4, cv1.size())
        assertEquals("$DOMAINS_ID is correct", id, cv1.get(DOMAINS_ID))
        assertEquals("$DOMAINS_NAME is correct", name, cv1.get(DOMAINS_NAME))
        assertEquals("$DOMAINS_LANG_ORIGINAL is correct", langOriginal.id, cv1.get(DOMAINS_LANG_ORIGINAL))
        assertEquals("$DOMAINS_LANG_TRANSLATIONS is correct", langTranslations.id, cv1.get(DOMAINS_LANG_TRANSLATIONS))
    }

    @Test
    fun createCardContentValuesTest() {
        // given
        val deckId = 5L
        val domainId = 2L
        val type = CardType.FORWARD
        val lang = mockLanguage()
        val original = mockTerm("some original term", lang)

        // when
        val cv = CreateContentValues.createCardContentValues(domainId, deckId, original, type)

        // then
        assertEquals("values count is correct", 4, cv.size())
        assertEquals("$CARDS_FRONT_ID is correct", original.id, cv.get(CARDS_FRONT_ID))
        assertEquals("$CARDS_DECK_ID is correct", deckId, cv.get(CARDS_DECK_ID))
        assertEquals("$CARDS_DOMAIN_ID is correct", domainId, cv.get(CARDS_DOMAIN_ID))
        assertEquals("$CARDS_TYPE is correct", type.value, cv.get(CARDS_TYPE))
    }

    @Test
    fun createCardContentValuesTest__hasCardId() {
        // given
        val deckId = 5L
        val domainId = 1L
        val lang = mockLanguage()
        val type = CardType.FORWARD
        val original = mockTerm("some original term", lang)
        val cardId = 7L

        // when
        val cv = CreateContentValues.createCardContentValues(domainId, deckId, original, type, cardId)

        // then
        cv.run {
            assertEquals("values count is correct", 5, size())
            assertEquals("$CARDS_ID is correct", cardId, get(CARDS_ID))
            assertEquals("$CARDS_FRONT_ID is correct", original.id, get(CARDS_FRONT_ID))
            assertEquals("$CARDS_DECK_ID is correct", deckId, get(CARDS_DECK_ID))
            assertEquals("$CARDS_DOMAIN_ID is correct", domainId, get(CARDS_DOMAIN_ID))
            assertEquals("$CARDS_TYPE is correct", type.value, get(CARDS_TYPE))
        }

        // when
        val cv1 = CreateContentValues.createCardContentValues(domainId, deckId, original.id, type, cardId)

        // then
        cv1.run {
            assertEquals("values count is correct", 5, size())
            assertEquals("$CARDS_ID is correct", cardId, get(CARDS_ID))
            assertEquals("$CARDS_FRONT_ID is correct", original.id, get(CARDS_FRONT_ID))
            assertEquals("$CARDS_DECK_ID is correct", deckId, get(CARDS_DECK_ID))
            assertEquals("$CARDS_DOMAIN_ID is correct", domainId, get(CARDS_DOMAIN_ID))
            assertEquals("$CARDS_TYPE is correct", type.value, get(CARDS_TYPE))
        }
    }

    @Test
    fun createDeckContentValuesTest() {
        // given
        val name = "New Deck"
        val domainId = 3L

        // when
        val cv = CreateContentValues.createDeckContentValues(domainId, name)

        // then
        assertEquals("values count is correct", 2, cv.size())
        assertEquals("$DECKS_NAME is correct", name, cv.get(DECKS_NAME))
        assertEquals("$DECKS_DOMAIN_ID is correct", domainId, cv.get(DECKS_DOMAIN_ID))
    }

    @Test
    fun createDeckContentValuesTest__hasId() {
        // given
        val name = "New Deck"
        val domainId = 3L
        val deckId = 5L

        // when
        val cv = CreateContentValues.createDeckContentValues(domainId, name, deckId)

        // then
        assertEquals("values count is correct", 3, cv.size())
        assertEquals("$DECKS_ID is correct", deckId, cv.get(DECKS_ID))
        assertEquals("$DECKS_NAME is correct", name, cv.get(DECKS_NAME))
        assertEquals("$DECKS_DOMAIN_ID is correct", domainId, cv.get(DECKS_DOMAIN_ID))
    }

    @Test
    fun generateCardsReverseContentValuesTest() {
        // given
        val cardId = 99L
        val lang = mockLanguage()
        val translations = listOf(
                mockTerm("firework", lang),
                mockTerm("rocket", lang),
                mockTerm( "missile", lang)
        )

        // when
        val cvList = CreateContentValues.generateCardsReverseContentValues(cardId, translations)

        // then
        assertEquals("entries count is correct", 3, cvList.size)
        for (i in 0 until 3) {
            val cv = cvList[i]
            assertEquals("values count is correct", 2, cv.size())
            assertEquals("$CARDS_REVERSE_CARD_ID is correct", cardId, cv.get(CARDS_REVERSE_CARD_ID))
            assertEquals("$CARDS_REVERSE_TERM_ID is correct", translations[i].id, cv.get(CARDS_REVERSE_TERM_ID))
        }

        // when
        val cvList1 = CreateContentValues.generateCardsReverseContentValues(cardId, translations.map { it.id })

        // then
        assertEquals("entries count is correct", 3, cvList1.size)
        for (i in 0 until 3) {
            val cv = cvList1[i]
            assertEquals("values count is correct", 2, cv.size())
            assertEquals("$CARDS_REVERSE_CARD_ID is correct", cardId, cv.get(CARDS_REVERSE_CARD_ID))
            assertEquals("$CARDS_REVERSE_TERM_ID is correct", translations[i].id, cv.get(CARDS_REVERSE_TERM_ID))
        }
    }

    @Test
    fun createStateContentValuesTest() {
        // given
        val cardId = 1L
        val exerciseId = ExerciseId.TEST
        val due = DateTime(1519529781000)
        val period = 16
        val learningProgress = mockLearningProgress(due, period)

        // when
        val cv = CreateContentValues.createCardStateContentValues(cardId, exerciseId, learningProgress)

        // then
        assertEquals("values count is correct", 4, cv.size())
        assertEquals("$STATES_CARD_ID is correct", cardId, cv.get(STATES_CARD_ID))
        assertEquals("$STATES_EXERCISE is correct", exerciseId.value, cv.get(STATES_EXERCISE))
        assertEquals("$STATES_DUE_DATE is correct", due, cv.getAsDate(STATES_DUE_DATE))
        assertEquals("$STATES_PERIOD is correct", period, cv.get(STATES_PERIOD))

        // when
        val state = learningProgress.stateFor(exerciseId)
        val cv1 = CreateContentValues.createCardStateContentValues(cardId, exerciseId, state.due, state.period)

        // then
        assertEquals("values count is correct", 4, cv1.size())
        assertEquals("$STATES_CARD_ID is correct", cardId, cv1.get(STATES_CARD_ID))
        assertEquals("$STATES_EXERCISE is correct", exerciseId.value, cv1.get(STATES_EXERCISE))
        assertEquals("$STATES_DUE_DATE is correct", due, cv1.getAsDate(STATES_DUE_DATE))
        assertEquals("$STATES_PERIOD is correct", period, cv1.get(STATES_PERIOD))
    }
}