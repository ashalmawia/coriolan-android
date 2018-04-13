package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.scheduler.CardWithState
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.util.forwardWithState
import com.ashalmawia.coriolan.util.reverseWithState

class CardTypeForwardFirstMutation : Mutation.CardTypeFilter() {
    override fun <S : State> apply(cards: List<CardWithState<S>>): List<CardWithState<S>>
            = cards.forwardWithState().plus(cards.reverseWithState())
}

class CardTypeReverseFirstMutation : Mutation.CardTypeFilter() {
    override fun <S : State> apply(cards: List<CardWithState<S>>): List<CardWithState<S>>
            = cards.reverseWithState().plus(cards.forwardWithState())
}

class CardTypeMixedMutation : Mutation.CardTypeFilter() {
    override fun <S : State> apply(cards: List<CardWithState<S>>): List<CardWithState<S>> = cards
}

class CardTypeForwardOnlyMutation : Mutation.CardTypeFilter() {
    override fun <S : State> apply(cards: List<CardWithState<S>>): List<CardWithState<S>> = cards.forwardWithState()
}

class CardTypeReverseOnlyMutation : Mutation.CardTypeFilter() {
    override fun <S : State> apply(cards: List<CardWithState<S>>): List<CardWithState<S>> = cards.reverseWithState()
}