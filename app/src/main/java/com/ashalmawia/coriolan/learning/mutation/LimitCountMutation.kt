package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.learning.scheduler.Status
import com.ashalmawia.coriolan.model.Card
import org.joda.time.DateTime

class LimitCountMutation(preferences: Preferences, journal: Journal, date: DateTime) : Mutation {

    private val limitNew = preferences.getNewCardsDailyLimit()
    private val limitReview = preferences.getReviewCardsDailyLimit()

    private val counts = journal.cardsStudiedOnDate(date)

    override fun apply(cards: List<Card>): List<Card> {
        if (limitNew == null && limitReview == null) {
            return cards
        }

        return transformed(cards, limitNew(), limitReview())
    }

    private fun limitNew() = limitNew?.minus(counts.countNew()) ?: Int.MAX_VALUE
    private fun limitReview() = limitReview?.minus(counts.countReview()) ?: Int.MAX_VALUE

    private fun transformed(cards: List<Card>, limitNew: Int, limitReview: Int): List<Card> {
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