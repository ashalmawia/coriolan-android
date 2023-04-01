package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.TodayManager
import com.ashalmawia.coriolan.model.Card

class GenericLogbook(
        private val logbook: Logbook,
        private val exercise: Exercise
) : ExerciseLogbook {

    override fun recordCardAction(card: Card, oldState: State, newState: State) {
        val date = TodayManager.today()
        when (exercise.status(oldState)) {
            Status.NEW -> {
                logbook.incrementCardActions(date, exercise.id, CardAction.NEW_CARD_FIRST_SEEN)
            }

            Status.IN_PROGRESS, Status.LEARNT -> {
                if (exercise.status(newState) == Status.RELEARN) {
                    logbook.incrementCardActions(date, exercise.id, CardAction.CARD_RELEARNED)
                } else {
                    logbook.incrementCardActions(date, exercise.id, CardAction.CARD_REVIEWED)
                }
            }

            Status.RELEARN -> {
            } // ignore all relearns as if they appear they have been already counted somehow
        }
    }

    override fun unrecordCardAction(card: Card, state: State, stateThatWasUndone: State) {
        val date = TodayManager.today()
        when (exercise.status(state)) {
            Status.NEW -> {
                logbook.decrementCardActions(date, exercise.id, CardAction.NEW_CARD_FIRST_SEEN)
            }

            Status.IN_PROGRESS, Status.LEARNT -> {
                if (exercise.status(stateThatWasUndone) == Status.RELEARN) {
                    logbook.decrementCardActions(date, exercise.id, CardAction.CARD_REVIEWED)
                } else {
                    logbook.decrementCardActions(date, exercise.id, CardAction.CARD_RELEARNED)
                }
            }

            Status.RELEARN -> {
            } // ignore all relearns as if they appear they have been already counted somehow
        }
    }
}