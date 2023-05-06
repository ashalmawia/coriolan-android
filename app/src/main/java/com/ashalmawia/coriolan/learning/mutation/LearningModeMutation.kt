package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.CardWithProgress
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Term

/**
 * Leave only those reverse cards for which forward is already in progress.
 */
class LearningModeMutation(private val repository: Repository) : Mutation {

    override fun apply(cards: List<CardWithProgress>): List<CardWithProgress> {
        val (forward, reverse) = cards.splitForwardAndReverse()
        return forward.plus(reverse.filterReady())
    }

    private fun List<CardWithProgress>.filterReady() : List<CardWithProgress> {
        val cardsAndTranslationsIds = flatMap { it.card.translations }.map { it.id }
                .plus(this.map { it.card.id })

        val states = repository.getStatesForCardsWithOriginals(cardsAndTranslationsIds)

        return filter {
            it.card.alreadySeen(states) || it.card.translations.all { exp -> exp.isReady(states) }
        }
    }

    private fun Card.alreadySeen(states: Map<Long, LearningProgress>): Boolean {
        return (states[id]?.globalStatus ?: Status.NEW) != Status.NEW
    }

    private fun Term.isReady(states: Map<Long, LearningProgress>): Boolean {
        val state = states[id]
        return state != null && state.flashcards.interval >= 4
    }
}

private fun List<CardWithProgress>.splitForwardAndReverse() = partition { it.card.type == CardType.FORWARD }