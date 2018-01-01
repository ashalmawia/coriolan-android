package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Expression
import com.ashalmawia.coriolan.model.ExpressionType

class InMemoryCache(private val inner: Storage) : Storage {

    private val expressions = mutableMapOf<Long, Expression?>()
    private val cards = mutableMapOf<Long, Card?>()
    private val allDecks = mutableMapOf<Long, Deck?>()  // must be all decks as we have a function to return them all

    override fun addExpression(value: String, type: ExpressionType): Expression {
        val expression = inner.addExpression(value, type)
        expressions.put(expression.id, expression)
        return expression
    }

    override fun expressionById(id: Long): Expression? {
        if (expressions.containsKey(id)) {
            return expressions[id]
        }

        val value = inner.expressionById(id)
        expressions.put(id, value)
        return value
    }

    override fun addCard(data: CardData): Card {
        val card = inner.addCard(data)
        cards.put(card.id, card)
        val deck = deckById(data.deckId)
        deck?.add(card)
        return card
    }

    override fun cardById(id: Long): Card? {
        if (cards.containsKey(id)) {
            return cards[id]
        }

        val value = inner.cardById(id)
        cards.put(id, value)
        return value
    }

    override fun allDecks(): List<Deck> {
        loadDecksIfNeeded()
        return allDecks.values.filterNotNull().toList()
    }

    override fun deckById(id: Long): Deck? {
        loadDecksIfNeeded()

        return if (allDecks.containsKey(id)) {
            allDecks[id]
        } else {
            null
        }
    }

    override fun addDeck(name: String): Deck {
        loadDecksIfNeeded()

        val deck = inner.addDeck(name)
        allDecks.put(deck.id, deck)
        return deck
    }

    private fun loadDecksIfNeeded() {
        if (allDecks.isEmpty()) {
            allDecks.putAll(inner.allDecks().associateBy { it.id })
        }
    }
}