package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.CardTypePreference
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.learning.scheduler.CardWithState
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.learning.scheduler.Status
import org.joda.time.DateTime

sealed class Mutation {
    abstract fun <S : State>  apply(cards: List<CardWithState<S>>): List<CardWithState<S>>

    abstract class CardTypeFilter : Mutation() {
        companion object {
            fun from(preferences: Preferences): CardTypeFilter {
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

    class LimitCount(preferences: Preferences, journal: Journal, date: DateTime) : Mutation() {

        private val limitNew = preferences.getNewCardsDailyLimit()
        private val limitReview = preferences.getReviewCardsDailyLimit()

        private val counts = journal.cardsStudiedOnDate(date)

        override fun <S : State> apply(cards: List<CardWithState<S>>): List<CardWithState<S>> {
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

    class Shuffle(private val shuffle: Boolean) : Mutation() {

        override fun <S : State> apply(cards: List<CardWithState<S>>): List<CardWithState<S>> {
            if (shuffle) {
                return cards.shuffled()
            } else {
                return cards
            }
        }
    }
}