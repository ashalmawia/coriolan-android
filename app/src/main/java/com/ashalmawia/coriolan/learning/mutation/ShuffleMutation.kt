package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.scheduler.CardWithState
import com.ashalmawia.coriolan.learning.scheduler.State

class ShuffleMutation(private val shuffle: Boolean) : Mutation {

    override fun <S : State> apply(cards: List<CardWithState<S>>): List<CardWithState<S>> {
        if (shuffle) {
            return cards.shuffled()
        } else {
            return cards
        }
    }
}