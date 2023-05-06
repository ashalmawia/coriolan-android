package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithProgress
import com.ashalmawia.coriolan.model.CardType

class CardTypeMutation(private val cardType: CardType) : Mutation {

    override fun apply(cards: List<CardWithProgress>): List<CardWithProgress> {
        return cards.filter { it.card.type == cardType }
    }
}