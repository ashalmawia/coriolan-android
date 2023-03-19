package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.model.CardType

class CardTypeMutation(private val cardType: CardType) : Mutation {

    override fun apply(cards: List<CardWithState>): List<CardWithState> {
        return cards.filter { it.card.type == cardType }
    }
}