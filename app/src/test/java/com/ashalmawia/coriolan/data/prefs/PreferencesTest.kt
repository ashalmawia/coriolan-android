package com.ashalmawia.coriolan.data.prefs

import org.junit.Assert.*
import org.junit.Test

abstract class PreferencesTest {

    protected abstract fun create(): Preferences

    private val prefereces = lazy { create() }

    @Test
    fun `test__isFirstStart__recordFirstStart`() {
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
    fun `test__newCardsLimit`() {
        // given
        val preferences = this.prefereces.value
        val newCardsLimit = 8

        // then
        assertNull(preferences.getNewCardsDailyLimit())
        assertNull(preferences.getNewCardsDailyLimit())

        // when
        preferences.setReviewCardsDailyLimit(5)

        // then
        assertNull(preferences.getNewCardsDailyLimit())

        // when
        preferences.setNewCardsDailyLimit(newCardsLimit)

        // then
        assertEquals(newCardsLimit, preferences.getNewCardsDailyLimit())
        assertEquals(newCardsLimit, preferences.getNewCardsDailyLimit())

        // when
        preferences.clearNewCardsDailyLimit()

        // then
        assertNull(preferences.getNewCardsDailyLimit())
        assertNull(preferences.getNewCardsDailyLimit())
    }

    @Test
    fun `test__reviewCardsLimit`() {
        // given
        val preferences = this.prefereces.value
        val reviewCardsLimit = 6

        // then
        assertNull(preferences.getReviewCardsDailyLimit())
        assertNull(preferences.getReviewCardsDailyLimit())

        // when
        preferences.setNewCardsDailyLimit(3)

        // then
        assertNull(preferences.getReviewCardsDailyLimit())

        // when
        preferences.setReviewCardsDailyLimit(reviewCardsLimit)

        // then
        assertEquals(reviewCardsLimit, preferences.getReviewCardsDailyLimit())
        assertEquals(reviewCardsLimit, preferences.getReviewCardsDailyLimit())

        // when
        preferences.clearReviewCardsDailyLimit()

        // then
        assertNull(preferences.getReviewCardsDailyLimit())
        assertNull(preferences.getReviewCardsDailyLimit())
    }

    @Test
    fun `test__cardTypePreference`() {
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
}