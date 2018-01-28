package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.mockCard
import org.junit.Test

import org.junit.Assert.*
import java.util.*

private const val MAGIC_COLLECTION_LENGTH = 4   // this magic number exactly meets the min amount of turns before rescheduled go
private val date = Date()

fun `assignmentTest__emptyCollection`(factory: AssignmentCreator) {
    // given
    val cards = listOf<Card>()
    val assignment = factory.create(date, cards)

    // when
    assignment.next()

    // then - exception is thrown
}

fun `assignmentTest__isSequential`(factory: AssignmentCreator, expectedSequential: Boolean) {
    // given
    val map = mutableMapOf<Card, Int>()
    for (i in 0 until 100) {
        map.put(mockCard(), i)
    }
    val cards = map.keys.toList()

    // when
    val assignment = factory.create(date, cards)
    var isSequential = true
    for (i in 0 until map.size) {
        assignment.next()

        assertNotNull("next card was selected as current", assignment.current)

        val index = map[assignment.current]
        if (index != i) {
            isSequential = false
        }
    }

    // then
    assertEquals("expected sequential[$expectedSequential] met", expectedSequential, isSequential)
}

fun `assignmentTest__rescheduled`(factory: AssignmentCreator) {
    // given
    val map = mutableMapOf<Card, Int>()
    for (i in 0 until MAGIC_COLLECTION_LENGTH) {
        map.put(mockCard(), 0)
    }
    val cards = map.keys.toList()

    // when
    val assignment = factory.create(date, cards)
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

fun `assignmentTest__halfRescheduled`(factory: AssignmentCreator) {
    // given
    val map = mutableMapOf<Card, Boolean>()     // boolean means - to be rescheduled
    for (i in 0 until MAGIC_COLLECTION_LENGTH) {
        map.put(mockCard(), i % 2 == 0)
    }
    val cards = map.keys.toList()

    // when
    val assignment = factory.create(date, cards)
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

fun `assignmentTest__rescheduledDoesNotAppearImmediately`(factory: AssignmentCreator) {
    // given
    val cards = listOf(mockCard(), mockCard())      // only 2 items
    var lastMet: Card? = null

    // when
    val assignment = factory.create(date, cards)
    for (i in 0 until 50) {
        assignment.next()
        assertNotNull("next card was selected as current", assignment.current)

        val current = assignment.current!!
        assignment.reschedule(current)

        assertNotEquals("current card differs from the last met", lastMet, current)

        lastMet = current
    }
}

interface AssignmentCreator {
    fun create(date: Date, cards: List<Card>): Assignment
}