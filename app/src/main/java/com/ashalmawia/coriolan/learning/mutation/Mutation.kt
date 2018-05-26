package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.CardTypePreference
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.learning.scheduler.CardWithState
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.learning.scheduler.Status
import com.ashalmawia.coriolan.learning.scheduler.sr.SRState
import org.joda.time.DateTime

sealed class Mutation<S : State> {
    abstract fun apply(cards: List<CardWithState<S>>): List<CardWithState<S>>

    abstract class CardTypeFilter<S : State> : Mutation<S>() {
        companion object {
            fun <S : State> from(preferences: Preferences): CardTypeFilter<S> {
                val cardType = preferences.getCardTypePreference()
                        ?: CardTypePreference.MIXED  // has no effect

                return when (cardType) {
                    CardTypePreference.MIXED -> CardTypeMixedMutation()
                    CardTypePreference.FORWARD_ONLY -> CardTypeForwardOnlyMutation()
                    CardTypePreference.REVERSE_ONLY -> CardTypeReverseOnlyMutation()
                }
            }
        }
    }

    class LimitCount<S : State>(preferences: Preferences, journal: Journal, date: DateTime) : Mutation<S>() {

        private val limitNew = preferences.getNewCardsDailyLimit()
        private val limitReview = preferences.getReviewCardsDailyLimit()

        private val counts = journal.cardsStudiedOnDate(date)

        override fun apply(cards: List<CardWithState<S>>): List<CardWithState<S>> {
            if (limitNew == null && limitReview == null) {
                return cards
            }

            return transformed(cards, limitNew(), limitReview())
        }

        private fun limitNew() = limitNew?.minus(counts.countNew()) ?: Int.MAX_VALUE
        private fun limitReview() = limitReview?.minus(counts.countReview()) ?: Int.MAX_VALUE

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

            val new = cards.filter { it.state.status == Status.NEW }
            val review = cards.filter { it.state.status != Status.NEW }

            if (new.size >= newCardsAllowedSize) {
                return new.shuffled().plus(review.shuffled())
            } else {
                // otherwise, keep the last X cards review only, to make sure all new cards are seen in advance
                val extraReviewsCount = newCardsAllowedSize - new.size

                val newCardsAllowed = new.plus(review.subList(0, extraReviewsCount))
                val reviewsOnly = review.subList(extraReviewsCount, review.size)

                return newCardsAllowed.shuffled().plus(reviewsOnly.shuffled())
            }
        }
    }

    class SortByPeriod : Mutation<SRState>() {

        override fun apply(cards: List<CardWithState<SRState>>): List<CardWithState<SRState>> {
            return cards.sortedBy { it.state.period }
        }
    }
}