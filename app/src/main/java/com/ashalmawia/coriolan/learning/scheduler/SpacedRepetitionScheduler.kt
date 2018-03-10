package com.ashalmawia.coriolan.learning.scheduler

import org.joda.time.Days
import kotlin.math.*

private const val MULTIPLIER_HARD = 0.5f
private const val MULTIPLIER_CORRECT = 2f
private const val MULTIPLIER_EASY = 4f

class SpacedRepetitionScheduler : Scheduler {

    override fun answers(state: State): Array<Answer> {
        return when (state.status) {
            Status.NEW, Status.RELEARN -> arrayOf(Answer.WRONG, Answer.CORRECT)
            Status.IN_PROGRESS, Status.LEARNT -> arrayOf(Answer.WRONG, Answer.HARD, Answer.CORRECT, Answer.EASY)
        }
    }

    override fun wrong(state: State): State {
        val due = today()
        val period = 0
        return State(due, period)
    }

    override fun hard(state: State): State = correctInner(state, MULTIPLIER_HARD)

    override fun correct(state: State): State = correctInner(state, MULTIPLIER_CORRECT)

    override fun easy(state: State): State = correctInner(state, MULTIPLIER_EASY)

    private fun correctInner(state: State, multiplier: Float): State {
        return if (state.period == PERIOD_NEVER_SCHEDULED) {
            // the card is completely new
            // the first answer actually counts like "wrong"
            wrong(state)
        } else {
            val expectedPeriod = state.period
            val actualPeriod = abs(Days.daysBetween(state.due.minusDays(state.period), today()).days)
            val period = max(floor(max(expectedPeriod, actualPeriod) * multiplier).roundToInt(), 1)
            val due = today().plusDays(period)
            State(due, period)
        }
    }
}