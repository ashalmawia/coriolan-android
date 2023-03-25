package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.model.mockState
import org.joda.time.DateTime

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

private const val MAGIC_COLLECTION_LENGTH = 4   // this magic number exactly meets the min amount of turns before rescheduled go
private val date = DateTime.now()

@RunWith(JUnit4::class)
class AssignmentTest {
    
    private fun create(date: DateTime, tasks: List<Task>): Assignment {
        return Assignment(date, MockHistoryFactory.create(), tasks)
    }

    @Test(expected = IllegalStateException::class)
    fun test__emptyCollection() {
        // given
        val tasks = listOf<Task>()
        val assignment = create(date, tasks)

        // when
        assignment.next()

        // then - exception is thrown
    }

    @Test
    fun test__rescheduled() {
        // given
        val map = mutableMapOf<Task, Int>()
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
    fun test__halfRescheduled() {
        // given
        val map = mutableMapOf<Task, Boolean>()     // boolean means - to be rescheduled
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
    fun test__rescheduledDoesNotAppearImmediately() {
        // given
        val cards = listOf(mockCard(), mockCard())      // only 2 items
        var lastMet: Task? = null

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

    @Test(expected = Exception::class)
    fun test__undo__empty() {
        // given
        val tasks = listOf<Task>()

        // when
        val assignment = create(date, tasks)

        // then
        assertFalse(assignment.canUndo())

        // exception expected
        assignment.undo()
    }

    @Test
    fun test__undo__forthAndBack() {
        // given
        val cards = (0 until 20).map {
            Task(com.ashalmawia.coriolan.model.mockCard(
                    front = "front $it", back = "back $it"
            ), mockState())
        }

        // when
        val assignment = create(date, cards)
        assertFalse(assignment.canUndo())
        val first = assignment.next()

        // then
        (0 until cards.size - 1).forEach {
            assertFalse(assignment.canUndo())
            val next = assignment.next()
            assertTrue(assignment.canUndo())
            val restored = assignment.undo()
            assertFalse(assignment.canUndo())
            assertEquals(first, restored)
            assertEquals(first, assignment.current)
        }

        assertFalse(assignment.canUndo())
    }

    @Test
    fun test__undo__fullQueue() {
        // given
        val cards = (0 until 20).map {
            Task(com.ashalmawia.coriolan.model.mockCard(
                    front = "front $it", back = "back $it"
            ), mockState())
        }

        // when
        val assignment = create(date, cards)
        assertFalse(assignment.canUndo())

        val list = mutableListOf<Task>()
        while (assignment.hasNext()) {
            list.add(assignment.next())
        }

        (list.size - 2 downTo 0).forEach {
            assertTrue(assignment.canUndo())
            val restored = assignment.undo()
            assertEquals(list[it], restored)
        }

        assertFalse(assignment.canUndo())
    }

    @Test
    fun test__undo__reschedule() {
        // given
        val cards = (1 .. 2).map {
            Task(com.ashalmawia.coriolan.model.mockCard(
                    front = "front $it", back = "back $it"
            ), mockState())
        }

        // when
        val assignment = create(date, cards)

        assertTrue(assignment.hasNext())

        var last = assignment.next()
        (1 .. 2).forEach {
            assignment.reschedule(last)
            last = assignment.next()
        }
        assertTrue(assignment.hasNext())        // reschedules

        assertTrue(assignment.canUndo())
        assignment.undo()
        assignment.undo()
        assertFalse(assignment.canUndo())

        assignment.next()

        assertTrue(assignment.canUndo())
        assertFalse(assignment.hasNext())
    }
}

private fun mockCard() = Task(com.ashalmawia.coriolan.model.mockCard(), mockState())