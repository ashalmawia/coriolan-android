package com.ashalmawia.coriolan.data.merger

import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.exercise.ExercisesRegistry
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.Term

class CardsMergerImpl(
        private val repository: Repository,
        private val domain: Domain,
        private val exercisesRegistry: ExercisesRegistry
): CardsMerger {

    override fun mergeOrAdd(original: Term, translations: List<Term>, deckId: Long) {
        val originalMatch = repository.cardByValues(domain, original)
        if (originalMatch == null) {
            addCard(original, translations, deckId)
        } else {
            mergeTranslations(originalMatch, translations, deckId)
        }

        // that's it, we don't care if there are matching translations
    }

    private fun addCard(original: Term, translations: List<Term>, deckId: Long) {
        repository.addCard(domain, deckId, original, translations)
    }

    private fun mergeTranslations(card: Card, translations: List<Term>, deckId: Long) {
        val mergedTranslations = card.translations.plus(translations).distinctBy { it.id }
        if (mergedTranslations == card.translations) {
            return
        }

        val updated = repository.updateCard(card, deckId, card.original, mergedTranslations)
        notifyExercises(updated)
    }

    private fun notifyExercises(card: Card) {
        exercisesRegistry.allExercises().forEach { it.onTranslationAdded(repository, card) }
    }
}