package com.ashalmawia.coriolan.learning.exercise

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.CardWithProgress
import com.ashalmawia.coriolan.learning.ExerciseData
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.exercise.flashcards.SpacedRepetitionScheduler
import com.ashalmawia.coriolan.model.Card

interface Exercise {

    /**
     * Unique string ID which must never be changed.
     * Data storing relies on it.
     */
    val id: ExerciseId

    /**
     * Each exercise must have it's name
     */
    @StringRes
    fun name(): Int

    val canUndo: Boolean

    fun generateTasks(cards: List<CardWithProgress>): List<Task>

    fun onTranslationAdded(card: Card, exerciseData: ExerciseData): ExerciseData {
        return exerciseData
    }

    fun createExecutor(
            context: Context,
            repository: Repository,
            scheduler: SpacedRepetitionScheduler,
            uiContainer: ViewGroup,
            listener: ExerciseListener
    ): ExerciseExecutor
}

enum class ExerciseId(val value: String) {
    PREVIEW("preview"),
    FLASHCARDS("flashcards"),

    @VisibleForTesting
    TEST("test");

    companion object {
        fun fromValue(value: String): ExerciseId {
            return values().find { it.value == value } ?: throw IllegalArgumentException("unknown experiment id: $value")
        }
    }
}