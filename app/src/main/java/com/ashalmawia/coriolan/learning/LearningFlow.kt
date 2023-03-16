package com.ashalmawia.coriolan.learning

import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.assignment.Assignment
import com.ashalmawia.coriolan.learning.assignment.AssignmentFactory
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.exercise.sr.SRAnswer
import com.ashalmawia.coriolan.learning.exercise.sr.SRState
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.ExpressionExtras

class LearningFlow<S : State, R>(
        private val repository: Repository,
        assignmentFactory: AssignmentFactory,
        val deck: Deck,
        private val cardType: CardType,
        studyOrder: StudyOrder,
        val exercise: Exercise<S, R>,
        val journal: Journal,
        private val listener: Listener<S>
) {

    private val assignment: Assignment<S> = assignmentFactory.createAssignment(
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

    fun replyCurrent(reply: R) {
        val updated = exercise.processReply(repository, card, reply)
        recordCardStudied(card.state.status, updated.state.status, journal)
        rescheduleIfNeeded(card)
        showNextOrComplete()
    }

    private fun rescheduleIfNeeded(card: CardWithState<S>) {
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
        undoCardStudied(undone.state.status, journal, newState.status != Status.RELEARN)
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

    fun refetchCard(cardWithState: CardWithState<S>) {
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

    private fun renderCard(card: CardWithState<S>) {
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

    interface Listener<S : State> {
        fun onRender(card: CardWithState<S>, extras: List<ExpressionExtras>)
        fun onFinish()
    }

    interface Factory<S : State, R> {
        fun createLearningFlow(
                deck: Deck,
                cardType: CardType,
                studyOrder: StudyOrder,
                exercise: Exercise<S, R>,
                listener: Listener<S>
        ) : LearningFlow<S, R>
    }
}
class LearningFlowFactory(
        private val repository: Repository,
        private val assignmentFactory: AssignmentFactory,
        private val journal: Journal
) : LearningFlow.Factory<SRState, SRAnswer> {
    override fun createLearningFlow(
            deck: Deck,
            cardType: CardType,
            studyOrder: StudyOrder,
            exercise: Exercise<SRState, SRAnswer>,
            listener: LearningFlow.Listener<SRState>
    ): LearningFlow<SRState, SRAnswer> {
        return LearningFlow(repository, assignmentFactory, deck, cardType, studyOrder, exercise, journal, listener)
    }
}