package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithProgress
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.mockCardWithProgress
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CardTypeMutationTest {

    private val forwardCards = List(50) { i -> mockCardWithProgress(id = i.toLong(), type = CardType.FORWARD) }
    private val reverseCards = List(50) { i -> mockCardWithProgress(id = (50+i).toLong(), type = CardType.REVERSE) }

    private val cards = testData()

    private fun testData(): List<CardWithProgress> {
        val result = mutableListOf<CardWithProgress>()
        for (i in 0 until 50) {
            result.add(forwardCards[i])
            result.add(reverseCards[i])
        }
        return result
    }

    @Test
    fun testEmptyList() {
        // given
        val mutation = CardTypeMutation(CardType.FORWARD)

        // when
        val filteredList = mutation.apply(listOf())

        // then
        assertEquals(0, filteredList.size)
    }

    @Test
    fun testForwardIsFiltered() {
        // given
        val mutation = CardTypeMutation(CardType.FORWARD)

        // when
        val filteredList = mutation.apply(cards)

        // then
        assertEquals(forwardCards, filteredList)
    }

    @Test
    fun testReverseIsFiltered() {
        // given
        val mutation = CardTypeMutation(CardType.REVERSE)

        // when
        val filteredList = mutation.apply(cards)

        // then
        assertEquals(reverseCards, filteredList)
    }
}