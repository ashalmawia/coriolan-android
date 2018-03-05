package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.assignment.Counts
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.model.*
import org.joda.time.DateTime

class InMemoryCache(private val inner: Repository) : Repository {

    private val expressions = mutableMapOf<Long, Expression?>()
    private val domains = mutableMapOf<Long, Domain?>()
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

    override fun expressionByValues(value: String, type: ExpressionType, language: Language): Expression? {
        val found = expressions.values.find { it?.value == value && it.type == type && it.language == language }
        return found ?: inner.expressionByValues(value, type, language)
    }

    override fun isUsed(expression: Expression): Boolean {
        // does not need caching
        return inner.isUsed(expression)
    }

    override fun deleteExpression(expression: Expression) {
        inner.deleteExpression(expression)
        expressions.remove(expression.id)
    }

    override fun createDomain(name: String, langOriginal: Language, langTranslations: Language): Domain {
        val domain = inner.createDomain(name, langOriginal, langTranslations)
        domains[domain.id] = domain
        return domain
    }

    override fun allDomains(): List<Domain> {
        return inner.allDomains()
    }

    override fun addCard(domain: Domain, deckId: Long, original: Expression, translations: List<Expression>): Card {
        val card = inner.addCard(domain, deckId, original, translations)
        cards.put(card.id, card)
        return card
    }

    override fun cardById(id: Long, domain: Domain): Card? {
        if (cards.containsKey(id)) {
            return cards[id]
        }

        val value = inner.cardById(id, domain)
        cards.put(id, value)
        return value
    }

    override fun updateCard(card: Card, deckId: Long, original: Expression, translations: List<Expression>): Card? {
        val updated = inner.updateCard(card, deckId, original, translations)
        cards[card.id] = updated
        return updated
    }

    override fun deleteCard(card: Card) {
        inner.deleteCard(card)
        cards.remove(card.id)
    }

    override fun allDecks(domain: Domain): List<Deck> {
        loadDecksIfNeeded(domain)
        return allDecks.values.filterNotNull().toList()
    }

    override fun deckById(id: Long, domain: Domain): Deck? {
        loadDecksIfNeeded(domain)

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

    override fun addDeck(domain: Domain, name: String): Deck {
        loadDecksIfNeeded(domain)

        val deck = inner.addDeck(domain, name)
        allDecks.put(deck.id, deck)
        return deck
    }

    private fun loadDecksIfNeeded(domain: Domain) {
        if (allDecks.isEmpty()) {
            allDecks.putAll(inner.allDecks(domain).associateBy { it.id })
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