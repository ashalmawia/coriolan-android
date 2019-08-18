package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.model.Deck

class SplitDeckMutation<S : State>(private val deck: Deck) : Mutation<S> {

    override fun apply(cards: List<CardWithState<S>>): List<CardWithState<S>> {
        return cards.filter { it.card.type == deck.type }
    }
}