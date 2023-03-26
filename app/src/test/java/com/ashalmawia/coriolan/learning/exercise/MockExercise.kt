package com.ashalmawia.coriolan.learning.exercise

import android.content.Context
import android.view.ViewGroup
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.mutation.Mutation
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck
import org.joda.time.DateTime

class MockExercise : Exercise {

    override val id: ExerciseId
        get() = ExerciseId.TEST

    override val canUndo: Boolean
        get() = true

    override fun mutations(preferences: Preferences, journal: Journal, date: DateTime, order: StudyOrder, deck: Deck, cardType: CardType): List<Mutation> {
        return emptyList()
    }

    override fun createExecutor(context: Context, uiContainer: ViewGroup, journal: Journal, listener: ExerciseListener): ExerciseExecutor {
        return MockExerciseExecutor(this)
    }

    override fun status(state: State): Status {
        return Status.NEW
    }

    override fun name(): Int = 0

    override fun pendingCards(deck: Deck, date: DateTime): List<Task> {
        return emptyList()
    }

    override fun onTranslationAdded(card: Card) {
    }

    override fun equals(other: Any?): Boolean {
        return other is MockExercise && id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}