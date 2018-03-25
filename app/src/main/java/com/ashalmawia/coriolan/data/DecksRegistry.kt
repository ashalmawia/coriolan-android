package com.ashalmawia.coriolan.data

import android.content.Context
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.data.merger.CardsMerger
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.ExercisesRegistry
import com.ashalmawia.coriolan.model.*

class DecksRegistry(context: Context, val domain: Domain, private val repository: Repository) {

    companion object {
        private lateinit var instance: DecksRegistry

        fun get(): DecksRegistry {
            return instance
        }

        fun initialize(context: Context, domain: Domain, repository: Repository) {
            instance = DecksRegistry(context, domain, repository)
        }
    }

    init {
        // TODO: must be per domain
        val decksCount = repository.allDecks(domain).size
        if (decksCount == 0) {
            addDefaultDeck(context, repository)
        }
    }

    fun allDecks(): List<Deck> {
        return repository.allDecks(domain)
    }

    fun addDeck(name: String) {
        repository.addDeck(domain, name)
    }

    fun updateDeck(deck: Deck, name: String) {
        repository.updateDeck(deck, name)
    }

    fun deleteDeck(deck: Deck): Boolean {
        return repository.deleteDeck(deck)
    }

    fun addCardToDeck(data: CardData) {
        addCard(data)
    }

    fun addCardsToDeck(data: List<CardData>) {
        data.forEach { addCard(it) }
    }

    fun editCard(card: Card, cardData: CardData): Card? {
        val original = findOrAddExpression(cardData.original, cardData.contentType, domain.langOriginal(card.type))
        val translations = cardData.translations.map { findOrAddExpression(it, cardData.contentType, domain.langTranslations(card.type)) }

        val updated = repository.updateCard(card, cardData.deckId, original, translations)

        if (updated != null) {
            deleteOrphanExpressions(card.translations.plus(card.original))
        }

        return updated
    }

    fun deleteCard(card: Card) {
        val expressions = card.translations.plus(card.original)
        repository.deleteCard(card)
        deleteOrphanExpressions(expressions)
    }

    private fun deleteOrphanExpressions(candidates: List<Expression>) {
        candidates.forEach { if (!repository.isUsed(it)) repository.deleteExpression(it) }
    }

    /**
     * For each card it will add a forward card and all possible reverse cards.
     *
     * E.g. for the input "spring -- 1. весна 2. источник" it will generate 2 cards:
     * 1. forward: "spring -- 1. весна 2. источник"
     * 2. reverse: "весна -- spring"
     * 3. reverse: "источник -- spring"
     */
    private fun addCard(cardData: CardData) {
        val original = findOrAddExpression(cardData.original, cardData.contentType, domain.langOriginal())
        val translations = cardData.translations.map { findOrAddExpression(it, cardData.contentType, domain.langTranslations()) }

        val merger = CardsMerger.create(repository, domain, ExercisesRegistry)

        merger.mergeOrAdd(original, translations, cardData.deckId)

        val originalAsList = listOf(original)
        translations.forEach { merger.mergeOrAdd(it, originalAsList, cardData.deckId) }
    }

    private fun findOrAddExpression(value: String, type: ExpressionType, language: Language): Expression {
        val found = repository.expressionByValues(value, type, language)
        return found ?: repository.addExpression(value, type, language)
    }

    private fun addDefaultDeck(context: Context, repository: Repository): Deck {
        return repository.addDeck(domain, context.getString(R.string.decks_default))
    }
}