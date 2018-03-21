package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.data.prefs.CardTypePreference
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.learning.scheduler.CardWithState
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.util.forwardWithState
import com.ashalmawia.coriolan.util.reverseWithState

abstract class CardTypeFilterMutation : Mutation {

    companion object {
        fun from(preferences: Preferences): CardTypeFilterMutation {
            val cardType = preferences.getCardTypePreference()
                    ?: CardTypePreference.MIXED  // does no effect

            return when (cardType) {
                CardTypePreference.FORWARD_FIRST -> CardTypeForwardFirstMutation()
                CardTypePreference.REVERSE_FIRST -> CardTypeReverseFirstMutation()
                CardTypePreference.MIXED -> CardTypeMixedMutation()
                CardTypePreference.FORWARD_ONLY -> CardTypeForwardOnlyMutation()
                CardTypePreference.REVERSE_ONLY -> CardTypeReverseOnlyMutation()
            }
        }
    }
}

class CardTypeForwardFirstMutation : CardTypeFilterMutation() {
    override fun <S : State> apply(cards: List<CardWithState<S>>): List<CardWithState<S>>
            = cards.forwardWithState().plus(cards.reverseWithState())
}

class CardTypeReverseFirstMutation : CardTypeFilterMutation() {
    override fun <S : State> apply(cards: List<CardWithState<S>>): List<CardWithState<S>>
            = cards.reverseWithState().plus(cards.forwardWithState())
}

class CardTypeMixedMutation : CardTypeFilterMutation() {
    override fun <S : State> apply(cards: List<CardWithState<S>>): List<CardWithState<S>> = cards
}

class CardTypeForwardOnlyMutation : CardTypeFilterMutation() {
    override fun <S : State> apply(cards: List<CardWithState<S>>): List<CardWithState<S>> = cards.forwardWithState()
}

class CardTypeReverseOnlyMutation : CardTypeFilterMutation() {
    override fun <S : State> apply(cards: List<CardWithState<S>>): List<CardWithState<S>> = cards.reverseWithState()
}