package com.ashalmawia.coriolan.learning.exercise

import android.content.Context
import android.view.ViewGroup
import com.ashalmawia.coriolan.learning.CardWithProgress
import com.ashalmawia.coriolan.learning.ExerciseData
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.model.Card

class MockExercise : Exercise {

    override val id: ExerciseId
        get() = ExerciseId.TEST

    override val canUndo: Boolean
        get() = true

    override fun name(): Int = 0

    override fun generateTasks(cards: List<CardWithProgress>): List<Task> {
        return emptyList()
    }

    override fun createRenderer(context: Context, uiContainer: ViewGroup, listener: ExerciseRenderer.Listener): ExerciseRenderer {
        throw NotImplementedError()
    }

    override fun onTaskStudied(card: Card, answer: Any, exerciseData: ExerciseData): Pair<Boolean, ExerciseData> {
        return Pair(false, exerciseData)
    }

    override fun equals(other: Any?): Boolean {
        return other is MockExercise && id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}