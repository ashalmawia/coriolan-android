package com.ashalmawia.coriolan

import com.ashalmawia.coriolan.data.prefs.CardTypePreference
import com.ashalmawia.coriolan.data.prefs.MockPreferences
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner

@RunWith(BlockJUnit4ClassRunner::class)
class FirstStartTest {

    private val preferences = MockPreferences()

    @Test
    fun testFirstStart() {
        // given
        assertNull(preferences.getNewCardsDailyLimit())
        assertNull(preferences.getReviewCardsDailyLimit())
        assertTrue(preferences.isFirstStart())

        // when
        FirstStart.preinitializeIfFirstStart(preferences)

        // then
        assertFalse(preferences.isFirstStart())
        assertEquals(15, preferences.getNewCardsDailyLimit())
        assertEquals(30, preferences.getReviewCardsDailyLimit())
        assertEquals(CardTypePreference.FORWARD_FIRST, preferences.getCardTypePreference())
    }

    @Test
    fun testNotFirstStart() {
        // given
        preferences.recordFirstStart()
        assertFalse(preferences.isFirstStart())

        preferences.setReviewCardsDailyLimit(5)
        preferences.setCardTypePreference(CardTypePreference.REVERSE_ONLY)

        // when
        FirstStart.preinitializeIfFirstStart(preferences)

        // then
        assertFalse(preferences.isFirstStart())
        assertNull(preferences.getNewCardsDailyLimit())
        assertEquals(5, preferences.getReviewCardsDailyLimit())
        assertEquals(CardTypePreference.REVERSE_ONLY, preferences.getCardTypePreference())
    }

}