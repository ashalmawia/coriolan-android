package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.model.*
import junit.framework.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ShuffleMutationTest {

    private val cards = List(50) { i -> mockTask(id = i.toLong()) }

    @Test
    fun testNoShuffle() {
        // given
        val mutation = ShuffleMutation(false)

        // when
        val processed = mutation.apply(cards)

        // then
        assertEquals(cards, processed)
    }

    @Test
    fun testYesShuffle() {
        // given
        val mutation = ShuffleMutation(true)

        // when
        val processed = mutation.apply(cards)

        // then
        assertFalse(cards == processed)
        assertEquals(cards.sortedBy { it.card.id }, processed.sortedBy { it.card.id })
    }

    @Test
    fun testNewCardsInTheBeginning() {
        // given
        val mutation = ShuffleMutation(true)
        val cards = listOf(
                mockTask(mockLearningProgressNew()),
                mockTask(mockLearningProgressNew()),
                mockTask(mockLearningProgressInProgress()),
                mockTask(mockLearningProgressLearnt()),
                mockTask(mockLearningProgressInProgress()),
                mockTask(mockLearningProgressNew()),
                mockTask(mockLearningProgressNew())
        )

        // when
        val processed = mutation.apply(cards)

        // then
        assertFalse(cards == processed)
        assertEquals(cards.sortedBy { it.card.id }, processed.sortedBy { it.card.id })
        assertFalse(processed.subList(processed.size - processed.size / 3, processed.size)
                .any { it.learningProgress.spacedRepetition.status == Status.NEW })
    }
}