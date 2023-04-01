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
        val cards = List(5) { i -> mockTask(mockLearningProgress(period = i - 1)) }
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards, processed)
    }

    @Test
    fun test__sortedDescending() {
        // when
        val cards = List(5) { i -> mockTask(mockLearningProgress(period = 5 - i)) }
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards.reversed(), processed)
    }

    @Test
    fun test__mixed() {
        // when
        val cards = listOf(
                mockTask(mockLearningProgress(period = 5)),
                mockTask(mockLearningProgress(period = -1)),
                mockTask(mockLearningProgress(period = 10)),
                mockTask(mockLearningProgress(period = 2)),
                mockTask(mockLearningProgress(period = 0)),
                mockTask(mockLearningProgress(period = -1))
        )
        val processed = mutation.value.apply(cards)

        // then
        assertFalse(checkAscending(cards))
        assertTrue(checkAscending(processed))
    }

    private fun checkAscending(tasks: List<Task>): Boolean {
        var previous = -1000
        tasks.forEach {
            if (it.learningProgress.spacedRepetition.period < previous) return false
            previous = it.learningProgress.spacedRepetition.period
        }
        return true
    }
}