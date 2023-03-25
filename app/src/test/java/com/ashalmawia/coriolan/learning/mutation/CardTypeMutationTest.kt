package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.mockTask
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CardTypeMutationTest {

    private val forwardCards = List(50) { i -> mockTask(id = i.toLong(), type = CardType.FORWARD) }
    private val reverseCards = List(50) { i -> mockTask(id = (50+i).toLong(), type = CardType.REVERSE) }

    private val cards = mixTasks()

    private fun mixTasks(): List<Task> {
        val result = mutableListOf<Task>()
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

    @Test
    fun testUnexpectedSituation() {
        // given
        val mutation = CardTypeMutation(CardType.UNKNOWN)

        // when
        val filteredList = mutation.apply(cards)

        // then
        assertEquals(0, filteredList.size)
    }
}