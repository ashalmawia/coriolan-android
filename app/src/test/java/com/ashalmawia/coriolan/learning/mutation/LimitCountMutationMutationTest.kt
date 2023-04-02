package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.data.logbook.MockLogbook
import com.ashalmawia.coriolan.data.prefs.MockPreferences
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.model.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LimitCountMutationMutationTest {

    private val preferences = MockPreferences()
    private val journal = MockLogbook()
    private val date = mockToday()
    private val cards = List(60) { i -> mockTask(procudeMockLearningProgress(i)) }

    private val mutation = lazy { LimitCountMutation(preferences, journal, date) }

    private fun procudeMockLearningProgress(i: Int) =
            when (i % 4) {
                0 -> mockLearningProgressNew()
                1 -> mockLearningProgressInProgress()
                2 -> mockLearningProgressRelearn()
                else -> mockLearningProgressLearnt()
            }

    @Test
    fun test__noLimits() {
        // when
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards, processed)
    }

    @Test
    fun test__noNew() {
        // given
        preferences.setNewCardsDailyLimitDefault(0)

        // when
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards.size - cards.count(Status.NEW), processed.size)
        assertEquals(0, processed.count(Status.NEW))
    }

    @Test
    fun test__noReview() {
        // given
        preferences.setReviewCardsDailyLimitDefault(0)

        // when
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards.count(Status.NEW, Status.RELEARN), processed.size)
        assertEquals(cards.filter(Status.NEW, Status.RELEARN), processed)
    }

    @Test
    fun test__allZero() {
        // given
        preferences.setNewCardsDailyLimitDefault(0)
        preferences.setReviewCardsDailyLimitDefault(0)

        // when
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards.count(Status.RELEARN), processed.size)
    }

    @Test
    fun test__limitOnlyNew() {
        // given
        preferences.setNewCardsDailyLimitDefault(3)

        // when
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards.count(Status.RELEARN, Status.IN_PROGRESS, Status.LEARNT) + 3, processed.size)
        assertEquals(3, processed.count(Status.NEW))
        assertEquals(cards.count(Status.RELEARN, Status.IN_PROGRESS, Status.LEARNT),
                processed.count(Status.RELEARN, Status.IN_PROGRESS, Status.LEARNT))
    }

    @Test
    fun test__limitOnlyReview() {
        // given
        preferences.setReviewCardsDailyLimitDefault(5)

        // when
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards.count(Status.NEW, Status.RELEARN) + 5, processed.size)
        assertEquals(5, processed.count(Status.IN_PROGRESS, Status.LEARNT))
        assertEquals(cards.count(Status.NEW, Status.RELEARN),
                processed.count(Status.NEW, Status.RELEARN))
    }

    @Test
    fun test__limitsTooHigh() {
        // given
        preferences.setNewCardsDailyLimitDefault(cards.size * 2)
        preferences.setReviewCardsDailyLimitDefault(cards.size * 2)

        // when
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards, processed)
    }

    @Test
    fun test__limitsEqual() {
        // given
        preferences.setNewCardsDailyLimitDefault(15)
        preferences.setReviewCardsDailyLimitDefault(15)

        // when
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(30 + cards.count(Status.RELEARN), processed.size)
        assertEquals(15, processed.count(Status.NEW))
        assertEquals(15, processed.count(Status.IN_PROGRESS, Status.LEARNT))
    }

    @Test
    fun test__realLimits__less() {
        // given
        preferences.setNewCardsDailyLimitDefault(5)
        preferences.setReviewCardsDailyLimitDefault(12)

        // when
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(5 + 12 + cards.count(Status.RELEARN), processed.size)
        assertEquals(5, processed.count(Status.NEW))
        assertEquals(12, processed.count(Status.IN_PROGRESS, Status.LEARNT))
    }

    @Test
    fun test__withJournal__allLearned() {
        // given
        journal.setTodayLearned(100, 100)
        preferences.setNewCardsDailyLimitDefault(20)
        preferences.setReviewCardsDailyLimitDefault(20)

        // when
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards.count(Status.RELEARN), processed.size)
    }

    @Test
    fun test__withJournal__allNewLearned() {
        // given
        journal.setTodayLearned(100, 0)
        preferences.setNewCardsDailyLimitDefault(20)
        preferences.setReviewCardsDailyLimitDefault(20)

        // when
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards.count(Status.RELEARN) + 20, processed.size)
        assertEquals(0, processed.count(Status.NEW))
        assertEquals(20, processed.count(Status.IN_PROGRESS, Status.LEARNT))
    }

    @Test
    fun test__withJournal__allReviewLearned() {
        // given
        journal.setTodayLearned(0, 100)
        preferences.setNewCardsDailyLimitDefault(5)
        preferences.setReviewCardsDailyLimitDefault(10)

        // when
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards.count(Status.RELEARN) + 5, processed.size)
        assertEquals(5, processed.count(Status.NEW))
        assertEquals(0, processed.count(Status.IN_PROGRESS, Status.LEARNT))
    }

    @Test
    fun test__withJournal__partlyLearned() {
        // given
        journal.setTodayLearned(5, 7)
        preferences.setNewCardsDailyLimitDefault(12)
        preferences.setReviewCardsDailyLimitDefault(10)

        // when
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards.count(Status.RELEARN) + (12 - 5) + (10 - 7), processed.size)
        assertEquals(12 - 5, processed.count(Status.NEW))
        assertEquals(10 - 7, processed.count(Status.IN_PROGRESS, Status.LEARNT))
    }
}

private fun List<Task>.filter(vararg statuses: Status): List<Task> {
    return filter { statuses.contains(it.learningProgress.globalStatus) }
}

private fun List<Task>.count(vararg statuses: Status): Int {
    return count { statuses.contains(it.learningProgress.globalStatus) }
}