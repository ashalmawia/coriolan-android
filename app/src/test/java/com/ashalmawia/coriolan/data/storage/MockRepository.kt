package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.exercise.mockEmptySRState
import com.ashalmawia.coriolan.learning.exercise.sr.SRState
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.model.*
import org.joda.time.DateTime

class MockRepository : Repository {
    val langs = mutableListOf<Language>()
    override fun addLanguage(value: String): Language {
        val lang = Language(langs.size + 1L, value)
        langs.add(lang)
        return lang
    }

    override fun languageById(id: Long): Language? {
        return langs.find { it.id == id }
    }
    override fun languageByName(name: String): Language? {
        return langs.find { it.value == name }
    }

    private val expressions = mutableListOf<Expression>()
    override fun addExpression(value: String, language: Language): Expression {
        val exp = Expression(expressions.size + 1L, value, language)
        expressions.add(exp)
        return exp
    }
    override fun expressionById(id: Long): Expression? {
        return expressions.find { it.id == id }
    }
    override fun expressionByValues(value: String, language: Language): Expression? {
        return expressions.find { it.value == value && it.language == language }
    }
    override fun isUsed(expression: Expression): Boolean {
        return cards.any { it.original.id == expression.id || it.translations.any { it.id == expression.id } }
    }
    override fun deleteExpression(expression: Expression) {
        expressions.remove(expression)
    }

    private val extras = mutableListOf<ExpressionExtras>()
    override fun setExtra(expression: Expression, type: ExtraType, value: String?) {
        val expressionExtras = extras.find { it.expression.id == expression.id }
        if (value == null) {
            expressionExtras?.apply {
                val new = copy(map = expressionExtras.map.toMutableMap().apply { remove(type) })
                extras.apply {
                    remove(expressionExtras)
                    add(new)
                }
            }
        } else {
            if (expressionExtras == null) {
                extras.add(ExpressionExtras(expression, mapOf(type to mockExtra(value))))
            } else {
                val new = expressionExtras.copy(map = expressionExtras.map.toMutableMap().apply {
                    put(type, mockExtra(value))
                })

                extras.apply {
                    remove(expressionExtras)
                    add(new)
                }
            }
        }
    }
    override fun allExtrasForExpression(expression: Expression): ExpressionExtras {
        return extras.find { it.expression.id == expression.id } ?: ExpressionExtras(expression, mapOf())
    }
    override fun allExtrasForCard(card: Card): List<ExpressionExtras> {
        return card.translations.plus(card.original)
                .mapNotNull { expression -> extras.find { it.expression.id == expression.id } }
    }

    private val domains = mutableListOf<Domain>()
    override fun createDomain(name: String?, langOriginal: Language, langTranslations: Language): Domain {
        val domain = Domain(domains.size + 1L, name, langOriginal, langTranslations)
        domains.add(domain)
        return domain
    }
    override fun domainById(id: Long): Domain? = domains.find { it.id == id }
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
    override fun cardByValues(domain: Domain, original: Expression): Card? {
        return cards.find { it.original.id == original.id }
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
        return states[card.id] ?: mockEmptySRState(mockToday())
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

fun Repository.justAddExpression(value: String, language: Language) =
        addExpression(value, language)