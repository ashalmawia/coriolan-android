package com.ashalmawia.coriolan.learning.scheduler

import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.exercise.mockEmptySRState
import com.ashalmawia.coriolan.learning.exercise.sr.PERIOD_LEARNT
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.model.mockState
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class StateTest {

    @Test
    fun `test__stateNew`() {
        // when
        val state = mockEmptySRState(mockToday())

        // then
        assertEquals(Status.NEW, state.status)
    }

    @Test
    fun `test__state__period__0`() {
        // when
        val state = mockState(0)

        // then
        assertEquals(Status.RELEARN, state.status)
    }

    @Test
    fun `test__state__period__1`() {
        // when
        val state = mockState(1)

        // then
        assertEquals(Status.IN_PROGRESS, state.status)
    }

    @Test
    fun `test__state__period__almost_learnt`() {
        // when
        val state = mockState(PERIOD_LEARNT - 1)

        // then
        assertEquals(Status.IN_PROGRESS, state.status)
    }

    @Test
    fun `test__state__period__learnt`() {
        // when
        val state = mockState(PERIOD_LEARNT)

        // then
        assertEquals(Status.LEARNT, state.status)
    }
}