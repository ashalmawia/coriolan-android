package com.ashalmawia.coriolan.learning.scheduler

import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.exercise.sr.INTERVAL_LEARNT
import com.ashalmawia.coriolan.learning.exercise.sr.emptyState
import com.ashalmawia.coriolan.model.mockState
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ExerciseStateTest {

    @Test
    fun test__stateNew() {
        // when
        val state = emptyState()

        // then
        assertEquals(Status.NEW, state.status)
    }

    @Test
    fun test__state__interval__0() {
        // when
        val state = mockState(0)

        // then
        assertEquals(Status.RELEARN, state.status)
    }

    @Test
    fun test__state__interval__1() {
        // when
        val state = mockState(1)

        // then
        assertEquals(Status.IN_PROGRESS, state.status)
    }

    @Test
    fun test__state__interval__almost_learnt() {
        // when
        val state = mockState(INTERVAL_LEARNT - 1)

        // then
        assertEquals(Status.IN_PROGRESS, state.status)
    }

    @Test
    fun test__state__interval__learnt() {
        // when
        val state = mockState(INTERVAL_LEARNT)

        // then
        assertEquals(Status.LEARNT, state.status)
    }
}