package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.exercise.sr.SRState
import com.ashalmawia.coriolan.model.*
import junit.framework.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SortReviewsByPeriodMutationTest {

    private val mutation = lazy { SortReviewsByPeriodMutation }

    @Test
    fun test__sortedAscending() {
        // when
        val cards = List(5, { i -> mockCardWithState(mockState(i - 1)) })
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards, processed)
    }

    @Test
    fun test__sortedDescending() {
        // when
        val cards = List(5, { i -> mockCardWithState(mockState(5 - i)) })
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards.reversed(), processed)
    }

    @Test
    fun test__mixed() {
        // when
        val cards = listOf(
                mockCardWithState(mockState(5)),
                mockCardWithState(mockState(-1)),
                mockCardWithState(mockState(10)),
                mockCardWithState(mockState(2)),
                mockCardWithState(mockState(0)),
                mockCardWithState(mockState(-1))
        )
        val processed = mutation.value.apply(cards)

        // then
        assertFalse(checkAscending(cards))
        assertTrue(checkAscending(processed))
    }

    private fun checkAscending(cards: List<CardWithState<SRState>>): Boolean {
        var previous = -1000
        cards.forEach {
            if (it.state.period < previous) return false
            previous = it.state.period
        }
        return true
    }
}