package com.ashalmawia.coriolan.learning.exercise.flashcards

import android.content.Context
import android.view.ViewGroup
import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.TodayManager
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.exercise.ExerciseExecutor
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.exercise.ExerciseListener
import com.ashalmawia.coriolan.learning.exercise.GenericLogbook
import com.ashalmawia.coriolan.model.Card

class FlashcardsExerciseExecutor(
        context: Context,
        private val exercise: Exercise,
        private val repository: Repository,
        logbook: Logbook,
        private val scheduler: SpacedRepetitionScheduler,
        uiContainer: ViewGroup,
        private val listener: ExerciseListener
) : ExerciseExecutor {

    private val renderer = FlashcardsExerciseRenderer(context, uiContainer, this)
    private val logbook = GenericLogbook(logbook, exercise)

    override val exerciseId: ExerciseId
        get() = exercise.id

    private var currentTask: Task? = null

    override fun renderTask(task: Task) {
        currentTask = task
        renderer.renderTask(task)
    }

    override fun onAnswered(answer: Any) {
        val card = currentTask!!
        val oldState = card.learningProgress
        val updated = processReply(card, answer as FlashcardsAnswer)
        logbook.recordCardAction(card.card, oldState, updated.learningProgress)
        listener.onTaskStudied(updated)
    }

    private fun processReply(task: Task, answer: FlashcardsAnswer): Task {
        val newState = scheduler.processAnswer(answer, task.learningProgress.state)
        return updateTask(task, task.learningProgressWithUpdatedExerciseState(newState))
    }

    private fun updateTask(task: Task, newLearningProgress: LearningProgress): Task {
        repository.updateCardLearningProgress(task.card, newLearningProgress)
        return Task(task.card, newLearningProgress, exercise)
    }

    override fun undoTask(task: Task, undoneLearningProgress: LearningProgress): Task {
        val updated = updateTask(task, task.learningProgress)
        logbook.unrecordCardAction(updated.card, updated.learningProgress, undoneLearningProgress)
        return updated
    }

    override fun getTask(card: Card): Task {
        return Task(card, repository.getCardLearningProgress(card), exercise)
    }

    override fun isPending(task: Task): Boolean = task.state().due <= TodayManager.today()
}

private fun Task.state() = learningProgress.state