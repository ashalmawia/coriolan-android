package com.ashalmawia.coriolan.data

import android.content.Context
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.*

class DecksRegistry(context: Context, preferences: Preferences, private val repository: Repository) {

    companion object {
        private lateinit var instance: DecksRegistry

        fun get(): DecksRegistry {
            return instance
        }

        fun initialize(context: Context, preferences: Preferences, repository: Repository) {
            instance = DecksRegistry(context, preferences, repository)
        }
    }

    private val def: Deck

    init {
        val defaultDeckId = preferences.getDefaultDeckId()
        if (defaultDeckId != null) {
            def = repository.deckById(defaultDeckId)!!
        } else {
            def = addDefaultDeck(context, repository)
            preferences.setDefaultDeckId(def.id)
        }
    }

    fun default(): Deck {
        return def
    }

    fun allDecks(): List<Deck> {
        return repository.allDecks()
    }

    fun addCardToDeck(data: CardData) {
        addCard(data)
    }

    fun addCardsToDeck(data: List<CardData>) {
        data.map { addCard(it) }
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
        val original = findOrAddExpression(cardData.original, cardData.contentType, cardData.originalLang)
        val translations = cardData.translations.map { findOrAddExpression(it, cardData.contentType, cardData.translationsLang) }

        repository.addCard(cardData.deckId, original, translations)
        translations.forEach { repository.addCard(cardData.deckId, it, listOf(original)) }
    }

    private fun findOrAddExpression(value: String, type: ExpressionType, language: Language): Expression {
        val found = repository.expressionByValues(value, type, language)
        return found ?: repository.addExpression(value, type, language)
    }

    private fun addDefaultDeck(context: Context, repository: Repository): Deck {
        return repository.addDeck(context.getString(R.string.decks_default))
    }
}