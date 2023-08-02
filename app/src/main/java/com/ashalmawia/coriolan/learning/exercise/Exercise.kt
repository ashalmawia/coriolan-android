package com.ashalmawia.coriolan.learning.exercise

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import com.ashalmawia.coriolan.learning.CardWithProgress
import com.ashalmawia.coriolan.learning.ExerciseData
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.Task
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

    fun onNewWordAccepted(card: Card, learningProgress: LearningProgress): Task? = null

    /**
     * Returns: Pair of
     *      - should reschedule (true if the answer was wrong)
     *      - udpated exercise data for this card
     */
    fun onTaskStudied(card: Card, answer: Any, exerciseData: ExerciseData): Pair<Boolean, ExerciseData>

    fun createRenderer(
            context: Context,
            uiContainer: ViewGroup,
            listener: ExerciseRenderer.Listener
    ): ExerciseRenderer
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