package com.ashalmawia.coriolan.data

import android.content.Context
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.merger.CardsMerger
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.ExercisesRegistry
import com.ashalmawia.coriolan.model.*

class DecksRegistry(context: Context, val domain: Domain, private val repository: Repository, private val exercisesRegistry: ExercisesRegistry) {

    init {
        val decksCount = repository.allDecks(domain).size
        if (decksCount == 0) {
            addDefaultDeck(context, repository)
        }
    }

    fun allDecks(): List<Deck> {
        return repository.allDecks(domain)
    }

    fun allDecksForLearning(): List<Deck> {
        val realList = repository.allDecks(domain)
        return realList.flatMap { it.splitInfoForwardAndReverse() }
    }

    private fun Deck.splitInfoForwardAndReverse(): List<Deck> {
        return listOf(this.copy(type = CardType.FORWARD), this.copy(type = CardType.REVERSE))
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

    fun addCardToDeck(data: CardData): AddCardResult {
        return addCard(data)
    }

    fun addCardsToDeck(data: List<CardData>) {
        data.forEach { addCard(it) }
    }

    fun editCard(card: Card, cardData: CardData): Card? {
        val original = findOrAddExpression(
                cardData.original, domain.langOriginal(card.type)
        )
        repository.setTranscription(original, cardData.transcription)

        val translations = cardData.translations.map {
            findOrAddExpression(it, domain.langTranslations(card.type))
        }

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
    private fun addCard(cardData: CardData): AddCardResult {
        val original = findOrAddExpression(
                cardData.original, domain.langOriginal()
        )
        // todo: write test that transcription is not overriden by adding a new card with the same expression
        if (cardData.transcription != null)
        repository.setTranscription(original, cardData.transcription)

        val translations = cardData.translations.map {
            findOrAddExpression(it, domain.langTranslations())
        }

        val duplicate = repository.cardByValues(domain, original)
        fun Card.containsAll(translationValues: List<String>): Boolean
                = translationValues.all { value -> this.translations.any { it.value == value } }
        return if (duplicate != null && duplicate.containsAll(cardData.translations)) {
            AddCardResult.Duplicate(duplicate)
        } else {
            addForwardAndReverseWithMerging(original, translations, cardData)
            AddCardResult.Success
        }
    }

    private fun addForwardAndReverseWithMerging(original: Expression, translations: List<Expression>, cardData: CardData) {
        val merger = CardsMerger.create(repository, domain, exercisesRegistry)

        merger.mergeOrAdd(original, translations, cardData.deckId)

        val originalAsList = listOf(original)
        translations.forEach { merger.mergeOrAdd(it, originalAsList, cardData.deckId) }
    }

    private fun findOrAddExpression(
            value: String,
            language: Language
    ): Expression {
        return repository.expressionByValues(value, language) ?: repository.addExpression(value, language)
    }

    private fun addDefaultDeck(context: Context, repository: Repository): Deck {
        return repository.addDeck(domain, context.getString(R.string.decks_default))
    }
}

sealed class AddCardResult {
    object Success : AddCardResult()
    class Duplicate(val card: Card) : AddCardResult()
}