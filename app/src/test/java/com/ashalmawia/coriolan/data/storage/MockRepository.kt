package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.learning.scheduler.CardWithState
import com.ashalmawia.coriolan.learning.scheduler.sr.SRState
import com.ashalmawia.coriolan.learning.scheduler.sr.emptyState
import com.ashalmawia.coriolan.model.*
import org.joda.time.DateTime

class MockRepository : Repository {
    private val langs = mutableListOf<Language>()
    override fun addLanguage(value: String): Language {
        val lang = Language(langs.size + 1L, value)
        langs.add(lang)
        return lang
    }

    override fun languageById(id: Long): Language? {
        return langs.find { it.id == id }
    }

    val expressions = mutableListOf<Expression>()
    override fun addExpression(value: String, type: ExpressionType, language: Language): Expression {
        val exp = Expression(expressions.size + 1L, value, type, language)
        expressions.add(exp)
        return exp
    }
    override fun expressionById(id: Long): Expression? {
        return expressions.find { it.id == id }
    }
    override fun expressionByValues(value: String, type: ExpressionType, language: Language): Expression? {
        return expressions.find { it.value == value && it.type == type && it.language == language }
    }
    override fun isUsed(expression: Expression): Boolean {
        return cards.any { it.original.id == expression.id || it.translations.any { it.id == expression.id } }
    }
    override fun deleteExpression(expression: Expression) {
        expressions.remove(expression)
    }

    private val domains = mutableListOf<Domain>()
    override fun createDomain(name: String, langOriginal: Language, langTranslations: Language): Domain {
        val domain = Domain(domains.size + 1L, name, langOriginal, langTranslations)
        domains.add(domain)
        return domain
    }
    override fun allDomains(): List<Domain> {
        return domains
    }

    val cards = mutableListOf<Card>()
    override fun addCard(domain: Domain, deckId: Long, original: Expression, translations: List<Expression>): Card {
        val card = Card(
                cards.size + 1L,
                deckId,
                domain,
                original,
                translations
        )
        cards.add(card)
        return card
    }
    override fun cardById(id: Long, domain: Domain): Card? {
        return cards.find { it.id == id }
    }
    override fun cardByValues(domain: Domain, original: Expression, translations: List<Expression>): Card? {
        return cards.find { it.original == original && it.translations == translations }
    }
    override fun updateCard(card: Card, deckId: Long, original: Expression, translations: List<Expression>): Card? {
        if (!cards.contains(card)) {
            return null
        }

        val updated = Card(card.id, deckId, card.domain, original, translations)
        cards.remove(card)
        cards.add(updated)
        return updated
    }
    override fun deleteCard(card: Card) {
        cards.remove(card)
    }
    override fun allCards(domain: Domain): List<Card> {
        return cards
    }

    val decks = mutableListOf<Deck>()
    override fun allDecks(domain: Domain): List<Deck> {
        return decks
    }
    override fun deckById(id: Long, domain: Domain): Deck? {
        return decks.find { it.id == id }
    }
    override fun cardsOfDeck(deck: Deck): List<Card> {
        return cards.filter { it.deckId == deck.id }
    }
    override fun addDeck(domain: Domain, name: String): Deck {
        val deck = Deck(decks.size + 1L, domain, name)
        decks.add(deck)
        return deck
    }
    override fun updateDeck(deck: Deck, name: String): Deck? {
        if (!decks.contains(deck)) {
            throw DataProcessingException("deck is not in the repo: $deck")
        }

        val updated = Deck(deck.id, deck.domain, name)
        decks.remove(deck)
        decks.add(updated)
        return updated
    }
    override fun deleteDeck(deck: Deck): Boolean {
        if (!decks.contains(deck)) {
            throw DataProcessingException("deck $deck was not found")
        }
        return if (cardsOfDeck(deck).isEmpty()) {
            decks.remove(deck)
            true
        } else {
            false
        }
    }

    val states = mutableMapOf<Long, SRState>()
    override fun updateSRCardState(card: Card, state: SRState, exerciseId: String) {
        if (!cards.contains(card)) {
            throw DataProcessingException("card is not in the repo: $card")
        }

        states[card.id] = state
    }
    override fun getSRCardState(card: Card, exerciseId: String): SRState {
        return states[card.id] ?: emptyState()
    }
    override fun cardsDueDate(exerciseId: String, deck: Deck, date: DateTime): List<CardWithState<SRState>> {
        return cardsOfDeck(deck)
                .map { card -> CardWithState(card, getSRCardState(card, exerciseId)) }
                .filter {
                    it.state.due <= date
                }
    }

    override fun invalidateCache() {
        // nothing to do here
    }
}