package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithProgress
import com.ashalmawia.coriolan.model.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SortReviewsByIntervalMutationTest {

    private val mutation = lazy { SortReviewsByIntervalMutation }

    @Test
    fun test__sortedAscending() {
        // when
        val cards = List(5) { i -> mockCardWithInterval(i - 1) }
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards, processed)
    }

    private fun mockCardWithInterval(interval: Int) =
            mockCardWithProgress(mockLearningProgress(interval = interval - 1))

    @Test
    fun test__sortedDescending() {
        // when
        val cards = List(5) { i -> mockCardWithInterval(interval = 5 - i) }
        val processed = mutation.value.apply(cards)

        // then
        assertEquals(cards.reversed(), processed)
    }

    @Test
    fun test__mixed() {
        // when
        val cards = listOf(
                mockCardWithInterval(interval = 5),
                mockCardWithInterval(interval = -1),
                mockCardWithInterval(interval = 10),
                mockCardWithInterval(interval = 2),
                mockCardWithInterval(interval = 0),
                mockCardWithInterval(interval = -1)
        )
        val processed = mutation.value.apply(cards)

        // then
        assertFalse(checkAscending(cards))
        assertTrue(checkAscending(processed))
    }

    private fun checkAscending(tasks: List<CardWithProgress>): Boolean {
        var previous = -1000
        tasks.forEach {
            if (it.learningProgress.state.interval < previous) return false
            previous = it.learningProgress.state.interval
        }
        return true
    }
}