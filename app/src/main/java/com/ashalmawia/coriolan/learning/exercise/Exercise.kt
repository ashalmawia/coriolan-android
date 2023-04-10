package com.ashalmawia.coriolan.learning.exercise

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.mutation.Mutation
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.learning.CardTypeFilter
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

    fun pendingCards(repository: Repository, deck: Deck, date: DateTime): List<Task>

    fun mutations(
            repository: Repository,
            preferences: Preferences,
            logbook: Logbook,
            date: DateTime,
            order: StudyOrder,
            deck: Deck,
            cardType: CardTypeFilter
    ): List<Mutation>

    fun onTranslationAdded(repository: Repository, card: Card)

    fun createExecutor(
            context: Context,
            repository: Repository,
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