package com.ashalmawia.coriolan.learning.exercise.sr

import android.content.Context
import android.view.ViewGroup
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.TodayProvider
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.exercise.ExerciseExecutor
import com.ashalmawia.coriolan.learning.exercise.ExerciseListener
import com.ashalmawia.coriolan.learning.exercise.GenericLogbook
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.ExpressionExtras

class SpacedRepetitionExerciseExecutor(
        context: Context,
        private val exercise: Exercise,
        private val repository: Repository,
        private val todayProvider: TodayProvider,
        journal: Journal,
        private val scheduler: SpacedRepetitionScheduler,
        uiContainer: ViewGroup,
        private val listener: ExerciseListener
) : ExerciseExecutor {

    private val renderer = SpacedRepetitionExerciseRenderer(context, uiContainer, this)
    private val logbook = GenericLogbook(journal, todayProvider, exercise)

    override val canUndo: Boolean = exercise.canUndo

    private var currentTask: Task? = null

    override fun renderTask(task: Task, extras: List<ExpressionExtras>) {
        currentTask = task
        renderer.renderTask(task, extras)
    }

    override fun onAnswered(answer: Any) {
        val card = currentTask!!
        val oldState = card.state
        val updated = processReply(card, answer as SRAnswer)
        logbook.recordCardAction(card.card, oldState, updated.state)
        listener.onTaskStudied(updated)
    }

    private fun processReply(task: Task, answer: SRAnswer): Task {
        val newSrState = scheduler.processAnswer(answer, task.state.spacedRepetition)
        return updateTask(task, task.state.copy(spacedRepetition = newSrState))
    }

    private fun updateTask(task: Task, newState: State): Task {
        repository.updateCardState(task.card, newState)
        return Task(task.card, newState, exercise)
    }

    override fun undoTask(task: Task, undoneState: State): Task {
        val updated = updateTask(task, task.state)
        logbook.unrecordCardAction(updated.card, updated.state, undoneState)
        return updated
    }

    override fun getTask(card: Card): Task {
        return Task(card, repository.getCardState(card), exercise)
    }

    override fun isPending(task: Task): Boolean = task.state().due <= todayProvider.today()
}

private fun Task.state() = state.spacedRepetition