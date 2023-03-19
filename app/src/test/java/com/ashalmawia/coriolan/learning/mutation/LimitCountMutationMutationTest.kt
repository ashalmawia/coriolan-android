package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.data.journal.MockJournal
import com.ashalmawia.coriolan.data.prefs.MockPreferences
import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.model.*
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LimitCountMutationMutationTest {

    private val preferences = MockPreferences()
    private val journal = MockJournal()
    private val date = mockToday()
    private val cards = List(60, { i -> mockCardWithState(procudeMockState(i)) })

    private val mutation = lazy { LimitCountMutation(preferences, journal, date) }

    private fun procudeMockState(i: Int) =
            when (i % 4) {
                0 -> mockStateNew()
                1 -> mockStateInProgress()
                2 -> mockStateRelearn()
                else -> mockStateLearnt()
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
        journal.setTodayLearned(100, 100, date)
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
        journal.setTodayLearned(100, 0, date)
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
        journal.setTodayLearned(0, 100, date)
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
        journal.setTodayLearned(5, 7, date)
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

private fun List<CardWithState>.filter(vararg statuses: Status): List<CardWithState> {
    return filter { statuses.contains(it.state.spacedRepetition.status) }
}

private fun List<CardWithState>.count(vararg statuses: Status): Int {
    return count { statuses.contains(it.state.spacedRepetition.status) }
}