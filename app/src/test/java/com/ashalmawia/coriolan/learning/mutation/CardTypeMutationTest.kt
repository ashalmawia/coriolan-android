package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.assignment.MockState
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.mockCardWithState
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CardTypeMutationTest {

    private val forwardCards = List(50) { i -> mockCardWithState(MockState(), id = i.toLong(), type = CardType.FORWARD) }
    private val reverseCards = List(50) { i -> mockCardWithState(MockState(), id = (50+i).toLong(), type = CardType.REVERSE) }

    private val cards = mixCards()

    private fun mixCards(): List<CardWithState<MockState>> {
        val result = mutableListOf<CardWithState<MockState>>()
        for (i in 0 until 50) {
            result.add(forwardCards[i])
            result.add(reverseCards[i])
        }
        return result
    }

    @Test
    fun testEmptyList() {
        // given
        val mutation = CardTypeMutation<MockState>(CardType.FORWARD)

        // when
        val filteredList = mutation.apply(listOf())

        // then
        assertEquals(0, filteredList.size)
    }

    @Test
    fun testForwardIsFiltered() {
        // given
        val mutation = CardTypeMutation<MockState>(CardType.FORWARD)

        // when
        val filteredList = mutation.apply(cards)

        // then
        assertEquals(forwardCards, filteredList)
    }

    @Test
    fun testReverseIsFiltered() {
        // given
        val mutation = CardTypeMutation<MockState>(CardType.REVERSE)

        // when
        val filteredList = mutation.apply(cards)

        // then
        assertEquals(reverseCards, filteredList)
    }

    @Test
    fun testUnexpectedSituation() {
        // given
        val mutation = CardTypeMutation<MockState>(CardType.UNKNOWN)

        // when
        val filteredList = mutation.apply(cards)

        // then
        assertEquals(0, filteredList.size)
    }
}