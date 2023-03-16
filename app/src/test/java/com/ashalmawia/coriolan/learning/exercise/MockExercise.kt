package com.ashalmawia.coriolan.learning.exercise

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.StateType
import com.ashalmawia.coriolan.learning.assignment.MockState
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.learning.mutation.Mutations
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.ExpressionExtras
import org.joda.time.DateTime

class MockExercise(override val stableId: String = "mock", override val stateType: StateType = StateType.UNKNOWN) : Exercise<MockState, Any> {

    override val canUndo: Boolean
        get() = true

    override fun isPending(card: CardWithState<MockState>): Boolean {
        return false
    }

    override fun getCardWithState(repository: Repository, card: Card): CardWithState<MockState> {
        return CardWithState(card, MockState())
    }

    override fun updateCardState(repository: Repository, card: CardWithState<MockState>, newState: MockState): CardWithState<MockState> {
        return CardWithState(card.card, newState)
    }

    override fun name(): Int = 0

    override fun pendingCards(repository: Repository, deck: Deck, date: DateTime): List<CardWithState<MockState>> {
        return emptyList()
    }

    override fun onTranslationAdded(repository: Repository, card: Card) {
        repository.updateSRCardState(card, mockEmptySRState(mockToday()), stableId)
    }

    override fun equals(other: Any?): Boolean {
        return other is MockExercise && stableId == other.stableId
    }

    override fun hashCode(): Int {
        return stableId.hashCode()
    }

    override fun mutations(
            repository: Repository,
            preferences: Preferences,
            journal: Journal,
            date: DateTime,
            order: StudyOrder,
            deck: Deck,
            cardType: CardType
    ): Mutations<MockState> {
        return Mutations(listOf())
    }

    override fun processReply(repository: Repository, card: CardWithState<MockState>, answer: Any): CardWithState<MockState> {
        return card
    }

    override fun createRenderer(listener: ExerciseRenderer.Listener<Any>): ExerciseRenderer<MockState, Any> {
        return Renderer()
    }

    class Renderer : ExerciseRenderer<MockState, Any> {
        override fun prepareUi(context: Context, parentView: ViewGroup): View {
            return View(context)
        }

        override fun renderCard(card: CardWithState<MockState>, extras: List<ExpressionExtras>) {
        }
    }
}