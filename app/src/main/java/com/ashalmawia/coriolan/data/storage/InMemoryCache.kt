package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.assignment.Counts
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.model.*
import org.joda.time.DateTime

class InMemoryCache(private val inner: Repository) : Repository {

    private val expressions = mutableMapOf<Long, Expression?>()
    private val cards = mutableMapOf<Long, Card?>()
    private val allDecks = mutableMapOf<Long, Deck?>()  // must be all decks as we have a function to return them all

    override fun addLanguage(value: String): Language {
        // no need to cache languages as it's immutable data that isn't normally queried
        return inner.addLanguage(value)
    }

    override fun languageById(id: Long): Language? {
        // no need to cache languages as it's immutable data that isn't normally queried
        return inner.languageById(id)
    }

    override fun addExpression(value: String, type: ExpressionType, language: Language): Expression {
        val expression = inner.addExpression(value, type, language)
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

    override fun cardsOfDeck(deck: Deck): List<Card> {
        val result = inner.cardsOfDeck(deck)
        result.forEach { cards[it.id] = it }
        return result
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

    override fun updateCardState(card: Card, state: State, exercise: Exercise): Card {
        // this does not need to be cached
        return inner.updateCardState(card, state, exercise)
    }

    override fun cardsDueDate(exercise: Exercise, deck: Deck, date: DateTime): List<Card> {
        val due = inner.cardsDueDate(exercise, deck, date)
        for (card in due) {
            cards[card.id] = card
        }
        return due
    }

    override fun cardsDueDateCount(exercise: Exercise, deck: Deck, date: DateTime): Counts {
        return inner.cardsDueDateCount(exercise, deck, date)
    }
}