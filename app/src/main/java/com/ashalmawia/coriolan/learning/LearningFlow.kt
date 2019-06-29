package com.ashalmawia.coriolan.learning

import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.assignment.Assignment
import com.ashalmawia.coriolan.learning.assignment.AssignmentFactory
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck

class LearningFlow<S : State, R>(
        private val repository: Repository,
        assignmentFactory: AssignmentFactory,
        val deck: Deck,
        studyOrder: StudyOrder,
        val exercise: Exercise<S, R>,
        val journal: Journal
) {

    private val assignment: Assignment<S> = assignmentFactory.createAssignment(
            studyOrder,
            exercise,
            deck
    )

    private val finishListeners = mutableListOf<FinishListener>()

    val card
        get() = assignment.current!!

    val counts: Counts
        get() = assignment.counts()

    fun showNextOrComplete() {
        if (assignment.hasNext()) {
            val card = assignment.next()
            exercise.showCard(card)
        } else {
            finish()
        }
    }

    fun replyCurrent(reply: R) {
        val updated = exercise.processReply(repository, card, reply, assignment)
        recordCardStudied(card.state.status, updated.state.status, journal)
        showNextOrComplete()
    }

    fun addFinishListener(listener: FinishListener) {
        finishListeners.add(listener)
    }

    fun removeFinishListener(listener: FinishListener) {
        finishListeners.remove(listener)
    }

    private fun finish() {
        finishListeners.forEach { it() }
    }

    fun canUndo() = exercise.canUndo && assignment.canUndo()

    fun undo() {
        val newState = assignment.current!!.state
        val undone = assignment.undo()
        exercise.updateCardState(repository, undone, undone.state)
        undoCardStudied(undone.state.status, journal, newState.status != Status.RELEARN)
        exercise.showCard(undone)
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
                exercise.showCard(updatedWithState)
            }
        } else {
            dropCard(card)
        }
    }

    fun dropCard(card: Card) {
        val removingCurrent = isCurrent(card)
        assignment.delete(card)
        if (removingCurrent) {
            showNextOrComplete()
        }
    }

    private fun isCurrent(card: Card) = this.card.card.id == card.id
}

typealias FinishListener = () -> Unit