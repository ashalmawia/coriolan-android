package com.ashalmawia.coriolan.learning.exercise

import androidx.annotation.StringRes
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.mutation.Mutations
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

    fun pendingCards(repository: Repository, deck: Deck, date: DateTime): List<CardWithState>

    fun isPending(card: CardWithState): Boolean

    fun getCardWithState(repository: Repository, card: Card): CardWithState

    fun updateCardState(repository: Repository, card: CardWithState, newState: State): CardWithState

    fun mutations(
            repository: Repository,
            preferences: Preferences,
            journal: Journal,
            date: DateTime,
            order: StudyOrder,
            deck: Deck,
            cardType: CardType
    ): Mutations

    fun processReply(repository: Repository, card: CardWithState, answer: Any): CardWithState

    fun onTranslationAdded(repository: Repository, card: Card)

    fun createRenderer(listener: ExerciseRenderer.Listener): ExerciseRenderer
}

enum class ExerciseId(val value: String) {
    SPACED_REPETITION("sr"),

    TEST("test")    // testing only
}