package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.model.mockCard
import junit.framework.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner

@RunWith(BlockJUnit4ClassRunner::class)
class ShuffleMutationTest {

    private val cards = List(50, { i -> mockCard(id = i.toLong()) })

    private lateinit var mutation: ShuffleMutation

    @Test
    fun testNoShuffle() {
        // given
        mutation = ShuffleMutation(false)

        // when
        val processed = mutation.apply(cards)

        // then
        assertEquals(cards, processed)
    }

    @Test
    fun testYesShuffle() {
        // given
        mutation = ShuffleMutation(true)

        // when
        val processed = mutation.apply(cards)

        // then
        assertFalse(cards == processed)
        assertEquals(cards.sortedBy { it.id }, processed.sortedBy { it.id })
    }
}