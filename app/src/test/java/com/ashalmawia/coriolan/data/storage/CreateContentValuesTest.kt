package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.storage.sqlite.*
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.exercise.sr.SRState
import com.ashalmawia.coriolan.model.Extras
import com.ashalmawia.coriolan.model.mockTerm
import com.ashalmawia.coriolan.model.mockLanguage
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
        assertEquals("$SQLITE_COLUMN_LANG_VALUE is correct", value, cv.get(SQLITE_COLUMN_LANG_VALUE))
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
        assertEquals("$SQLITE_COLUMN_ID is correct", id, cv.get(SQLITE_COLUMN_ID))
        assertEquals("$SQLITE_COLUMN_LANG_VALUE is correct", value, cv.get(SQLITE_COLUMN_LANG_VALUE))
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
        assertEquals("$SQLITE_COLUMN_VALUE is correct", value, cv.get(SQLITE_COLUMN_VALUE))
        assertEquals("$SQLITE_COLUMN_LANGUAGE_ID is correct", lang.id, cv.get(SQLITE_COLUMN_LANGUAGE_ID))
        assertEquals("$SQLITE_COLUMN_EXTRAS is correct", null, cv.get(SQLITE_COLUMN_EXTRAS))

        // when
        val id = 7L
        val cv1 = CreateContentValues.createTermContentValues(value, lang.id, null, id)

        // then
        assertEquals("values count is correct", 4, cv1.size())
        assertEquals("$SQLITE_COLUMN_ID is correct", id, cv1.get(SQLITE_COLUMN_ID))
        assertEquals("$SQLITE_COLUMN_VALUE is correct", value, cv1.get(SQLITE_COLUMN_VALUE))
        assertEquals("$SQLITE_COLUMN_LANGUAGE_ID is correct", lang.id, cv1.get(SQLITE_COLUMN_LANGUAGE_ID))
        assertEquals("$SQLITE_COLUMN_EXTRAS is correct", null, cv.get(SQLITE_COLUMN_EXTRAS))
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
        assertEquals("$SQLITE_COLUMN_VALUE is correct", value, cv.get(SQLITE_COLUMN_VALUE))
        assertEquals("$SQLITE_COLUMN_LANGUAGE_ID is correct", lang.id, cv.get(SQLITE_COLUMN_LANGUAGE_ID))
        assertEquals("$SQLITE_COLUMN_EXTRAS is correct",
                objectMapper.writeValueAsString(extras), cv.get(SQLITE_COLUMN_EXTRAS))

        // when
        val id = 7L
        val cv1 = CreateContentValues.createTermContentValues(value, lang.id, extras, id)

        // then
        assertEquals("values count is correct", 4, cv1.size())
        assertEquals("$SQLITE_COLUMN_ID is correct", id, cv1.get(SQLITE_COLUMN_ID))
        assertEquals("$SQLITE_COLUMN_VALUE is correct", value, cv1.get(SQLITE_COLUMN_VALUE))
        assertEquals("$SQLITE_COLUMN_LANGUAGE_ID is correct", lang.id, cv1.get(SQLITE_COLUMN_LANGUAGE_ID))
        assertEquals("$SQLITE_COLUMN_EXTRAS is correct",
                objectMapper.writeValueAsString(extras), cv.get(SQLITE_COLUMN_EXTRAS))
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
        assertEquals("$SQLITE_COLUMN_NAME is correct", name, cv.get(SQLITE_COLUMN_NAME))
        assertEquals("$SQLITE_COLUMN_LANG_ORIGINAL is correct", langOriginal.id, cv.get(SQLITE_COLUMN_LANG_ORIGINAL))
        assertEquals("$SQLITE_COLUMN_LANG_TRANSLATIONS is correct", langTranslations.id, cv.get(SQLITE_COLUMN_LANG_TRANSLATIONS))

        // when
        val id = 5L
        val cv1 = CreateContentValues.createDomainContentValues(name, langOriginal.id, langTranslations.id, id)

        // then
        assertEquals("values count is correct", 4, cv1.size())
        assertEquals("$SQLITE_COLUMN_ID is correct", id, cv1.get(SQLITE_COLUMN_ID))
        assertEquals("$SQLITE_COLUMN_NAME is correct", name, cv1.get(SQLITE_COLUMN_NAME))
        assertEquals("$SQLITE_COLUMN_LANG_ORIGINAL is correct", langOriginal.id, cv1.get(SQLITE_COLUMN_LANG_ORIGINAL))
        assertEquals("$SQLITE_COLUMN_LANG_TRANSLATIONS is correct", langTranslations.id, cv1.get(SQLITE_COLUMN_LANG_TRANSLATIONS))
    }

    @Test
    fun createCardContentValuesTest() {
        // given
        val deckId = 5L
        val domainId = 2L
        val lang = mockLanguage()
        val original = mockTerm("some original term", lang)

        // when
        val cv = CreateContentValues.createCardContentValues(domainId, deckId, original)

        // then
        assertEquals("values count is correct", 3, cv.size())
        assertEquals("$SQLITE_COLUMN_FRONT_ID is correct", original.id, cv.get(SQLITE_COLUMN_FRONT_ID))
        assertEquals("$SQLITE_COLUMN_DECK_ID is correct", deckId, cv.get(SQLITE_COLUMN_DECK_ID))
        assertEquals("$SQLITE_COLUMN_DOMAIN_ID is correct", domainId, cv.get(SQLITE_COLUMN_DOMAIN_ID))
    }

    @Test
    fun createCardContentValuesTest__hasCardId() {
        // given
        val deckId = 5L
        val domainId = 1L
        val lang = mockLanguage()
        val original = mockTerm("some original term", lang)
        val cardId = 7L

        // when
        val cv = CreateContentValues.createCardContentValues(domainId, deckId, original, cardId)

        // then
        assertEquals("values count is correct", 4, cv.size())
        assertEquals("$SQLITE_COLUMN_ID is correct", cardId, cv.get(SQLITE_COLUMN_ID))
        assertEquals("$SQLITE_COLUMN_FRONT_ID is correct", original.id, cv.get(SQLITE_COLUMN_FRONT_ID))
        assertEquals("$SQLITE_COLUMN_DECK_ID is correct", deckId, cv.get(SQLITE_COLUMN_DECK_ID))
        assertEquals("$SQLITE_COLUMN_DOMAIN_ID is correct", domainId, cv.get(SQLITE_COLUMN_DOMAIN_ID))

        // when
        val cv1 = CreateContentValues.createCardContentValues(domainId, deckId, original.id, cardId)

        // then
        assertEquals("values count is correct", 4, cv1.size())
        assertEquals("$SQLITE_COLUMN_ID is correct", cardId, cv1.get(SQLITE_COLUMN_ID))
        assertEquals("$SQLITE_COLUMN_FRONT_ID is correct", original.id, cv1.get(SQLITE_COLUMN_FRONT_ID))
        assertEquals("$SQLITE_COLUMN_DECK_ID is correct", deckId, cv1.get(SQLITE_COLUMN_DECK_ID))
        assertEquals("$SQLITE_COLUMN_DOMAIN_ID is correct", domainId, cv1.get(SQLITE_COLUMN_DOMAIN_ID))
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
        assertEquals("$SQLITE_COLUMN_NAME is correct", name, cv.get(SQLITE_COLUMN_NAME))
        assertEquals("$SQLITE_COLUMN_DOMAIN_ID is correct", domainId, cv.get(SQLITE_COLUMN_DOMAIN_ID))
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
        assertEquals("$SQLITE_COLUMN_ID is correct", deckId, cv.get(SQLITE_COLUMN_ID))
        assertEquals("$SQLITE_COLUMN_NAME is correct", name, cv.get(SQLITE_COLUMN_NAME))
        assertEquals("$SQLITE_COLUMN_DOMAIN_ID is correct", domainId, cv.get(SQLITE_COLUMN_DOMAIN_ID))
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
            assertEquals("$SQLITE_COLUMN_CARD_ID is correct", cardId, cv.get(SQLITE_COLUMN_CARD_ID))
            assertEquals("$SQLITE_COLUMN_TERM_ID is correct", translations[i].id, cv.get(SQLITE_COLUMN_TERM_ID))
        }

        // when
        val cvList1 = CreateContentValues.generateCardsReverseContentValues(cardId, translations.map { it.id })

        // then
        assertEquals("entries count is correct", 3, cvList1.size)
        for (i in 0 until 3) {
            val cv = cvList1[i]
            assertEquals("values count is correct", 2, cv.size())
            assertEquals("$SQLITE_COLUMN_CARD_ID is correct", cardId, cv.get(SQLITE_COLUMN_CARD_ID))
            assertEquals("$SQLITE_COLUMN_TERM_ID is correct", translations[i].id, cv.get(SQLITE_COLUMN_TERM_ID))
        }
    }

    @Test
    fun createStateContentValuesTest() {
        // given
        val cardId = 1L
        val due = DateTime(1519529781000)
        val period = 16
        val state = State(SRState(due, period))

        // when
        val cv = CreateContentValues.createCardStateContentValues(cardId, state)

        // then
        assertEquals("values count is correct", 3, cv.size())
        assertEquals("$SQLITE_COLUMN_CARD_ID is correct", cardId, cv.get(SQLITE_COLUMN_CARD_ID))
        assertEquals("$SQLITE_COLUMN_STATE_SR_DUE is correct", due, cv.getAsDate(SQLITE_COLUMN_STATE_SR_DUE))
        assertEquals("$SQLITE_COLUMN_STATE_SR_PERIOD is correct", period, cv.get(SQLITE_COLUMN_STATE_SR_PERIOD))

        // when
        val cv1 = CreateContentValues.createCardStateContentValues(cardId, state.spacedRepetition.due, state.spacedRepetition.period)

        // then
        assertEquals("values count is correct", 3, cv1.size())
        assertEquals("$SQLITE_COLUMN_CARD_ID is correct", cardId, cv1.get(SQLITE_COLUMN_CARD_ID))
        assertEquals("$SQLITE_COLUMN_STATE_SR_DUE is correct", due, cv1.getAsDate(SQLITE_COLUMN_STATE_SR_DUE))
        assertEquals("$SQLITE_COLUMN_STATE_SR_PERIOD is correct", period, cv1.get(SQLITE_COLUMN_STATE_SR_PERIOD))
    }
}