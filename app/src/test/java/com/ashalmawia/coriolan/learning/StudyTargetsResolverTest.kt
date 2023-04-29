package com.ashalmawia.coriolan.learning

import com.ashalmawia.coriolan.data.logbook.MockLogbook
import com.ashalmawia.coriolan.data.prefs.MockPreferences
import com.ashalmawia.coriolan.model.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class StudyTargetsResolverTest {

    private val preferences = MockPreferences()
    private val logbook = MockLogbook()
    private val date = mockToday()

    private val resolver by lazy { StudyTargetsResolverImpl(preferences, logbook) }

    @Test
    fun test__noLimits() {
        // when
        val targets = resolver.defaultStudyTargets(date)

        // then
        assertEquals(StudyTargets(null, null), targets)
        assertTrue(targets.unlimited())
    }

    @Test
    fun test__noNew() {
        // given
        preferences.setNewCardsDailyLimitDefault(0)

        // when
        val targets = resolver.defaultStudyTargets(date)

        // then
        assertEquals(StudyTargets(0, null), targets)
        assertFalse(targets.unlimited())
    }

    @Test
    fun test__noReview() {
        // given
        preferences.setReviewCardsDailyLimitDefault(0)

        // when
        val targets = resolver.defaultStudyTargets(date)

        // then
        assertEquals(StudyTargets(null, 0), targets)
        assertFalse(targets.unlimited())
    }

    @Test
    fun test__allZero() {
        // given
        preferences.setNewCardsDailyLimitDefault(0)
        preferences.setReviewCardsDailyLimitDefault(0)

        // when
        val targets = resolver.defaultStudyTargets(date)

        // then
        assertEquals(StudyTargets(0, 0), targets)
        assertFalse(targets.unlimited())
    }

    @Test
    fun test__limitOnlyNew() {
        // given
        preferences.setNewCardsDailyLimitDefault(3)

        // when
        val targets = resolver.defaultStudyTargets(date)

        // then
        assertEquals(StudyTargets(3, null), targets)
        assertFalse(targets.unlimited())
    }

    @Test
    fun test__limitOnlyReview() {
        // given
        preferences.setReviewCardsDailyLimitDefault(5)

        // when
        val targets = resolver.defaultStudyTargets(date)

        // then
        assertEquals(StudyTargets(null, 5), targets)
        assertFalse(targets.unlimited())
    }

    @Test
    fun test__withJournal__allLearned() {
        // given
        logbook.setTodayLearned(100, 100)
        preferences.setNewCardsDailyLimitDefault(20)
        preferences.setReviewCardsDailyLimitDefault(20)

        // when
        val targets = resolver.defaultStudyTargets(date)

        // then
        assertEquals(StudyTargets(0, 0), targets)
        assertFalse(targets.unlimited())
    }

    @Test
    fun test__withJournal__allNewLearned() {
        // given
        logbook.setTodayLearned(100, 0)
        preferences.setNewCardsDailyLimitDefault(20)
        preferences.setReviewCardsDailyLimitDefault(20)

        // when
        val targets = resolver.defaultStudyTargets(date)

        // then
        assertEquals(StudyTargets(0, 20), targets)
        assertFalse(targets.unlimited())
    }

    @Test
    fun test__withJournal__allReviewLearned() {
        // given
        logbook.setTodayLearned(0, 100)
        preferences.setNewCardsDailyLimitDefault(5)
        preferences.setReviewCardsDailyLimitDefault(10)

        // when
        val targets = resolver.defaultStudyTargets(date)

        // then
        assertEquals(StudyTargets(5, 0), targets)
        assertFalse(targets.unlimited())
    }

    @Test
    fun test__withJournal__partlyLearned() {
        // given
        logbook.setTodayLearned(5, 7)
        preferences.setNewCardsDailyLimitDefault(12)
        preferences.setReviewCardsDailyLimitDefault(10)

        // when
        val targets = resolver.defaultStudyTargets(date)

        // then
        assertEquals(StudyTargets(7, 3), targets)
        assertFalse(targets.unlimited())
    }
}

