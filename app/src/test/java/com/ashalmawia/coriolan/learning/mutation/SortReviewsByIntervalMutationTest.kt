package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.model.*
import junit.framework.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SortReviewsByIntervalMutationTest {

    private val mutation = lazy { SortReviewsByIntervalMutation }

    @Test
    fun test__sortedAscending() {
        // when
        val cards = List(5) { i -> mockTask(mockLearningProgress(interval = i - 1)) }
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards, processed)
    }

    @Test
    fun test__sortedDescending() {
        // when
        val cards = List(5) { i -> mockTask(mockLearningProgress(interval = 5 - i)) }
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards.reversed(), processed)
    }

    @Test
    fun test__mixed() {
        // when
        val cards = listOf(
                mockTask(mockLearningProgress(interval = 5)),
                mockTask(mockLearningProgress(interval = -1)),
                mockTask(mockLearningProgress(interval = 10)),
                mockTask(mockLearningProgress(interval = 2)),
                mockTask(mockLearningProgress(interval = 0)),
                mockTask(mockLearningProgress(interval = -1))
        )
        val processed = mutation.value.apply(cards)

        // then
        assertFalse(checkAscending(cards))
        assertTrue(checkAscending(processed))
    }

    private fun checkAscending(tasks: List<Task>): Boolean {
        var previous = -1000
        tasks.forEach {
            if (it.learningProgress.mock.interval < previous) return false
            previous = it.learningProgress.mock.interval
        }
        return true
    }
}