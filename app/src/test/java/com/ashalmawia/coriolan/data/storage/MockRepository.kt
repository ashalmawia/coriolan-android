package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.learning.scheduler.emptyState
import com.ashalmawia.coriolan.model.*
import java.util.*

class MockRepository : Repository {
    val expressions = mutableListOf<Expression>()
    override fun addExpression(value: String, type: ExpressionType): Expression {
        val exp = Expression(expressions.size + 1L, value, type)
        expressions.add(exp)
        return exp
    }
    override fun expressionById(id: Long): Expression? {
        return expressions.find { it.id == id }
    }

    val cards = mutableListOf<Card>()
    override fun addCard(data: CardData): Card {
        val card = Card(
                cards.size + 1L,
                addExpression(data.original, data.type),
                data.translations.map { addExpression(it, data.type) },
                emptyState()
        )
        cards.add(card)
//        decks.find { it.id == data.deckId }?.add(card)
        return card
    }
    override fun cardById(id: Long): Card? {
        return cards.find { it.id == id }
    }

    val decks = mutableListOf<Deck>()
    override fun allDecks(): List<Deck> {
        return decks
    }
    override fun deckById(id: Long): Deck? {
        return decks.find { it.id == id }
    }
    override fun addDeck(name: String): Deck {
        val deck = Deck(decks.size + 1L, name, listOf())
        decks.add(deck)
        return deck
    }

    val states = mutableMapOf<Long, State>()
    override fun updateCardState(card: Card, state: State, exercise: Exercise): Card {
        states[card.id] = state
        card.state = state
        return card
    }
    override fun cardsDueDate(exercise: Exercise, deck: Deck, date: Date): List<Card> {
        return deck.cards().filter { it.state.due <= date }
    }
}