package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.learning.scheduler.emptyState
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

    val domains = mutableListOf<Domain>()
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
                translations,
                emptyState()
        )
        cards.add(card)
        return card
    }
    override fun cardById(id: Long, domain: Domain): Card? {
        return cards.find { it.id == id }
    }
    override fun updateCard(card: Card, deckId: Long, original: Expression, translations: List<Expression>): Card? {
        if (!cards.contains(card)) {
            return null
        }

        val updated = Card(card.id, deckId, card.domain, original, translations, card.state)
        cards.remove(card)
        cards.add(updated)
        return updated
    }
    override fun deleteCard(card: Card) {
        cards.remove(card)
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

    val states = mutableMapOf<Long, State>()
    override fun updateCardState(card: Card, state: State, exercise: Exercise): Card {
        states[card.id] = state
        card.state = state
        return card
    }
    override fun cardsDueDate(exercise: Exercise, deck: Deck, date: DateTime): List<Card> {
        return cardsOfDeck(deck).filter { it.state.due <= date }
    }
}