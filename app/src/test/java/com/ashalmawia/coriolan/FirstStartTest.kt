package com.ashalmawia.coriolan

import com.ashalmawia.coriolan.data.prefs.MockPreferences
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FirstStartTest {

    private val preferences = MockPreferences()
    private val firstStart = FirstStartImpl(preferences)

    @Test
    fun testFirstStart() {
        // given
        assertNull(preferences.getNewCardsDailyLimitDefault())
        assertNull(preferences.getReviewCardsDailyLimitDefault())
        assertTrue(preferences.isFirstStart())

        // when
        firstStart.runFirstStartRoutine()

        // then
        assertFalse(preferences.isFirstStart())
        assertEquals(15, preferences.getNewCardsDailyLimitDefault())
        assertEquals(30, preferences.getReviewCardsDailyLimitDefault())
    }

    @Test
    fun testNotFirstStart() {
        // given
        preferences.recordFirstStart()
        assertFalse(preferences.isFirstStart())

        preferences.setReviewCardsDailyLimitDefault(5)

        // when
        firstStart.runFirstStartRoutine()

        // then
        assertFalse(preferences.isFirstStart())
        assertNull(preferences.getNewCardsDailyLimitDefault())
        assertEquals(5, preferences.getReviewCardsDailyLimitDefault())
    }

}