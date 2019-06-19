package com.ashalmawia.coriolan.data.prefs

import com.ashalmawia.coriolan.learning.today
import com.ashalmawia.coriolan.model.mockLanguage
import org.junit.Assert.*
import org.junit.Test

abstract class PreferencesTest {

    protected abstract fun create(): Preferences

    private val prefereces = lazy { create() }

    @Test
    fun `test isFirstStart recordFirstStart`() {
        // given
        val preferences = this.prefereces.value

        // then
        assertTrue(preferences.isFirstStart())
        assertTrue(preferences.isFirstStart())

        // when
        preferences.recordFirstStart()

        // then
        assertFalse(preferences.isFirstStart())
        assertFalse(preferences.isFirstStart())
    }

    @Test
    fun `test newCardsLimit default`() {
        // given
        val preferences = this.prefereces.value
        val newCardsLimit = 8

        // then
        assertNull(preferences.getNewCardsDailyLimitDefault())
        assertNull(preferences.getNewCardsDailyLimitDefault())

        // when
        preferences.setReviewCardsDailyLimitDefault(5)

        // then
        assertNull(preferences.getNewCardsDailyLimitDefault())

        // when
        preferences.setNewCardsDailyLimitDefault(newCardsLimit)

        // then
        assertEquals(newCardsLimit, preferences.getNewCardsDailyLimitDefault())
        assertEquals(newCardsLimit, preferences.getNewCardsDailyLimitDefault())

        // when
        preferences.clearNewCardsDailyLimit()

        // then
        assertNull(preferences.getNewCardsDailyLimitDefault())
        assertNull(preferences.getNewCardsDailyLimitDefault())
    }

    @Test
    fun `test newCardsLimit`() {
        // given
        val preferences = this.prefereces.value
        val newCardsLimit = 8
        val newCardsLimitToday = 20
        val today = today()

        // when
        preferences.setNewCardsDailyLimitDefault(newCardsLimit)

        // then
        assertEquals(newCardsLimit, preferences.getNewCardsDailyLimitDefault())
        assertEquals(newCardsLimit, preferences.getNewCardsDailyLimit(today))

        // when
        preferences.setNewCardsDailyLimit(newCardsLimitToday, today)

        // then
        assertEquals(newCardsLimitToday, preferences.getNewCardsDailyLimit(today))
        assertEquals(newCardsLimit, preferences.getNewCardsDailyLimit(today.minusDays(1)))
        assertEquals(newCardsLimit, preferences.getNewCardsDailyLimit(today.plusDays(1)))

        // when
        preferences.clearNewCardsDailyLimit()

        // then
        assertNull(preferences.getNewCardsDailyLimit(today))
    }

    @Test
    fun `test reviewCardsLimit default`() {
        // given
        val preferences = this.prefereces.value
        val reviewCardsLimit = 6

        // then
        assertNull(preferences.getReviewCardsDailyLimitDefault())
        assertNull(preferences.getReviewCardsDailyLimitDefault())

        // when
        preferences.setNewCardsDailyLimitDefault(3)

        // then
        assertNull(preferences.getReviewCardsDailyLimitDefault())

        // when
        preferences.setReviewCardsDailyLimitDefault(reviewCardsLimit)

        // then
        assertEquals(reviewCardsLimit, preferences.getReviewCardsDailyLimitDefault())
        assertEquals(reviewCardsLimit, preferences.getReviewCardsDailyLimitDefault())

        // when
        preferences.clearReviewCardsDailyLimit()

        // then
        assertNull(preferences.getReviewCardsDailyLimitDefault())
        assertNull(preferences.getReviewCardsDailyLimitDefault())
    }

    @Test
    fun `test reviewCardsLimit`() {
        // given
        val preferences = this.prefereces.value
        val reviewCardsLimit = 8
        val reviewCardsLimitToday = 20
        val today = today()

        // when
        preferences.setReviewCardsDailyLimitDefault(reviewCardsLimit)

        // then
        assertEquals(reviewCardsLimit, preferences.getReviewCardsDailyLimitDefault())
        assertEquals(reviewCardsLimit, preferences.getReviewCardsDailyLimit(today))

        // when
        preferences.setReviewCardsDailyLimit(reviewCardsLimitToday, today)

        // then
        assertEquals(reviewCardsLimitToday, preferences.getReviewCardsDailyLimit(today))
        assertEquals(reviewCardsLimit, preferences.getReviewCardsDailyLimit(today.minusDays(1)))
        assertEquals(reviewCardsLimit, preferences.getReviewCardsDailyLimit(today.plusDays(1)))

        // when
        preferences.clearReviewCardsDailyLimit()

        // then
        assertNull(preferences.getReviewCardsDailyLimit(today))
    }

    @Test
    fun `test cardTypePreference`() {
        // given
        val preferences = this.prefereces.value
        val cardType = CardTypePreference.REVERSE_ONLY

        // then
        assertNull(preferences.getCardTypePreference())
        assertNull(preferences.getCardTypePreference())

        // when
        preferences.setCardTypePreference(cardType)

        // then
        assertEquals(cardType, preferences.getCardTypePreference())
        assertEquals(cardType, preferences.getCardTypePreference())
    }

    @Test
    fun `test lastTranslationsLanguageId`() {
        // given
        val preferences = this.prefereces.value
        val lastTranslationsLanguage = mockLanguage(id = 2L)

        // then
        assertNull(preferences.getLastTranslationsLanguageId())

        // when
        preferences.setLastTranslationsLanguageId(lastTranslationsLanguage)

        // then
        assertEquals(lastTranslationsLanguage.id, preferences.getLastTranslationsLanguageId())

        // when
        preferences.clearLastTranslationsLanguageId()

        // then
        assertNull(preferences.getLastTranslationsLanguageId())
    }
}