package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.learning.scheduler.CardWithState
import com.ashalmawia.coriolan.learning.scheduler.sr.SRState
import com.ashalmawia.coriolan.model.*
import org.joda.time.DateTime

class InMemoryCache(private val inner: Repository) : Repository {

    private val expressions = mutableMapOf<Long, Expression?>()
    private val domains = mutableMapOf<Long, Domain?>()
    private val cards = mutableMapOf<Long, Card?>()
    private val allDecks = mutableMapOf<Long, Deck?>()  // must be all decks as we have a function to return them all

    private var allCardsLoaded = false

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

    override fun cardByValues(domain: Domain, original: Expression): Card? {
        val cached = cards.values.filterNotNull()
                .find { it.domain == domain && it.original == original }
        return cached ?: inner.cardByValues(domain, original)
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

    override fun allCards(domain: Domain): List<Card> {
        return if (allCardsLoaded) {
            cards.values.filterNotNull().distinctBy { it.id }
        } else {
            val read = inner.allCards(domain)
            cards.clear()
            cards.putAll(read.associateBy { it.id })
            allCardsLoaded = true
            read
        }
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

    override fun updateDeck(deck: Deck, name: String): Deck? {
        val updated = inner.updateDeck(deck, name)
        allDecks[deck.id] = updated
        return updated
    }

    override fun deleteDeck(deck: Deck): Boolean {
        val deleted = inner.deleteDeck(deck)
        if (deleted) {
            allDecks.remove(deck.id)
        }
        return deleted
    }

    private fun loadDecksIfNeeded(domain: Domain) {
        if (allDecks.isEmpty()) {
            allDecks.putAll(inner.allDecks(domain).associateBy { it.id })
        }
    }

    override fun updateSRCardState(card: Card, state: SRState, exerciseId: String) {
        return inner.updateSRCardState(card, state, exerciseId)
    }

    override fun getSRCardState(card: Card, exerciseId: String): SRState {
        return inner.getSRCardState(card, exerciseId)
    }

    override fun cardsDueDate(exerciseId: String, deck: Deck, date: DateTime): List<CardWithState<SRState>> {
        return inner.cardsDueDate(exerciseId, deck, date)
    }

    override fun invalidateCache() {
        expressions.clear()
        domains.clear()
        cards.clear()
        allDecks.clear()
        allCardsLoaded = false
    }
}