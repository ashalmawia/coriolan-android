package com.ashalmawia.coriolan.learning.scheduler

import kotlin.math.max

private const val MULTIPLIER_CORRECT = 2

class SpacedRepetitionScheduler : Scheduler {

    override fun wrong(state: State): State {
        val due = today()
        val period = 0
        return State(due, period)
    }

    override fun correct(state: State): State {
        if (state.period == PERIOD_NEVER_SCHEDULED) {
            // the card is completely new
            // the first answer actually counts like "wrong"
            return wrong(state)
        } else {
            val period = max(state.period * MULTIPLIER_CORRECT, 1)
            val due = today().plusDays(period)
            return State(due, period)
        }
    }
}