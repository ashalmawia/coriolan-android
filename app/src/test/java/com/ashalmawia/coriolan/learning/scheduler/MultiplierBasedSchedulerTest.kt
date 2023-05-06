package com.ashalmawia.coriolan.learning.scheduler

import com.ashalmawia.coriolan.learning.*
import com.ashalmawia.coriolan.learning.exercise.flashcards.MultiplierBasedScheduler
import com.ashalmawia.coriolan.learning.exercise.flashcards.FlashcardsAnswer
import com.ashalmawia.coriolan.learning.exercise.flashcards.SpacedRepetitionScheduler
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MultiplierBasedSchedulerTest {

    private val today = TodayManager.today()
    private fun emptyState() = SchedulingState.new()

    private fun scheduler() = MultiplierBasedScheduler()

    @Test
    fun test__newCard__wrong__shouldMeetAtLeastTwoMoreTimes() {
        // given
        val scheduler = scheduler()

        var state = emptyState()
        assertEquals(Status.NEW, state.status)       // test requirement, update if needed

        // when
        state = scheduler.processAnswer(FlashcardsAnswer.WRONG, state)

        // then
        val today = today
        assertEquals(today, state.due)

        // when
        state = scheduler.correct(state)

        // then
        assertEquals(today, state.due)

        // when
        state = scheduler.correct(state)

        // then
        assertEquals(1, state.interval)
        assertEquals(today.plusDays(1), state.due)
    }

    @Test
    fun test__newCard__wrongMultipleTimes__shouldMeetAtLeastTwoMoreTimes() {
        // given
        val scheduler = scheduler()

        var state = emptyState()
        assertEquals(Status.NEW, state.status)       // test requirement, update if needed

        // when
        state = scheduler.wrong(state)

        // then
        val today = today
        assertEquals(today, state.due)

        // when
        state = scheduler.wrong(state)
        state = scheduler.wrong(state)
        state = scheduler.wrong(state)

        // then
        assertEquals(today, state.due)

        // when
        state = scheduler.correct(state)

        // then
        assertEquals(today, state.due)

        // when
        state = scheduler.correct(state)

        // then
        assertEquals(1, state.interval)
        assertEquals(today.plusDays(1), state.due)
    }

    @Test
    fun test__newCard__correct__shouldMeetOneMoreTime() {
        // given
        val scheduler = scheduler()

        var state = emptyState()
        assertEquals(Status.NEW, state.status)       // test requirement, update if needed

        // when
        state = scheduler.correct(state)

        // then
        val today = today
        assertEquals(0, state.interval)
        assertEquals(today, state.due)

        // when
        state = scheduler.correct(state)

        // then
        assertEquals(1, state.interval)
        assertEquals(today.plusDays(1), state.due)
    }

    @Test
    fun test__newCard__easy() {
        // given
        val scheduler = scheduler()

        val state = emptyState()
        assertEquals(Status.NEW, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.easy(state)

        // then
        val today = today
        assertEquals(4, updated.interval)
        assertEquals(today.plusDays(4), updated.due)
    }

    @Test
    fun test__inProgressCard__wrong__dueDate() {
        // given
        val scheduler = scheduler()
        val today = today

        val state = SchedulingState(today, 4)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.wrong(state)

        // then
        assertEquals(0, updated.interval)
        assertEquals(today, updated.due)
    }

    @Test
    fun test__inProgressCard__hard__dueDate() {
        // given
        val scheduler = scheduler()
        val today = today

        val state = SchedulingState(today, 4)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.hard(state)

        // then
        assertEquals(2, updated.interval)
        assertEquals(today.plusDays(2), updated.due)
    }

    @Test
    fun test__inProgressCard__correct__dueDate() {
        // given
        val scheduler = scheduler()
        val today = today

        val state = SchedulingState(today, 4)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.correct(state)

        // then
        assertEquals(8, updated.interval)
        assertEquals(today.plusDays(8), updated.due)
    }

    @Test
    fun test__inProgressCard__easy__dueDate() {
        // given
        val scheduler = scheduler()
        val today = today

        val state = SchedulingState(today, 4)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.easy(state)

        // then
        assertEquals(16, updated.interval)
        assertEquals(today.plusDays(16), updated.due)
    }

    @Test
    fun test__inProgressCard__wrong__afterDueDate() {
        // given
        val scheduler = scheduler()
        val today = today

        val state = SchedulingState(today.minusDays(5), 8)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.wrong(state)

        // then
        assertEquals(0, updated.interval)
        assertEquals(today, updated.due)
    }

    @Test
    fun test__inProgressCard__hard__afterDueDate() {
        // given
        val scheduler = scheduler()
        val today = today

        val state = SchedulingState(today.minusDays(5), 8)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.hard(state)

        // then
        assertEquals(13 / 2, updated.interval)
        assertEquals(today.plusDays(13 / 2), updated.due)
    }

    @Test
    fun test__inProgressCard__correct__afterDueDate() {
        // given
        val scheduler = scheduler()
        val today = today

        val state = SchedulingState(today.minusDays(5), 8)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.correct(state)

        // then
        assertEquals(13 * 2, updated.interval)
        assertEquals(today.plusDays(13 * 2), updated.due)
    }

    @Test
    fun test__inProgressCard__easy__afterDueDate() {
        // given
        val scheduler = scheduler()
        val today = today

        val state = SchedulingState(today.minusDays(5), 8)
        assertEquals(Status.IN_PROGRESS, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.easy(state)

        // then
        assertEquals(13 * 4, updated.interval)
        assertEquals(today.plusDays(13 * 4), updated.due)
    }

    @Test
    fun test__learnt__wrong() {
        // given
        val scheduler = scheduler()
        val today = today

        val state = SchedulingState(today.minusDays(217), 200)
        assertEquals(Status.LEARNT, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.wrong(state)

        // then
        assertEquals(0, updated.interval)
        assertEquals(today, updated.due)
    }

    @Test
    fun test__learnt__hard() {
        // given
        val scheduler = scheduler()
        val today = today

        val state = SchedulingState(today.minusDays(216), 200)
        assertEquals(Status.LEARNT, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.hard(state)

        // then
        assertEquals(416  / 2, updated.interval)
        assertEquals(today.plusDays(416  / 2), updated.due)
        assertEquals(Status.LEARNT, state.status)
    }

    @Test
    fun test__learnt__correct() {
        // given
        val scheduler = scheduler()
        val today = today

        val state = SchedulingState(today.minusDays(217), 200)
        assertEquals(Status.LEARNT, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.correct(state)

        // then
        assertEquals(417 * 2, updated.interval)
        assertEquals(today.plusDays(417 * 2), updated.due)
        assertEquals(Status.LEARNT, state.status)
    }

    @Test
    fun test__learnt__easy() {
        // given
        val scheduler = scheduler()
        val today = today

        val state = SchedulingState(today.minusDays(217), 200)
        assertEquals(Status.LEARNT, state.status)       // test requirement, update if needed

        // when
        val updated = scheduler.easy(state)

        // then
        assertEquals(417 * 4, updated.interval)
        assertEquals(today.plusDays(417 * 4), updated.due)
        assertEquals(Status.LEARNT, state.status)
    }
}

private fun SpacedRepetitionScheduler.wrong(SchedulingState: SchedulingState) = processAnswer(FlashcardsAnswer.WRONG, SchedulingState)
private fun SpacedRepetitionScheduler.correct(SchedulingState: SchedulingState) = processAnswer(FlashcardsAnswer.CORRECT, SchedulingState)
private fun SpacedRepetitionScheduler.easy(SchedulingState: SchedulingState) = processAnswer(FlashcardsAnswer.EASY, SchedulingState)
private fun SpacedRepetitionScheduler.hard(SchedulingState: SchedulingState) = processAnswer(FlashcardsAnswer.HARD, SchedulingState)