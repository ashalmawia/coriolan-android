package com.ashalmawia.coriolan.learning

import android.content.Context
import android.view.ViewGroup
import com.ashalmawia.coriolan.model.Counts
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.assignment.Assignment
import com.ashalmawia.coriolan.learning.assignment.ReschedulingStrategy
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.exercise.ExerciseRenderer
import com.ashalmawia.coriolan.learning.exercise.ExercisesRegistry
import com.ashalmawia.coriolan.learning.exercise.LogbookWriter
import com.ashalmawia.coriolan.learning.exercise.flashcards.SpacedRepetitionScheduler
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.learning.CardTypeFilter
import com.ashalmawia.coriolan.ui.learning.CardAnswer

class LearningFlow(
        context: Context,
        private val repository: Repository,
        private val assignment: Assignment,
        val deck: Deck,
        exercisesRegistry: ExercisesRegistry,
        private val scheduler: SpacedRepetitionScheduler,
        private val logbook: LogbookWriter,
        uiContainer: ViewGroup,
        private val listener: Listener
) : ExerciseRenderer.Listener {

    private val exercises: List<Exercise> = exercisesRegistry.enabledExercises()
    private val renderers: Map<ExerciseId, ExerciseRenderer> = exercises
            .associate { Pair(it.id, it.createRenderer(context, uiContainer, this)) }

    val current
        get() = assignment.current!!

    val counts: Counts
        get() = assignment.counts()

    fun showNextOrComplete() {
        if (assignment.hasNext()) {
            val card = assignment.next()
            renderTask(card)
        } else {
            val emptyAssignment = assignment.originalCount == 0
            finish(emptyAssignment)
        }
    }

    override fun onAnswered(answer: CardAnswer) {
        val task = current

        when (answer) {
            CardAnswer.ACCEPT -> acceptNewWord(task)
            else -> reschedule(task, answer)
        }
        showNextOrComplete()
    }

    private fun acceptNewWord(task: Task) {
        val newTasks = exercises.mapNotNull { it.onNewWordAccepted(task.card, task.learningProgress) }
        newTasks.forEach { assignment.reschedule(it, ReschedulingStrategy.SOON) }
    }

    private fun reschedule(task: Task, answer: CardAnswer) {
        val newState = scheduler.processAnswer(answer, task.learningProgress.state)
        val (shouldReschedule, newData) = task.exercise.onTaskStudied(task.card, answer, task.learningProgress.exerciseData)
        val newProgress = task.learningProgress.copy(state = newState, exerciseData = newData)
        val updated = updateTask(task, newProgress)

        if (shouldReschedule) {
            assignment.reschedule(updated, ReschedulingStrategy.MEDIUM)
        }
        logbook.recordCardAction(task, newProgress.state)
    }

    private fun finish(emptyAssignment: Boolean) {
        listener.onFinish(emptyAssignment)
    }

    private fun Task.renderer(): ExerciseRenderer {
        val exerciseId = this.exercise.id
        return renderers[exerciseId]
                ?: throw IllegalStateException("could not find executor for exercise $exerciseId")
    }

    fun canUndo() = assignment.canUndo()

    fun undo() {
        val task = assignment.current!!
        val progressToUndo = task.learningProgress
        val undone = assignment.undo()
        updateTask(task, undone.learningProgress)
        logbook.unrecordCardAction(undone, progressToUndo.state)
        renderTask(undone)
    }

    fun refetchTask(task: Task) {
        val card = task.card
        val updated = repository.cardById(card.id, card.domain)
        if (updated == null) {
            // card was deleted
            dropCard(card)
            return
        }

        val updatedWithState = createTask(updated, task.exercise)
        if (updated.deckId == deck.id && updatedWithState.isPending()) {
            assignment.replace(card, updatedWithState)
            if (isCurrent(card)) {
                // make exercise pre-present the card with the changes
                renderTask(updatedWithState)
            }
        } else {
            dropCard(card)
        }
    }

    private fun updateTask(task: Task, newLearningProgress: LearningProgress): Task {
        repository.updateCardLearningProgress(task.card, newLearningProgress)
        return Task(task.card, newLearningProgress, task.exercise)
    }

    private fun renderTask(task: Task) {
        task.renderer().renderTask(task)
        listener.onTaskRendered()
    }

    fun dropCard(card: Card) {
        val removingCurrent = isCurrent(card)
        assignment.delete(card)
        if (removingCurrent) {
            showNextOrComplete()
        }
    }

    private fun isCurrent(card: Card) = this.current.card.id == card.id

    private fun createTask(card: Card, exercise: Exercise): Task {
        return Task(card, repository.getCardLearningProgress(card), exercise)
    }

    interface Listener {
        fun onTaskRendered()
        fun onFinish(emptyAssignment: Boolean)
    }

    interface Factory {
        fun createLearningFlow(
                context: Context,
                uiContainer: ViewGroup,
                deck: Deck,
                cardTypeFilter: CardTypeFilter,
                studyOrder: StudyOrder,
                studyTargets: StudyTargets,
                listener: Listener
        ) : LearningFlow
    }
}