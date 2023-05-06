package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithProgress

object SortReviewsByIntervalMutation : Mutation {

    override fun apply(cards: List<CardWithProgress>): List<CardWithProgress> {
        return cards.sortedBy { it.learningProgress.flashcards.interval }
    }
}