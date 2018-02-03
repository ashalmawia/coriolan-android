package com.ashalmawia.coriolan.learning.scheduler

import com.ashalmawia.coriolan.learning.assignment.MockAssignment
import com.ashalmawia.coriolan.model.mockCard
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner

@RunWith(BlockJUnit4ClassRunner::class)
class SpacedRepetitionSchedulerTest {

    private fun assignment(): MockAssignment {
        return MockAssignment(listOf())
    }

    private fun scheduler() = SpacedRepetitionScheduler()

    @Test
    fun `test__newCard__wrong`() {
        // given
        val scheduler = scheduler()

        val assignment = assignment()

        val state = emptyState()
        assertEquals(Status.NEW, state.status)       // test requirement, update if needed

        assignment.mockCurrent(mockCard(state))
        val card = assignment.current!!

        // when
        val updated = scheduler.wrong(card.state)

        // then
        val today = today()
        assertEquals(today, updated.due)
        assertEquals(0, updated.period)
    }

    @Test
    fun `test__newCard__correct`() {
        // given
        val scheduler = scheduler()

        val assignment = assignment()

        val state = emptyState()
        assertEquals(Status.NEW, state.status)       // test requirement, update if needed

        assignment.mockCurrent(mockCard(state))
        val card = assignment.current!!

        // when
        val updated = scheduler.correct(card.state)

        // then
        val today = today()
        assertEquals(today, updated.due)
        assertEquals(0, updated.period)
    }

    @Test
    fun `test__newCard__correct__twice`() {
        // given
        val scheduler = scheduler()

        val assignment = assignment()

        val state = emptyState()
        assertEquals(Status.NEW, state.status)       // test requirement, update if needed

        assignment.mockCurrent(mockCard(state))
        val card = assignment.current!!

        // when
        val updated = scheduler.correct(scheduler.correct(card.state))

        // then
        val today = today()
        assertEquals(today.plusDays(1), updated.due)
        assertEquals(1, updated.period)
    }

    @Test
    fun `test__inProgressCard__wrong__dueDate`() {
        // given
        val scheduler = scheduler()

        val assignment = assignment()
        val today = today()

        val state = State(today, 4)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        assignment.mockCurrent(mockCard(state))
        val card = assignment.current!!

        // when
        val updated = scheduler.wrong(card.state)

        // then
        assertEquals(today, updated.due)
        assertEquals(0, updated.period)
    }

    @Test
    fun `test__inProgressCard__correct__dueDate`() {
        // given
        val scheduler = scheduler()

        val assignment = assignment()
        val today = today()

        val state = State(today, 4)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        assignment.mockCurrent(mockCard(state))
        val card = assignment.current!!

        // when
        val updated = scheduler.correct(card.state)

        // then
        assertEquals(today.plusDays(8), updated.due)
        assertEquals(8, updated.period)
    }

    @Test
    fun `test__inProgressCard__wrong__afterDueDate`() {
        // given
        val scheduler = scheduler()

        val assignment = assignment()
        val today = today()

        val state = State(today.minusDays(5), 8)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        assignment.mockCurrent(mockCard(state))
        val card = assignment.current!!

        // when
        val updated = scheduler.wrong(card.state)

        // then
        assertEquals(today, updated.due)
        assertEquals(0, updated.period)
    }

    @Test
    fun `test__inProgressCard__correct__afterDueDate`() {
        // given
        val scheduler = scheduler()

        val assignment = assignment()
        val today = today()

        val state = State(today.minusDays(5), 8)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        assignment.mockCurrent(mockCard(state))
        val card = assignment.current!!

        // when
        val updated = scheduler.correct(card.state)

        // then
        assertEquals(today.plusDays(16), updated.due)
        assertEquals(16, updated.period)
    }

    @Test
    fun `test__learnt__wrong`() {
        // given
        val scheduler = scheduler()

        val assignment = assignment()
        val today = today()

        val state = State(today.minusDays(217), 200)
        assertEquals(Status.LEARNT, state.status)       // test requirement, update if needed

        assignment.mockCurrent(mockCard(state))
        val card = assignment.current!!

        // when
        val updated = scheduler.wrong(card.state)

        // then
        assertEquals(today, updated.due)
        assertEquals(0, updated.period)
    }

    @Test
    fun `test__learnt__correct`() {
        // given
        val scheduler = scheduler()

        val assignment = assignment()
        val today = today()

        val state = State(today.minusDays(217), 200)
        assertEquals(Status.LEARNT, state.status)       // test requirement, update if needed

        assignment.mockCurrent(mockCard(state))
        val card = assignment.current!!

        // when
        val updated = scheduler.correct(card.state)

        // then
        assertEquals(today.plusDays(400), updated.due)
        assertEquals(400, updated.period)
        assertEquals(Status.LEARNT, state.status)
    }
}