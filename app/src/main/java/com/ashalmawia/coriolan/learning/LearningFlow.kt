package com.ashalmawia.coriolan.learning

import android.content.Context
import android.view.ViewGroup
import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.assignment.Assignment
import com.ashalmawia.coriolan.learning.exercise.ExerciseExecutor
import com.ashalmawia.coriolan.learning.exercise.ExerciseListener
import com.ashalmawia.coriolan.learning.exercise.ExercisesRegistry
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck

class LearningFlow(
        context: Context,
        private val repository: Repository,
        private val assignment: Assignment,
        val deck: Deck,
        exercisesRegistry: ExercisesRegistry,
        logbook: Logbook,
        uiContainer: ViewGroup,
        private val listener: Listener
) : ExerciseListener {

    private val executors = exercisesRegistry.enabledExercises()
            .map { it.createExecutor(context, uiContainer, logbook, this) }

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
        if (task.executor().isPending(task)) {
            assignment.reschedule(task)
        }
    }

    private fun finish() {
        listener.onFinish()
    }

    private fun Task.executor(): ExerciseExecutor {
        val exerciseId = this.exercise.id
        return executors.find { it.exerciseId == exerciseId }
                ?: throw IllegalStateException("could not find executor for exercise $exerciseId")
    }

    fun canUndo() = assignment.canUndo()

    fun undo() {
        val card = assignment.current!!
        val stateToUndo = card.state
        val undone = assignment.undo()
        undone.executor().undoTask(undone, stateToUndo)
        renderTask(undone)
    }

    fun refetchTask(task: Task) {
        val card = task.card
        val updated = repository.cardById(card.id, card.domain)!!
        val updatedWithState = task.executor().getTask(updated)
        if (updated.deckId == deck.id && task.executor().isPending(updatedWithState)) {
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
        task.executor().renderTask(task)
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
                listener: Listener
        ) : LearningFlow
    }
}