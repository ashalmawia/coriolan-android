package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.model.Card
import org.joda.time.DateTime
import org.junit.Test
import java.util.*

import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner

private const val EXPECTED_SEQUENTIAL = false

@RunWith(BlockJUnit4ClassRunner::class)
class RandomAssignmentTest {

    private val factory = object : AssignmentCreator {
        override fun create(date: DateTime, cards: List<Card>): Assignment {
            return RandomAssignment(date, cards)
        }
    }

    @Test(expected = IllegalStateException::class)
    fun `test__emptyCollection`() {
        `assignmentTest__emptyCollection`(factory)
    }

    @Test
    fun `test__isSequential`() {
        `assignmentTest__isSequential`(factory, EXPECTED_SEQUENTIAL)
    }

    @Test
    fun `test__rescheduled`() {
        `assignmentTest__rescheduled`(factory)
    }

    @Test(expected = IllegalStateException::class)
    fun `test__halfRescheduled`() {
        `assignmentTest__halfRescheduled`(factory)
    }

    @Test
    fun `test__rescheduledDoesNotAppearImmediately`() {
        `assignmentTest__rescheduledDoesNotAppearImmediately`(factory)
    }
}