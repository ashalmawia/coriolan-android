package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.data.prefs.CardTypePreference
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.util.forward
import com.ashalmawia.coriolan.util.reverse

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
    override fun apply(cards: List<Card>): List<Card> = cards.forward().plus(cards.reverse())
}

class CardTypeReverseFirstMutation : CardTypeFilterMutation() {
    override fun apply(cards: List<Card>): List<Card> = cards.reverse().plus(cards.forward())
}

class CardTypeMixedMutation : CardTypeFilterMutation() {
    override fun apply(cards: List<Card>): List<Card> = cards
}

class CardTypeForwardOnlyMutation : CardTypeFilterMutation() {
    override fun apply(cards: List<Card>): List<Card> = cards.forward()
}

class CardTypeReverseOnlyMutation : CardTypeFilterMutation() {
    override fun apply(cards: List<Card>): List<Card> = cards.reverse()
}