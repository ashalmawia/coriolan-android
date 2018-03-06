package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.model.Card

class ShuffleMutation(private val shuffle: Boolean) : Mutation {

    override fun apply(cards: List<Card>): List<Card> {
        if (shuffle) {
            return cards.shuffled()
        } else {
            return cards
        }
    }
}