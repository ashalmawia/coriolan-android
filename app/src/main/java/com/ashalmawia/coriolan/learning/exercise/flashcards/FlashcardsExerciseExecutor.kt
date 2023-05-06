package com.ashalmawia.coriolan.learning.exercise.flashcards

import android.content.Context
import android.view.ViewGroup
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.exercise.ExerciseExecutor
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.exercise.ExerciseListener

class FlashcardsExerciseExecutor(
        context: Context,
        private val exercise: Exercise,
        private val scheduler: SpacedRepetitionScheduler,
        uiContainer: ViewGroup,
        private val listener: ExerciseListener
) : ExerciseExecutor {

    private val renderer = FlashcardsExerciseRenderer(context, uiContainer, this)

    override val exerciseId: ExerciseId
        get() = exercise.id

    private var currentTask: Task? = null

    override fun renderTask(task: Task) {
        currentTask = task
        renderer.renderTask(task)
    }

    override fun onAnswered(answer: Any) {
        val task = currentTask!!
        currentTask = null
        val newState = scheduler.processAnswer(answer as FlashcardsAnswer, task.learningProgress.state)
        val newProgress = task.learningProgress.copy(state = newState)
        listener.onTaskStudied(task, newProgress)
    }
}