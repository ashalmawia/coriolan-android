package com.ashalmawia.coriolan.learning

import android.content.Context
import android.view.ViewGroup
import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.assignment.Assignment
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.exercise.ExerciseListener
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck

class LearningFlow(
        context: Context,
        private val repository: Repository,
        private val assignment: Assignment,
        val deck: Deck,
        exercise: Exercise,
        journal: Journal,
        uiContainer: ViewGroup,
        private val listener: Listener
) : ExerciseListener {

    private val exerciseExecutor = exercise.createExecutor(context, uiContainer, journal, this)

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

    override fun onCardStudied(updated: CardWithState) {
        rescheduleIfNeeded(updated)
        showNextOrComplete()
    }

    private fun rescheduleIfNeeded(card: CardWithState) {
        if (exerciseExecutor.isPending(card)) {
            assignment.reschedule(card)
        }
    }

    private fun finish() {
        listener.onFinish()
    }

    fun canUndo() = exerciseExecutor.canUndo && assignment.canUndo()

    fun undo() {
        val card = assignment.current!!
        val stateToUndo = card.state
        val undone = assignment.undo()
        exerciseExecutor.undoCard(undone, stateToUndo)
        renderCard(undone)
    }

    fun refetchCard(cardWithState: CardWithState) {
        val card = cardWithState.card
        val updated = repository.cardById(card.id, card.domain)!!
        val updatedWithState = exerciseExecutor.getCardWithState(updated)
        if (updated.deckId == deck.id && exerciseExecutor.isPending(updatedWithState)) {
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
        exerciseExecutor.renderCard(card, extras)
        listener.onCardRendered()
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
        fun onCardRendered()
        fun onFinish()
    }

    interface Factory {
        fun createLearningFlow(
                context: Context,
                uiContainer: ViewGroup,
                deck: Deck,
                cardType: CardType,
                studyOrder: StudyOrder,
                exercise: Exercise,
                listener: Listener
        ) : LearningFlow
    }
}