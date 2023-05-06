package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.TodayManager
import com.ashalmawia.coriolan.model.Card

class GenericLogbook(
        private val logbook: Logbook,
        private val exercise: Exercise
) : ExerciseLogbook {

    override fun recordCardAction(card: Card, oldLearningProgress: LearningProgress, newLearningProgress: LearningProgress) {
        val date = TodayManager.today()
        when (oldLearningProgress.status) {
            Status.NEW -> {
                logbook.incrementCardActions(date, exercise.id, card.deckId, CardAction.NEW_CARD_FIRST_SEEN)
            }

            Status.IN_PROGRESS, Status.LEARNT -> {
                if (newLearningProgress.status == Status.RELEARN) {
                    logbook.incrementCardActions(date, exercise.id, card.deckId, CardAction.CARD_RELEARNED)
                } else {
                    logbook.incrementCardActions(date, exercise.id, card.deckId, CardAction.CARD_REVIEWED)
                }
            }

            Status.RELEARN -> {
            } // ignore all relearns as if they appear they have been already counted somehow
        }
    }

    override fun unrecordCardAction(card: Card, learningProgress: LearningProgress, learningProgressThatWasUndone: LearningProgress) {
        val date = TodayManager.today()
        when (learningProgress.status) {
            Status.NEW -> {
                logbook.decrementCardActions(date, exercise.id, card.deckId, CardAction.NEW_CARD_FIRST_SEEN)
            }

            Status.IN_PROGRESS, Status.LEARNT -> {
                if (learningProgressThatWasUndone.status == Status.RELEARN) {
                    logbook.decrementCardActions(date, exercise.id, card.deckId, CardAction.CARD_REVIEWED)
                } else {
                    logbook.decrementCardActions(date, exercise.id, card.deckId, CardAction.CARD_RELEARNED)
                }
            }

            Status.RELEARN -> {
            } // ignore all relearns as if they appear they have been already counted somehow
        }
    }
}