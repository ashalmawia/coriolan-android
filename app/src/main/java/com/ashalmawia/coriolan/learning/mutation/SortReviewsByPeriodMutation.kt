package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.exercise.sr.SRState

object SortReviewsByPeriodMutation : Mutation<SRState> {

    override fun apply(cards: List<CardWithState<SRState>>): List<CardWithState<SRState>> {
        return cards.sortedBy { it.state.period }
    }
}