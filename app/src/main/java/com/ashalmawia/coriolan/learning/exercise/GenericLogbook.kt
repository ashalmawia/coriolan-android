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

    override fun recordCardStudied(card: Card, oldState: State, newState: State) {
        val date = todayProvider.today()
        when (exercise.status(oldState)) {
            Status.NEW -> {
                journal.incrementCardStudied(date, Status.NEW, exercise.id)
            }

            Status.IN_PROGRESS, Status.LEARNT -> {
                if (exercise.status(newState) == Status.RELEARN) {
                    journal.incrementCardStudied(date, Status.RELEARN, exercise.id)
                } else {
                    journal.incrementCardStudied(date, Status.IN_PROGRESS, exercise.id)
                }
            }

            Status.RELEARN -> {
            } // ignore all relearns as if they appear they have been already counted somehow
        }
    }

    override fun undoCardStudied(card: Card, state: State, stateThatWasUndone: State) {
        val date = todayProvider.today()
        when (exercise.status(state)) {
            Status.NEW -> {
                journal.decrementCardStudied(date, Status.NEW, exercise.id)
            }

            Status.IN_PROGRESS, Status.LEARNT -> {
                if (exercise.status(stateThatWasUndone) == Status.RELEARN) {
                    journal.decrementCardStudied(date, Status.IN_PROGRESS, exercise.id)
                } else {
                    journal.decrementCardStudied(date, Status.RELEARN, exercise.id)
                }
            }

            Status.RELEARN -> {
            } // ignore all relearns as if they appear they have been already counted somehow
        }
    }
}