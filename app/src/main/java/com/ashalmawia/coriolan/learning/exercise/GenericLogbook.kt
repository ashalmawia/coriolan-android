package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.TodayProvider
import com.ashalmawia.coriolan.model.Card

class GenericLogbook(
        private val journal: Journal,
        private val todayProvider: TodayProvider,
        private val exercise: Exercise
) : ExerciseLogbook {

    override fun recordCardAction(card: Card, oldState: State, newState: State) {
        val date = todayProvider.today()
        when (exercise.status(oldState)) {
            Status.NEW -> {
                journal.incrementCardActions(date, exercise.id, CardAction.NEW_CARD_FIRST_SEEN)
            }

            Status.IN_PROGRESS, Status.LEARNT -> {
                if (exercise.status(newState) == Status.RELEARN) {
                    journal.incrementCardActions(date, exercise.id, CardAction.CARD_RELEARNED)
                } else {
                    journal.incrementCardActions(date, exercise.id, CardAction.CARD_REVIEWED)
                }
            }

            Status.RELEARN -> {
            } // ignore all relearns as if they appear they have been already counted somehow
        }
    }

    override fun unrecordCardAction(card: Card, state: State, stateThatWasUndone: State) {
        val date = todayProvider.today()
        when (exercise.status(state)) {
            Status.NEW -> {
                journal.decrementCardActions(date, exercise.id, CardAction.NEW_CARD_FIRST_SEEN)
            }

            Status.IN_PROGRESS, Status.LEARNT -> {
                if (exercise.status(stateThatWasUndone) == Status.RELEARN) {
                    journal.decrementCardActions(date, exercise.id, CardAction.CARD_REVIEWED)
                } else {
                    journal.decrementCardActions(date, exercise.id, CardAction.CARD_RELEARNED)
                }
            }

            Status.RELEARN -> {
            } // ignore all relearns as if they appear they have been already counted somehow
        }
    }
}