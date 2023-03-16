package com.ashalmawia.coriolan.learning.exercise

import androidx.annotation.StringRes
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.StateType
import com.ashalmawia.coriolan.learning.assignment.Assignment
import com.ashalmawia.coriolan.learning.mutation.Mutations
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck
import org.joda.time.DateTime

interface Exercise<S : State, R> {

    /**
     * Unique string ID which must never be changed.
     * Data storing relies on it.
     */
    val stableId: String

    /**
     * State type is needed to ensure that correct data structures
     * are preinitialized on the first start to store states for this exercise.
     * As stableId, it must never change.
     */
    val stateType: StateType

    /**
     * Each exercise must have it's name
     */
    @StringRes
    fun name(): Int

    val canUndo: Boolean

    fun pendingCards(repository: Repository, deck: Deck, date: DateTime): List<CardWithState<S>>

    fun isPending(card: CardWithState<S>): Boolean

    fun getCardWithState(repository: Repository, card: Card): CardWithState<S>

    fun updateCardState(repository: Repository, card: CardWithState<S>, newState: S): CardWithState<S>

    fun mutations(
            repository: Repository,
            preferences: Preferences,
            journal: Journal,
            date: DateTime,
            order: StudyOrder,
            deck: Deck,
            cardType: CardType
    ): Mutations<S>

    fun processReply(repository: Repository, card: CardWithState<S>, answer: R): CardWithState<S>

    fun onTranslationAdded(repository: Repository, card: Card)

    fun createRenderer(listener: ExerciseRenderer.Listener<R>): ExerciseRenderer<S, R>
}