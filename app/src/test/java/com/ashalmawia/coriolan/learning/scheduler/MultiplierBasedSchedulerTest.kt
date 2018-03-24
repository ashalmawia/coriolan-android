package com.ashalmawia.coriolan.learning.scheduler

import com.ashalmawia.coriolan.learning.scheduler.sr.MultiplierBasedScheduler
import com.ashalmawia.coriolan.learning.scheduler.sr.SRState
import com.ashalmawia.coriolan.learning.scheduler.sr.emptyState
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MultiplierBasedSchedulerTest {

    private fun scheduler() = MultiplierBasedScheduler()

    @Test
    fun `test__newCard__wrong`() {
        // given
        val scheduler = scheduler()

        val state = emptyState()
        assertEquals(Status.NEW, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.wrong(state)

        // then
        val today = today()
        assertEquals(0, updated.period)
        assertEquals(today, updated.due)
    }

    @Test
    fun `test__newCard__correct`() {
        // given
        val scheduler = scheduler()

        val state = emptyState()
        assertEquals(Status.NEW, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.correct(state)

        // then
        val today = today()
        assertEquals(0, updated.period)
        assertEquals(today, updated.due)
    }

    @Test
    fun `test__newCard__correct__twice`() {
        // given
        val scheduler = scheduler()

        val state = emptyState()
        assertEquals(Status.NEW, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.correct(scheduler.correct(state))

        // then
        val today = today()
        assertEquals(1, updated.period)
        assertEquals(today.plusDays(1), updated.due)
    }

    @Test
    fun `test__inProgressCard__wrong__dueDate`() {
        // given
        val scheduler = scheduler()
        val today = today()

        val state = SRState(today, 4)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.wrong(state)

        // then
        assertEquals(0, updated.period)
        assertEquals(today, updated.due)
    }

    @Test
    fun `test__inProgressCard__hard__dueDate`() {
        // given
        val scheduler = scheduler()
        val today = today()

        val state = SRState(today, 4)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.hard(state)

        // then
        assertEquals(2, updated.period)
        assertEquals(today.plusDays(2), updated.due)
    }

    @Test
    fun `test__inProgressCard__correct__dueDate`() {
        // given
        val scheduler = scheduler()
        val today = today()

        val state = SRState(today, 4)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.correct(state)

        // then
        assertEquals(8, updated.period)
        assertEquals(today.plusDays(8), updated.due)
    }

    @Test
    fun `test__inProgressCard__easy__dueDate`() {
        // given
        val scheduler = scheduler()
        val today = today()

        val state = SRState(today, 4)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.easy(state)

        // then
        assertEquals(16, updated.period)
        assertEquals(today.plusDays(16), updated.due)
    }

    @Test
    fun `test__inProgressCard__wrong__afterDueDate`() {
        // given
        val scheduler = scheduler()
        val today = today()

        val state = SRState(today.minusDays(5), 8)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.wrong(state)

        // then
        assertEquals(0, updated.period)
        assertEquals(today, updated.due)
    }

    @Test
    fun `test__inProgressCard__hard__afterDueDate`() {
        // given
        val scheduler = scheduler()
        val today = today()

        val state = SRState(today.minusDays(5), 8)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.hard(state)

        // then
        assertEquals(13 / 2, updated.period)
        assertEquals(today.plusDays(13 / 2), updated.due)
    }

    @Test
    fun `test__inProgressCard__correct__afterDueDate`() {
        // given
        val scheduler = scheduler()
        val today = today()

        val state = SRState(today.minusDays(5), 8)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.correct(state)

        // then
        assertEquals(13 * 2, updated.period)
        assertEquals(today.plusDays(13 * 2), updated.due)
    }

    @Test
    fun `test__inProgressCard__easy__afterDueDate`() {
        // given
        val scheduler = scheduler()
        val today = today()

        val state = SRState(today.minusDays(5), 8)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.easy(state)

        // then
        assertEquals(13 * 4, updated.period)
        assertEquals(today.plusDays(13 * 4), updated.due)
    }

    @Test
    fun `test__learnt__wrong`() {
        // given
        val scheduler = scheduler()
        val today = today()

        val state = SRState(today.minusDays(217), 200)
        assertEquals(Status.LEARNT, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.wrong(state)

        // then
        assertEquals(0, updated.period)
        assertEquals(today, updated.due)
    }

    @Test
    fun `test__learnt__hard`() {
        // given
        val scheduler = scheduler()
        val today = today()

        val state = SRState(today.minusDays(216), 200)
        assertEquals(Status.LEARNT, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.hard(state)

        // then
        assertEquals(416  / 2, updated.period)
        assertEquals(today.plusDays(416  / 2), updated.due)
        assertEquals(Status.LEARNT, state.status)
    }

    @Test
    fun `test__learnt__correct`() {
        // given
        val scheduler = scheduler()
        val today = today()

        val state = SRState(today.minusDays(217), 200)
        assertEquals(Status.LEARNT, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.correct(state)

        // then
        assertEquals(417 * 2, updated.period)
        assertEquals(today.plusDays(417 * 2), updated.due)
        assertEquals(Status.LEARNT, state.status)
    }

    @Test
    fun `test__learnt__easy`() {
        // given
        val scheduler = scheduler()
        val today = today()

        val state = SRState(today.minusDays(217), 200)
        assertEquals(Status.LEARNT, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.easy(state)

        // then
        assertEquals(417 * 4, updated.period)
        assertEquals(today.plusDays(417 * 4), updated.due)
        assertEquals(Status.LEARNT, state.status)
    }
}