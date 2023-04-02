package com.ashalmawia.coriolan.learning.exercise

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.mutation.Mutation
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck
import org.joda.time.DateTime
import java.lang.IllegalArgumentException

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

    fun pendingCards(deck: Deck, date: DateTime): List<Task>

    fun mutations(
            preferences: Preferences,
            logbook: Logbook,
            date: DateTime,
            order: StudyOrder,
            deck: Deck,
            cardType: CardType
    ): List<Mutation>

    fun onTranslationAdded(card: Card)

    fun createExecutor(
            context: Context,
            uiContainer: ViewGroup,
            logbook: Logbook,
            listener: ExerciseListener
    ): ExerciseExecutor
}

enum class ExerciseId(val value: String) {
    FLASHCARDS("flashcards"),

    @VisibleForTesting
    TEST("test");

    companion object {
        fun fromValue(value: String): ExerciseId {
            return values().find { it.value == value } ?: throw IllegalArgumentException("unknown experiment id: $value")
        }
    }
}