package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.StateType
import com.ashalmawia.coriolan.learning.assignment.Assignment
import com.ashalmawia.coriolan.learning.assignment.MockState
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.learning.mutation.Mutations
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import org.joda.time.DateTime

class MockExercise(override val stableId: String = "mock", override val stateType: StateType = StateType.UNKNOWN) : Exercise<MockState, Any> {

    override val canUndo: Boolean
        get() = true

    override fun showCard(card: CardWithState<MockState>) {
    }

    override fun isPending(card: CardWithState<MockState>): Boolean {
        return false
    }

    override fun getCardWithState(repository: Repository, card: Card): CardWithState<MockState> {
        return CardWithState(card, MockState())
    }

    override fun updateCardState(repository: Repository, card: CardWithState<MockState>, newState: MockState): CardWithState<MockState> {
        return CardWithState(card.card, newState)
    }

    override fun processReply(repository: Repository, card: CardWithState<MockState>, answer: Any, assignment: Assignment<MockState>): CardWithState<MockState> {
        return card
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

    override fun mutations(repository: Repository, preferences: Preferences, journal: Journal, date: DateTime, order: StudyOrder, deck: Deck): Mutations<MockState> {
        return Mutations(listOf())
    }
}