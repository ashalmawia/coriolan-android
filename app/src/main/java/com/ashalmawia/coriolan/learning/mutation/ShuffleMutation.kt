package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.util.new
import com.ashalmawia.coriolan.util.review

class ShuffleMutation<S : State>(private val shuffle: Boolean) : Mutation<S> {

    override fun apply(cards: List<CardWithState<S>>): List<CardWithState<S>> {
        return if (shuffle) {
            shuffle(cards)
        } else {
            cards
        }
    }

    private fun shuffle(cards: List<CardWithState<S>>): List<CardWithState<S>> {
        val reviewOnlySize = cards.size / 3
        val newCardsAllowedSize = cards.size - reviewOnlySize

        val new = cards.new()
        val review = cards.review()

        return if (new.size >= newCardsAllowedSize) {
            new.shuffled().plus(review.shuffled())
        } else {
            // otherwise, keep the last X cards review only, to make sure all new cards are seen in advance
            val extraReviewsCount = newCardsAllowedSize - new.size

            val newCardsAllowed = new.plus(review.subList(0, extraReviewsCount))
            val reviewsOnly = review.subList(extraReviewsCount, review.size)

            newCardsAllowed.shuffled().plus(reviewsOnly.shuffled())
        }
    }
}