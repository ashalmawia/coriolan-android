package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.mockForwardCard
import com.ashalmawia.coriolan.model.mockReverseCard
import com.ashalmawia.coriolan.util.forward
import com.ashalmawia.coriolan.util.reverse
import junit.framework.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner

@RunWith(BlockJUnit4ClassRunner::class)
class CardTypeForwardFirstMutationTest {

    private val mutation = CardTypeForwardFirstMutation()

    @Test
    fun `test__empty`() {
        // given
        val cards = emptyList<Card>()

        // when
        val processed = mutation.apply(cards)

        // then
        assertTrue(processed.isEmpty())
    }

    @Test
    fun `test__forwardOnly`() {
        // given
        val cards = (0..10).map { mockForwardCard() }

        // when
        val processed = mutation.apply(cards)

        // then
        assertEquals(cards, processed)
    }

    @Test
    fun `test__reverseOnly`() {
        // given
        val cards = (0..10).map { mockReverseCard() }

        // when
        val processed = mutation.apply(cards)

        // then
        assertEquals(cards, processed)
    }

    @Test
    fun `test__mixed`() {
        // given
        val cards = (0 until 20).mapIndexed { i, _ -> if (i % 2 == 0) mockForwardCard() else mockReverseCard() }

        // when
        val processed = mutation.apply(cards)

        // then
        assertEquals(cards.size, processed.size)
        assertEquals(cards.forward(), processed.subList(0, cards.size / 2))
        assertEquals(cards.reverse(), processed.subList(cards.size / 2, cards.size))
    }
}