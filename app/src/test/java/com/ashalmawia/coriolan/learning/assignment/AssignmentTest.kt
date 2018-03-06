package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.mockCard
import org.joda.time.DateTime

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner

private const val MAGIC_COLLECTION_LENGTH = 4   // this magic number exactly meets the min amount of turns before rescheduled go
private val date = DateTime.now()

@RunWith(BlockJUnit4ClassRunner::class)
class AssignmentTest {
    
    private fun create(date: DateTime, cards: List<Card>): Assignment {
        return Assignment(date, cards)
    }

    @Test(expected = IllegalStateException::class)
    fun `assignmentTest__emptyCollection`() {
        // given
        val cards = listOf<Card>()
        val assignment = create(date, cards)

        // when
        assignment.next()

        // then - exception is thrown
    }

    @Test
    fun `assignmentTest__rescheduled`() {
        // given
        val map = mutableMapOf<Card, Int>()
        for (i in 0 until MAGIC_COLLECTION_LENGTH) {
            map.put(mockCard(), 0)
        }
        val cards = map.keys.toList()

        // when
        val assignment = create(date, cards)
        for (i in 0 until map.size) {
            assignment.next()
            assertNotNull("next card was selected as current", assignment.current)

            val current = assignment.current!!
            val countMet = map[current]
            assertEquals("the card was new", 0, countMet)

            map[current] = 1

            assignment.reschedule(current)
        }
        for (i in 0 until map.size) {
            assignment.next()
            assertNotNull("next card was selected as current", assignment.current)

            val current = assignment.current!!
            val countMet = map[current]
            assertEquals("the card was rescheduled", 1, countMet)
        }
    }

    @Test(expected = IllegalStateException::class)
    fun `assignmentTest__halfRescheduled`() {
        // given
        val map = mutableMapOf<Card, Boolean>()     // boolean means - to be rescheduled
        for (i in 0 until MAGIC_COLLECTION_LENGTH) {
            map.put(mockCard(), i % 2 == 0)
        }
        val cards = map.keys.toList()

        // when
        val assignment = create(date, cards)
        for (i in 0 until map.size) {
            assignment.next()
            assertNotNull("next card was selected as current", assignment.current)

            val current = assignment.current!!
            if (map[current]!!) {
                assignment.reschedule(current)
            }
        }
        for (i in 0 until map.size / 2) {
            assignment.next()
            assertNotNull("next card was selected as current", assignment.current)

            val current = assignment.current!!
            val rescheduled = map[current]!!
            assertTrue("correct cards were rescheduled", rescheduled)
        }

        assignment.next()       // exception must be thrown here
    }

    @Test
    fun `assignmentTest__rescheduledDoesNotAppearImmediately`() {
        // given
        val cards = listOf(mockCard(), mockCard())      // only 2 items
        var lastMet: Card? = null

        // when
        val assignment = create(date, cards)
        for (i in 0 until 50) {
            assignment.next()
            assertNotNull("next card was selected as current", assignment.current)

            val current = assignment.current!!
            assignment.reschedule(current)

            assertNotEquals("current card differs from the last met", lastMet, current)

            lastMet = current
        }
    }
}