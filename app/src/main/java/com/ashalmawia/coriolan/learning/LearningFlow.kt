package com.ashalmawia.coriolan.learning

import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.assignment.Assignment
import com.ashalmawia.coriolan.learning.assignment.AssignmentFactory
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.ExpressionExtras

class LearningFlow(
        private val repository: Repository,
        assignmentFactory: AssignmentFactory,
        val deck: Deck,
        cardType: CardType,
        studyOrder: StudyOrder,
        val exercise: Exercise,
        val journal: Journal,
        private val listener: Listener
) {

    private val assignment: Assignment = assignmentFactory.createAssignment(
            studyOrder,
            exercise,
            deck,
            cardType
    )

    val card
        get() = assignment.current!!

    val counts: Counts
        get() = assignment.counts()

    fun showNextOrComplete() {
        if (assignment.hasNext()) {
            val card = assignment.next()
            renderCard(card)
        } else {
            finish()
        }
    }

    fun replyCurrent(reply: Any) {
        val updated = exercise.processReply(repository, card, reply)
        // todo: decouple
        recordCardStudied(card.state.spacedRepetition.status, updated.state.spacedRepetition.status, journal)
        rescheduleIfNeeded(updated)
        showNextOrComplete()
    }

    private fun rescheduleIfNeeded(card: CardWithState) {
        if (exercise.isPending(card)) {
            assignment.reschedule(card)
        }
    }

    private fun finish() {
        listener.onFinish()
    }

    fun canUndo() = exercise.canUndo && assignment.canUndo()

    fun undo() {
        val newState = assignment.current!!.state
        val undone = assignment.undo()
        exercise.updateCardState(repository, undone, undone.state)
        // todo: decouple
        undoCardStudied(undone.state.spacedRepetition.status, journal, newState.spacedRepetition.status != Status.RELEARN)
        renderCard(undone)
    }

    private fun recordCardStudied(oldStatus: Status, newStatus: Status, journal: Journal) {
        val date = assignment.date
        when (oldStatus) {
            Status.NEW -> {
                journal.recordNewCardStudied(date)
            }

            Status.IN_PROGRESS, Status.LEARNT -> {
                journal.recordReviewStudied(date)
                if (newStatus == Status.RELEARN) {
                    journal.recordCardRelearned(date)
                }
            }

            Status.RELEARN -> {
            } // ignore all relearns as if they appear they have been already counted somehow
        }
    }

    private fun undoCardStudied(status: Status, journal: Journal, correct: Boolean) {
        val date = assignment.date
        when (status) {
            Status.NEW -> {
                journal.undoNewCardStudied(date)
            }

            Status.IN_PROGRESS, Status.LEARNT -> {
                if (correct) {
                    journal.undoReviewStudied(date)
                } else {
                    journal.undoCardRelearned(date)
                }
            }

            Status.RELEARN -> {
            } // ignore all relearns as if they appear they have been already counted somehow
        }
    }

    fun refetchCard(cardWithState: CardWithState) {
        val card = cardWithState.card
        val updated = repository.cardById(card.id, card.domain)!!
        val updatedWithState = exercise.getCardWithState(repository, updated)
        if (updated.deckId == deck.id && exercise.isPending(updatedWithState)) {
            assignment.replace(card, updatedWithState)
            if (isCurrent(card)) {
                // make exercise pre-present the card with the changes
                renderCard(updatedWithState)
            }
        } else {
            dropCard(card)
        }
    }

    private fun renderCard(card: CardWithState) {
        val extras = repository.allExtrasForCard(card.card)
        listener.onRender(card, extras)
    }

    fun dropCard(card: Card) {
        val removingCurrent = isCurrent(card)
        assignment.delete(card)
        if (removingCurrent) {
            showNextOrComplete()
        }
    }

    private fun isCurrent(card: Card) = this.card.card.id == card.id

    interface Listener {
        fun onRender(card: CardWithState, extras: List<ExpressionExtras>)
        fun onFinish()
    }

    interface Factory {
        fun createLearningFlow(
                deck: Deck,
                cardType: CardType,
                studyOrder: StudyOrder,
                exercise: Exercise,
                listener: Listener
        ) : LearningFlow
    }
}
class LearningFlowFactory(
        private val repository: Repository,
        private val assignmentFactory: AssignmentFactory,
        private val journal: Journal
) : LearningFlow.Factory {
    override fun createLearningFlow(
            deck: Deck,
            cardType: CardType,
            studyOrder: StudyOrder,
            exercise: Exercise,
            listener: LearningFlow.Listener
    ): LearningFlow {
        return LearningFlow(repository, assignmentFactory, deck, cardType, studyOrder, exercise, journal, listener)
    }
}