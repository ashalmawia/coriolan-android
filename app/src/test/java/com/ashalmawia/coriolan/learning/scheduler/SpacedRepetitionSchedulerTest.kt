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
        assertEquals(0, updated.period)
        assertEquals(today, updated.due)
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
        assertEquals(0, updated.period)
        assertEquals(today, updated.due)
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
        assertEquals(1, updated.period)
        assertEquals(today.plusDays(1), updated.due)
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
        assertEquals(0, updated.period)
        assertEquals(today, updated.due)
    }

    @Test
    fun `test__inProgressCard__hard__dueDate`() {
        // given
        val scheduler = scheduler()

        val assignment = assignment()
        val today = today()

        val state = State(today, 4)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        assignment.mockCurrent(mockCard(state))
        val card = assignment.current!!

        // when
        val updated = scheduler.hard(card.state)

        // then
        assertEquals(2, updated.period)
        assertEquals(today.plusDays(2), updated.due)
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
        assertEquals(8, updated.period)
        assertEquals(today.plusDays(8), updated.due)
    }

    @Test
    fun `test__inProgressCard__easy__dueDate`() {
        // given
        val scheduler = scheduler()

        val assignment = assignment()
        val today = today()

        val state = State(today, 4)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        assignment.mockCurrent(mockCard(state))
        val card = assignment.current!!

        // when
        val updated = scheduler.easy(card.state)

        // then
        assertEquals(16, updated.period)
        assertEquals(today.plusDays(16), updated.due)
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
        assertEquals(0, updated.period)
        assertEquals(today, updated.due)
    }

    @Test
    fun `test__inProgressCard__hard__afterDueDate`() {
        // given
        val scheduler = scheduler()

        val assignment = assignment()
        val today = today()

        val state = State(today.minusDays(5), 8)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        assignment.mockCurrent(mockCard(state))
        val card = assignment.current!!

        // when
        val updated = scheduler.hard(card.state)

        // then
        assertEquals(13 / 2, updated.period)
        assertEquals(today.plusDays(13 / 2), updated.due)
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
        assertEquals(13 * 2, updated.period)
        assertEquals(today.plusDays(13 * 2), updated.due)
    }

    @Test
    fun `test__inProgressCard__easy__afterDueDate`() {
        // given
        val scheduler = scheduler()

        val assignment = assignment()
        val today = today()

        val state = State(today.minusDays(5), 8)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        assignment.mockCurrent(mockCard(state))
        val card = assignment.current!!

        // when
        val updated = scheduler.easy(card.state)

        // then
        assertEquals(13 * 4, updated.period)
        assertEquals(today.plusDays(13 * 4), updated.due)
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
        assertEquals(0, updated.period)
        assertEquals(today, updated.due)
    }

    @Test
    fun `test__learnt__hard`() {
        // given
        val scheduler = scheduler()

        val assignment = assignment()
        val today = today()

        val state = State(today.minusDays(216), 200)
        assertEquals(Status.LEARNT, state.status)       // test requirement, update if needed

        assignment.mockCurrent(mockCard(state))
        val card = assignment.current!!

        // when
        val updated = scheduler.hard(card.state)

        // then
        assertEquals(416  / 2, updated.period)
        assertEquals(today.plusDays(416  / 2), updated.due)
        assertEquals(Status.LEARNT, state.status)
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
        assertEquals(417 * 2, updated.period)
        assertEquals(today.plusDays(417 * 2), updated.due)
        assertEquals(Status.LEARNT, state.status)
    }

    @Test
    fun `test__learnt__easy`() {
        // given
        val scheduler = scheduler()

        val assignment = assignment()
        val today = today()

        val state = State(today.minusDays(217), 200)
        assertEquals(Status.LEARNT, state.status)       // test requirement, update if needed

        assignment.mockCurrent(mockCard(state))
        val card = assignment.current!!

        // when
        val updated = scheduler.easy(card.state)

        // then
        assertEquals(417 * 4, updated.period)
        assertEquals(today.plusDays(417 * 4), updated.due)
        assertEquals(Status.LEARNT, state.status)
    }
}