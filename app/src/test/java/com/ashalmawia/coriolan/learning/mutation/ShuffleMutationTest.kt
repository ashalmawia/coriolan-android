package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.model.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ShuffleMutationTest {

    private val cards = List(50) { i -> mockCardWithProgress(id = i.toLong()) }

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
        assertEquals(cards.sortedBy { it.card.id.value }, processed.sortedBy { it.card.id.value })
    }

    @Test
    fun testNewCardsInTheBeginning() {
        // given
        val mutation = ShuffleMutation(true)
        val cards = listOf(
                mockCardWithProgress(mockLearningProgressNew()),
                mockCardWithProgress(mockLearningProgressNew()),
                mockCardWithProgress(mockLearningProgressInProgress()),
                mockCardWithProgress(mockLearningProgressLearnt()),
                mockCardWithProgress(mockLearningProgressInProgress()),
                mockCardWithProgress(mockLearningProgressNew()),
                mockCardWithProgress(mockLearningProgressNew())
        )

        // when
        val processed = mutation.apply(cards)

        // then
        assertFalse(cards == processed)
        assertEquals(cards.sortedBy { it.card.id.value }, processed.sortedBy { it.card.id.value })
        assertFalse(processed.subList(processed.size - processed.size / 3, processed.size)
                .any { it.learningProgress.status == Status.NEW })
    }
}