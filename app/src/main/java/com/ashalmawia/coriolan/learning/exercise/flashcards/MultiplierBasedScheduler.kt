package com.ashalmawia.coriolan.learning.exercise.flashcards

import com.ashalmawia.coriolan.learning.TodayManager
import org.joda.time.Days
import kotlin.math.*

private const val MULTIPLIER_HARD = 0.5f
private const val MULTIPLIER_CORRECT = 2f
private const val MULTIPLIER_EASY = 4f

private const val NEW_RESPONDED_EASY_DAYS = 4

class MultiplierBasedScheduler : SpacedRepetitionScheduler {

    override fun processAnswer(answer: FlashcardsAnswer, state: ExerciseState): ExerciseState {
        return when (answer) {
            FlashcardsAnswer.WRONG -> wrong(state)
            FlashcardsAnswer.CORRECT -> correct(state)
            FlashcardsAnswer.EASY -> easy(state)
            FlashcardsAnswer.HARD -> hard(state)
        }
    }

    private fun wrong(state: ExerciseState): ExerciseState {
        return if (state.interval == INTERVAL_NEVER_SCHEDULED || state.interval == INTERVAL_FIRST_ASNWER_WRONG) {
            ExerciseState(today(), INTERVAL_FIRST_ASNWER_WRONG)
        } else {
            stateForRelearn()
        }
    }

    private fun hard(state: ExerciseState): ExerciseState = stateForCorrect(state, MULTIPLIER_HARD)

    private fun correct(state: ExerciseState): ExerciseState = stateForCorrect(state, MULTIPLIER_CORRECT)

    private fun easy(state: ExerciseState): ExerciseState {
        return if (state.interval == INTERVAL_NEVER_SCHEDULED) {
            // a special rule for easy for a new card, don't show it in this assignment
            ExerciseState(today().plusDays(NEW_RESPONDED_EASY_DAYS), NEW_RESPONDED_EASY_DAYS)
        } else {
            stateForCorrect(state, MULTIPLIER_EASY)
        }
    }

    private fun stateForRelearn(): ExerciseState = ExerciseState(today(), 0)

    private fun stateForCorrect(state: ExerciseState, multiplier: Float): ExerciseState {
        return if (state.interval == INTERVAL_NEVER_SCHEDULED || state.interval == INTERVAL_FIRST_ASNWER_WRONG) {
            // the card is completely new
            // the first correct answer actually counts like "wrong"
            stateForRelearn()
        } else {
            val expectedInterval = state.interval
            val actualInterval = abs(Days.daysBetween(state.due.minusDays(state.interval), today()).days)
            val interval = max(floor(max(expectedInterval, actualInterval) * multiplier).roundToInt(), 1)
            val due = today().plusDays(interval)
            ExerciseState(due, interval)
        }
    }

    private fun today() = TodayManager.today()
}