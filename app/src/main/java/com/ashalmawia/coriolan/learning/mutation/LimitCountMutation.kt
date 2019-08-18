package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.Status
import org.joda.time.DateTime

class LimitCountMutation<S : State>(preferences: Preferences, journal: Journal, date: DateTime) : Mutation<S> {

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