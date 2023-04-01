package com.ashalmawia.coriolan.learning.exercise

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.mutation.Mutation
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck
import org.joda.time.DateTime

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

    fun status(state: State): Status
}

enum class ExerciseId(val value: String) {
    FLASHCARDS("flashcards"),

    TEST("test")    // testing only
}