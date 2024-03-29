package com.ashalmawia.coriolan.data.logbook

import com.ashalmawia.coriolan.learning.exercise.CardAction
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.DeckId
import com.ashalmawia.coriolan.util.orZero
import org.joda.time.DateTime

class MockLogbook : Logbook {

    private val data = mutableMapOf<CardAction, Int>()

    fun setTodayLearned(new: Int, review: Int) {
        data[CardAction.NEW_CARD_FIRST_SEEN] = new
        data[CardAction.CARD_REVIEWED] = review
    }

    override fun cardsStudiedOnDate(date: DateTime): Map<CardAction, Int> {
        return data
    }

    override fun cardsStudiedOnDate(date: DateTime, exercise: ExerciseId): Map<CardAction, Int> {
        return data
    }

    override fun cardsStudiedOnDate(date: DateTime, deckId: DeckId): Map<CardAction, Int> {
        return data
    }

    override fun incrementCardActions(date: DateTime, exercise: ExerciseId, deckId: DeckId, cardAction: CardAction) {
        data[cardAction] = data[cardAction].orZero() + 1
    }

    override fun decrementCardActions(date: DateTime, exercise: ExerciseId, deckId: DeckId, cardAction: CardAction) {
        data[cardAction] = data[cardAction].orZero() - 1
    }

    override fun cardsStudiedOnDateRange(from: DateTime, to: DateTime, decks: List<Deck>): Map<DateTime, Map<CardAction, Int>> {
        return emptyMap()
    }
}