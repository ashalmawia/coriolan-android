package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.exercise.sr.SRState
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.util.new
import com.ashalmawia.coriolan.util.review
import org.joda.time.DateTime

sealed class Mutation<S : State> {
    abstract fun apply(cards: List<CardWithState<S>>): List<CardWithState<S>>

    class LimitCount<S : State>(preferences: Preferences, journal: Journal, date: DateTime) : Mutation<S>() {

        private val limitNew = preferences.getNewCardsDailyLimit(date)
        private val limitReview = preferences.getReviewCardsDailyLimit(date)

        private val counts = journal.cardsStudiedOnDate(date)

        override fun apply(cards: List<CardWithState<S>>): List<CardWithState<S>> {
            if (limitNew == null && limitReview == null) {
                return cards
            }

            return transformed(cards, limitNew(), limitReview())
        }

        private fun limitNew() = limitNew?.minus(counts.new) ?: Int.MAX_VALUE
        private fun limitReview() = limitReview?.minus(counts.review) ?: Int.MAX_VALUE

        private fun <S : State> transformed(cards: List<CardWithState<S>>, limitNew: Int, limitReview: Int): List<CardWithState<S>> {
            var countNew = 0
            var countReview = 0
            return cards.filter {
                when (it.state.status) {
                    Status.NEW -> countNew++ < limitNew
                    Status.IN_PROGRESS, Status.LEARNT -> countReview++ < limitReview
                    Status.RELEARN -> true
                }
            }
        }
    }

    class Shuffle<S : State>(private val shuffle: Boolean) : Mutation<S>() {

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

    abstract class NewCardsOrder<S : State> : Mutation<S>() {
        companion object {
            fun <S : State> from(order: StudyOrder) : NewCardsOrder<S> {
                return when (order) {
                    StudyOrder.ORDER_ADDED -> OrderAdded()
                    StudyOrder.RANDOM -> Random()
                    StudyOrder.NEWEST_FIRST -> NewestFirst()
                }
            }
        }
    }

    object SortReviewsByPeriod : Mutation<SRState>() {

        override fun apply(cards: List<CardWithState<SRState>>): List<CardWithState<SRState>> {
            return cards.sortedBy { it.state.period }
        }
    }

    class SplitDeck<S : State>(private val deck: Deck) : Mutation<S>() {

        override fun apply(cards: List<CardWithState<S>>): List<CardWithState<S>> {
            return cards.filter { it.card.type == deck.type }
        }
    }
}