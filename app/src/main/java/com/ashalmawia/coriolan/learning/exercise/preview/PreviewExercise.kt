package com.ashalmawia.coriolan.learning.exercise.preview

import android.content.Context
import android.view.ViewGroup
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.learning.CardWithProgress
import com.ashalmawia.coriolan.learning.ExerciseData
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.exercise.ExerciseRenderer
import com.ashalmawia.coriolan.model.Card

class PreviewExercise : Exercise {

    override val id: ExerciseId
        get() = ExerciseId.PREVIEW

    override fun name() = R.string.exercise_preview

    override val canUndo: Boolean
        get() = true

    override fun generateTasks(cards: List<CardWithProgress>): List<Task> {
        return cards
                .filter { it.status == Status.NEW }
                .map { Task(it.card, it.learningProgress, this) }
    }

    override fun onTaskStudied(card: Card, answer: Any, exerciseData: ExerciseData): Pair<Boolean, ExerciseData> {
        return Pair(false, exerciseData)
    }

    override fun createRenderer(context: Context, uiContainer: ViewGroup, listener: ExerciseRenderer.Listener): ExerciseRenderer {
        return PreviewExerciseRenderer(context, uiContainer, listener)
    }
}