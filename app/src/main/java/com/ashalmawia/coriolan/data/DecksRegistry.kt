package com.ashalmawia.coriolan.data

import com.ashalmawia.coriolan.data.merger.CardsMerger
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.exercise.ExercisesRegistry
import com.ashalmawia.coriolan.model.*

class DecksRegistry(
        val domain: Domain,
        private val repository: Repository,
        private val exercisesRegistry: ExercisesRegistry
) {

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

    fun addCardToDeck(data: CardData): AddCardResult {
        return addCard(data)
    }

    fun addCardsToDeck(data: List<CardData>) {
        data.forEach { addCard(it) }
    }

    fun editCard(card: Card, cardData: CardData): Card {
        val original = findOrAddTerm(
                cardData.original, domain.langOriginal(card.type), cardData.transcription
        )
        val translations = cardData.translations.map {
            findOrAddTerm(it, domain.langTranslations(card.type), null)
        }

        val updated = repository.updateCard(card, cardData.deckId, original, translations)
        deleteOrphanTerms(card.translations.plus(card.original))

        return updated
    }

    fun deleteCard(card: Card) {
        val terms = card.translations.plus(card.original)
        repository.deleteCard(card)
        deleteOrphanTerms(terms)
    }

    private fun deleteOrphanTerms(candidates: List<Term>) {
        candidates.forEach { if (!repository.isUsed(it)) repository.deleteTerm(it) }
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
        val original = findOrAddTerm(
                cardData.original, domain.langOriginal(), cardData.transcription
        )
        val translations = cardData.translations.map {
            findOrAddTerm(it, domain.langTranslations(), null)
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

    private fun addForwardAndReverseWithMerging(original: Term, translations: List<Term>, cardData: CardData) {
        val merger = CardsMerger.create(repository, domain, exercisesRegistry)

        merger.mergeOrAdd(original, translations, cardData.deckId)

        val originalAsList = listOf(original)
        translations.forEach { merger.mergeOrAdd(it, originalAsList, cardData.deckId) }
    }

    private fun findOrAddTerm(
            value: String,
            language: Language,
            transcription: String?
    ): Term {
        val existingTerm = repository.termByValues(value, language)
        if (existingTerm == null) {
            return repository.addTerm(value, language, transcription)
        } else {
            return repository.updateTerm(existingTerm, transcription)
        }
    }
}

sealed class AddCardResult {
    object Success : AddCardResult()
    class Duplicate(val card: Card) : AddCardResult()
}