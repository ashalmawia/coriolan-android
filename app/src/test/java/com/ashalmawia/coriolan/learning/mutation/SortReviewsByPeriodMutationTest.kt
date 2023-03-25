package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.Task
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
        val cards = List(5) { i -> mockTask(mockState(i - 1)) }
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards, processed)
    }

    @Test
    fun test__sortedDescending() {
        // when
        val cards = List(5, { i -> mockTask(mockState(5 - i)) })
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards.reversed(), processed)
    }

    @Test
    fun test__mixed() {
        // when
        val cards = listOf(
                mockTask(mockState(5)),
                mockTask(mockState(-1)),
                mockTask(mockState(10)),
                mockTask(mockState(2)),
                mockTask(mockState(0)),
                mockTask(mockState(-1))
        )
        val processed = mutation.value.apply(cards)

        // then
        assertFalse(checkAscending(cards))
        assertTrue(checkAscending(processed))
    }

    private fun checkAscending(tasks: List<Task>): Boolean {
        var previous = -1000
        tasks.forEach {
            if (it.state.spacedRepetition.period < previous) return false
            previous = it.state.spacedRepetition.period
        }
        return true
    }
}