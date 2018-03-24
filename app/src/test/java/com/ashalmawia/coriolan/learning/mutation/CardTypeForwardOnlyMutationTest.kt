package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.assignment.MockState
import com.ashalmawia.coriolan.learning.scheduler.CardWithState
import com.ashalmawia.coriolan.model.mockForwardCardWithState
import com.ashalmawia.coriolan.model.mockReverseCardWithState
import com.ashalmawia.coriolan.util.forwardWithState
import com.ashalmawia.coriolan.util.reverseWithState
import junit.framework.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CardTypeForwardOnlyMutationTest {

    private val mutation = CardTypeForwardOnlyMutation()

    @Test
    fun `test__empty`() {
        // given
        val cards = emptyList<CardWithState<MockState>>()

        // when
        val processed = mutation.apply(cards)

        // then
        assertTrue(processed.isEmpty())
    }

    @Test
    fun `test__forwardOnly`() {
        // given
        val cards = (0..10).map { mockForwardCardWithState() }

        // when
        val processed = mutation.apply(cards)

        // then
        assertEquals(cards, processed)
    }

    @Test
    fun `test__reverseOnly`() {
        // given
        val cards = (0..10).map { mockReverseCardWithState() }

        // when
        val processed = mutation.apply(cards)

        // then
        assertTrue(processed.isEmpty())
    }

    @Test
    fun `test__mixed`() {
        // given
        val cards = (0 until 20).mapIndexed { i, _ -> if (i % 2 == 0) mockForwardCardWithState() else mockReverseCardWithState() }

        // when
        val processed = mutation.apply(cards)

        // then
        assertEquals(cards.size / 2, processed.size)
        assertEquals(processed.size, processed.forwardWithState().size)
        assertTrue(processed.reverseWithState().isEmpty())
    }
}