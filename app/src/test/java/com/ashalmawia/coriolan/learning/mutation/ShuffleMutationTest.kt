package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.assignment.MockState
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.exercise.sr.SRState
import com.ashalmawia.coriolan.model.*
import junit.framework.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ShuffleMutationTest {

    private val cards = List(50, { i -> mockCardWithState(MockState(), id = i.toLong()) })

    @Test
    fun testNoShuffle() {
        // given
        val mutation = ShuffleMutation<MockState>(false)

        // when
        val processed = mutation.apply(cards)

        // then
        assertEquals(cards, processed)
    }

    @Test
    fun testYesShuffle() {
        // given
        val mutation = ShuffleMutation<MockState>(true)

        // when
        val processed = mutation.apply(cards)

        // then
        assertFalse(cards == processed)
        assertEquals(cards.sortedBy { it.card.id }, processed.sortedBy { it.card.id })
    }

    @Test
    fun testNewCardsInTheBeginning() {
        // given
        val mutation = ShuffleMutation<SRState>(true)
        val cards = listOf(
                mockCardWithState(mockStateNew()),
                mockCardWithState(mockStateNew()),
                mockCardWithState(mockStateInProgress()),
                mockCardWithState(mockStateLearnt()),
                mockCardWithState(mockStateInProgress()),
                mockCardWithState(mockStateNew()),
                mockCardWithState(mockStateNew())
        )

        // when
        val processed = mutation.apply(cards)

        // then
        assertFalse(cards == processed)
        assertEquals(cards.sortedBy { it.card.id }, processed.sortedBy { it.card.id })
        assertFalse(processed.subList(processed.size - processed.size / 3, processed.size)
                .any { it.state.status == Status.NEW })
    }
}