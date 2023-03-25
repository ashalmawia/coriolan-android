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
            renderTask(card)
        } else {
            finish()
        }
    }

    override fun onTaskStudied(updated: Task) {
        rescheduleIfNeeded(updated)
        showNextOrComplete()
    }

    private fun rescheduleIfNeeded(task: Task) {
        if (exerciseExecutor.isPending(task)) {
            assignment.reschedule(task)
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
        exerciseExecutor.undoTask(undone, stateToUndo)
        renderTask(undone)
    }

    fun refetchTask(task: Task) {
        val card = task.card
        val updated = repository.cardById(card.id, card.domain)!!
        val updatedWithState = exerciseExecutor.getTask(updated)
        if (updated.deckId == deck.id && exerciseExecutor.isPending(updatedWithState)) {
            assignment.replace(card, updatedWithState)
            if (isCurrent(card)) {
                // make exercise pre-present the card with the changes
                renderTask(updatedWithState)
            }
        } else {
            dropCard(card)
        }
    }

    private fun renderTask(task: Task) {
        val extras = repository.allExtrasForCard(task.card)
        exerciseExecutor.renderTask(task, extras)
        listener.onTaskRendered()
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
        fun onTaskRendered()
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