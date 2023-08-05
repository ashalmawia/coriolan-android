package com.ashalmawia.coriolan.data.merger

import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.SchedulingState
import com.ashalmawia.coriolan.learning.exercise.ExercisesRegistry
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.DeckId
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.Term

class CardsMergerImpl(
        private val repository: Repository,
        private val domain: Domain,
        private val exercisesRegistry: ExercisesRegistry
): CardsMerger {

    override fun mergeOrAdd(original: Term, translations: List<Term>, deckId: DeckId) {
        val originalMatch = repository.cardByValues(domain, original)
        if (originalMatch == null) {
            addCard(original, translations, deckId)
        } else {
            mergeTranslations(originalMatch, translations, deckId)
        }

        // that's it, we don't care if there are matching translations
    }

    private fun addCard(original: Term, translations: List<Term>, deckId: DeckId) {
        repository.addCard(domain, deckId, original, translations)
    }

    private fun mergeTranslations(card: Card, translations: List<Term>, deckId: DeckId) {
        val mergedTranslations = card.translations.plus(translations).distinctBy { it.id }
        if (mergedTranslations == card.translations) {
            return
        }

        val updated = repository.updateCard(card, deckId, card.original, mergedTranslations)
        resetLearningProgress(updated)
    }

    private fun resetLearningProgress(card: Card) {
        val learningProgress = repository.getCardLearningProgress(card)
        var exerciseData = learningProgress.exerciseData

        // let exercises update their payload
        exercisesRegistry.allExercises().forEach {
            exerciseData = it.onTranslationAdded(card, exerciseData)
        }

        val updatedLearningProgress = learningProgress.copy(
                state = SchedulingState.new(), exerciseData = exerciseData
        )
        repository.updateCardLearningProgress(card, updatedLearningProgress)
    }
}