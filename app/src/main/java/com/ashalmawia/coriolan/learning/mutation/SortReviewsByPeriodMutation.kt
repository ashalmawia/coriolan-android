package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithState

object SortReviewsByPeriodMutation : Mutation {

    override fun apply(cards: List<CardWithState>): List<CardWithState> {
        return cards.sortedBy { it.state.spacedRepetition.period }
    }
}