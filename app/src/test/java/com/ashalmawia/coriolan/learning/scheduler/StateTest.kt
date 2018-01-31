package com.ashalmawia.coriolan.learning.scheduler

import java.util.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner

@RunWith(BlockJUnit4ClassRunner::class)
class StateTest {

    @Test
    fun `test__stateNew`() {
        // when
        val state = emptyState()

        // then
        assertEquals(Status.NEW, state.status)
    }

    @Test
    fun `test__state__period__0`() {
        // when
        val state = state(0)

        // then
        assertEquals(Status.IN_PROGRESS, state.status)
    }

    @Test
    fun `test__state__period__1`() {
        // when
        val state = state(1)

        // then
        assertEquals(Status.IN_PROGRESS, state.status)
    }

    @Test
    fun `test__state__period__almost_learnt`() {
        // when
        val state = state(PERIOD_LEARNT - 1)

        // then
        assertEquals(Status.IN_PROGRESS, state.status)
    }

    @Test
    fun `test__state__period__learnt`() {
        // when
        val state = state(PERIOD_LEARNT)

        // then
        assertEquals(Status.LEARNT, state.status)
    }
}

private fun state(period: Int): State {
    val date = Date()
    return State(date, period)
}