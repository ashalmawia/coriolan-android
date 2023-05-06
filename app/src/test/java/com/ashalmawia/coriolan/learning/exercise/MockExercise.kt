package com.ashalmawia.coriolan.learning.exercise

import android.content.Context
import android.view.ViewGroup
import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.CardWithProgress
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.model.Card

class MockExercise : Exercise {

    override val id: ExerciseId
        get() = ExerciseId.TEST

    override val canUndo: Boolean
        get() = true

    override fun createExecutor(context: Context, repository: Repository, uiContainer: ViewGroup, logbook: Logbook, listener: ExerciseListener): ExerciseExecutor {
        return MockExerciseExecutor(this)
    }

    override fun name(): Int = 0

    override fun generateTasks(cards: List<CardWithProgress>): List<Task> {
        return emptyList()
    }

    override fun onTranslationAdded(repository: Repository, card: Card) {
    }

    override fun equals(other: Any?): Boolean {
        return other is MockExercise && id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}