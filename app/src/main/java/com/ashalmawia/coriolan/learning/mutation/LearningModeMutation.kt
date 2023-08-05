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
                .plus(this.map { it.card.original.id })

        val progresses = repository.getProgressForCardsWithOriginals(cardsAndTranslationsIds)

        return filter {
            it.card.alreadySeen(progresses) || it.card.translations.all { exp -> exp.isReady(progresses) }
        }
    }

    private fun Card.alreadySeen(states: Map<Long, LearningProgress>): Boolean {
        return (states[original.id]?.status ?: Status.NEW) != Status.NEW
    }

    private fun Term.isReady(progresses: Map<Long, LearningProgress>): Boolean {
        val progress = progresses[id]
        return progress != null && progress.state.interval >= 4
    }
}

private fun List<CardWithProgress>.splitForwardAndReverse() = partition { it.card.type == CardType.FORWARD }