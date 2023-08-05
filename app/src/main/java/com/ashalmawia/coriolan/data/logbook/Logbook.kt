package com.ashalmawia.coriolan.data.logbook

import com.ashalmawia.coriolan.learning.exercise.CardAction
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.DeckId
import org.joda.time.DateTime

interface Logbook {

    fun cardsStudiedOnDate(date: DateTime): Map<CardAction, Int>
    fun cardsStudiedOnDate(date: DateTime, exercise: ExerciseId): Map<CardAction, Int>
    fun cardsStudiedOnDate(date: DateTime, deckId: DeckId): Map<CardAction, Int>

    fun cardsStudiedOnDateRange(from: DateTime, to: DateTime, decks: List<Deck>): Map<DateTime, Map<CardAction, Int>>

    fun incrementCardActions(date: DateTime, exercise: ExerciseId, deckId: DeckId, cardAction: CardAction)
    fun decrementCardActions(date: DateTime, exercise: ExerciseId, deckId: DeckId, cardAction: CardAction)
}