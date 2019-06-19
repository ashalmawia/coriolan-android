package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.util.forwardWithState
import com.ashalmawia.coriolan.util.reverseWithState

class CardTypeMixedMutation<S : State> : Mutation.CardTypeFilter<S>() {
    override fun apply(cards: List<CardWithState<S>>): List<CardWithState<S>> = cards
}

class CardTypeForwardOnlyMutation<S : State> : Mutation.CardTypeFilter<S>() {
    override fun apply(cards: List<CardWithState<S>>): List<CardWithState<S>> = cards.forwardWithState()
}

class CardTypeReverseOnlyMutation<S : State> : Mutation.CardTypeFilter<S>() {
    override fun apply(cards: List<CardWithState<S>>): List<CardWithState<S>> = cards.reverseWithState()
}