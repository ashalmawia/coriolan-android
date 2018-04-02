package com.ashalmawia.coriolan.learning.scheduler.sr

import com.ashalmawia.coriolan.learning.scheduler.*
import org.joda.time.Days
import kotlin.math.*

private const val MULTIPLIER_HARD = 0.5f
private const val MULTIPLIER_CORRECT = 2f
private const val MULTIPLIER_EASY = 4f

private const val NEW_RESPONDED_EASY_DAYS = 4

class MultiplierBasedScheduler : Scheduler {

    override fun answers(state: SRState): Array<Answer> {
        return when (state.status) {
            Status.NEW -> arrayOf(Answer.WRONG, Answer.CORRECT, Answer.EASY)
            Status.RELEARN -> arrayOf(Answer.WRONG, Answer.CORRECT)
            Status.IN_PROGRESS, Status.LEARNT -> arrayOf(Answer.WRONG, Answer.HARD, Answer.CORRECT, Answer.EASY)
        }
    }

    override fun wrong(state: SRState): SRState {
        return if (state.period == PERIOD_NEVER_SCHEDULED || state.period == PERIOD_FIRST_ASNWER_WRONG) {
            SRState(today(), PERIOD_FIRST_ASNWER_WRONG)
        } else {
            stateForRelearn()
        }
    }

    override fun hard(state: SRState): SRState = stateForCorrect(state, MULTIPLIER_HARD)

    override fun correct(state: SRState): SRState = stateForCorrect(state, MULTIPLIER_CORRECT)

    override fun easy(state: SRState): SRState {
        return if (state.period == PERIOD_NEVER_SCHEDULED) {
            // a special rule for easy for a new card, don't show it in this assignment
            SRState(today().plusDays(NEW_RESPONDED_EASY_DAYS), NEW_RESPONDED_EASY_DAYS)
        } else {
            stateForCorrect(state, MULTIPLIER_EASY)
        }
    }

    private fun stateForRelearn(): SRState = SRState(today(), 0)

    private fun stateForCorrect(state: SRState, multiplier: Float): SRState {
        return if (state.period == PERIOD_NEVER_SCHEDULED || state.period == PERIOD_FIRST_ASNWER_WRONG) {
            // the card is completely new
            // the first correct answer actually counts like "wrong"
            stateForRelearn()
        } else {
            val expectedPeriod = state.period
            val actualPeriod = abs(Days.daysBetween(state.due.minusDays(state.period), today()).days)
            val period = max(floor(max(expectedPeriod, actualPeriod) * multiplier).roundToInt(), 1)
            val due = today().plusDays(period)
            SRState(due, period)
        }
    }
}