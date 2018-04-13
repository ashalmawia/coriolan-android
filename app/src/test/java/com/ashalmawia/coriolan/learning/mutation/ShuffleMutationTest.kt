package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.assignment.MockState
import com.ashalmawia.coriolan.model.mockCardWithState
import junit.framework.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ShuffleMutationTest {

    private val cards = List(50, { i -> mockCardWithState(MockState(), id = i.toLong()) })

    private lateinit var mutation: Mutation.Shuffle

    @Test
    fun testNoShuffle() {
        // given
        mutation = Mutation.Shuffle(false)

        // when
        val processed = mutation.apply(cards)

        // then
        assertEquals(cards, processed)
    }

    @Test
    fun testYesShuffle() {
        // given
        mutation = Mutation.Shuffle(true)

        // when
        val processed = mutation.apply(cards)

        // then
        assertFalse(cards == processed)
        assertEquals(cards.sortedBy { it.card.id }, processed.sortedBy { it.card.id })
    }
}