package com.ashalmawia.coriolan.learning.exercise.flashcards

import com.ashalmawia.coriolan.learning.INTERVAL_FIRST_ASNWER_WRONG
import com.ashalmawia.coriolan.learning.INTERVAL_NEVER_SCHEDULED
import com.ashalmawia.coriolan.learning.SchedulingState
import com.ashalmawia.coriolan.learning.TodayManager
import com.ashalmawia.coriolan.ui.learning.CardViewAnswer
import org.joda.time.Days
import kotlin.math.*

private const val MULTIPLIER_HARD = 0.5f
private const val MULTIPLIER_CORRECT = 2f
private const val MULTIPLIER_EASY = 4f

private const val NEW_RESPONDED_EASY_DAYS = 4

class MultiplierBasedScheduler : SpacedRepetitionScheduler {

    override fun processAnswer(answer: CardViewAnswer, state: SchedulingState): SchedulingState {
        return when (answer) {
            CardViewAnswer.WRONG -> wrong(state)
            CardViewAnswer.CORRECT -> correct(state)
            CardViewAnswer.EASY -> easy(state)
            CardViewAnswer.HARD -> hard(state)
        }
    }

    private fun wrong(progress: SchedulingState): SchedulingState {
        return if (progress.interval == INTERVAL_NEVER_SCHEDULED || progress.interval == INTERVAL_FIRST_ASNWER_WRONG) {
            progress.copy(due = today(), interval = INTERVAL_FIRST_ASNWER_WRONG)
        } else {
            stateForRelearn(progress)
        }
    }

    private fun hard(state: SchedulingState): SchedulingState = stateForCorrect(state, MULTIPLIER_HARD)

    private fun correct(state: SchedulingState): SchedulingState = stateForCorrect(state, MULTIPLIER_CORRECT)

    private fun easy(state: SchedulingState): SchedulingState {
        return if (state.interval == INTERVAL_NEVER_SCHEDULED) {
            // a special rule for easy for a new card, don't show it in this assignment
            state.copy(due = today().plusDays(NEW_RESPONDED_EASY_DAYS), interval = NEW_RESPONDED_EASY_DAYS)
        } else {
            stateForCorrect(state, MULTIPLIER_EASY)
        }
    }

    private fun stateForRelearn(progress: SchedulingState): SchedulingState =
            progress.copy(due = today(), interval = 0)

    private fun stateForCorrect(progress: SchedulingState, multiplier: Float): SchedulingState {
        return if (progress.interval == INTERVAL_NEVER_SCHEDULED || progress.interval == INTERVAL_FIRST_ASNWER_WRONG) {
            // the card is completely new
            // the first correct answer actually counts like "wrong"
            stateForRelearn(progress)
        } else {
            val expectedInterval = progress.interval
            val actualInterval = abs(Days.daysBetween(progress.due.minusDays(progress.interval), today()).days)
            val interval = max(floor(max(expectedInterval, actualInterval) * multiplier).roundToInt(), 1)
            val due = today().plusDays(interval)
            progress.copy(due = due, interval = interval)
        }
    }

    private fun today() = TodayManager.today()
}