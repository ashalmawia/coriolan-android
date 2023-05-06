package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.learning.SchedulingState
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.TodayManager

class LogbookWriterImpl(private val logbook: Logbook) : LogbookWriter {

    override fun recordCardAction(task: Task, newState: SchedulingState) {
        val date = TodayManager.today()
        when (task.learningProgress.status) {
            Status.NEW -> {
                logbook.incrementCardActions(date, task.exercise.id, task.card.deckId, CardAction.NEW_CARD_FIRST_SEEN)
            }

            Status.IN_PROGRESS, Status.LEARNT -> {
                if (newState.status == Status.RELEARN) {
                    logbook.incrementCardActions(date, task.exercise.id, task.card.deckId, CardAction.CARD_RELEARNED)
                } else {
                    logbook.incrementCardActions(date, task.exercise.id, task.card.deckId, CardAction.CARD_REVIEWED)
                }
            }

            Status.RELEARN -> {
            } // ignore all relearns as if they appear they have been already counted somehow
        }
    }

    override fun unrecordCardAction(task: Task, stateThatWasUndone: SchedulingState) {
        val date = TodayManager.today()
        when (task.learningProgress.status) {
            Status.NEW -> {
                logbook.decrementCardActions(date, task.exercise.id, task.card.deckId, CardAction.NEW_CARD_FIRST_SEEN)
            }

            Status.IN_PROGRESS, Status.LEARNT -> {
                if (stateThatWasUndone.status == Status.RELEARN) {
                    logbook.decrementCardActions(date, task.exercise.id, task.card.deckId, CardAction.CARD_REVIEWED)
                } else {
                    logbook.decrementCardActions(date, task.exercise.id, task.card.deckId, CardAction.CARD_RELEARNED)
                }
            }

            Status.RELEARN -> {
            } // ignore all relearns as if they appear they have been already counted somehow
        }
    }
}