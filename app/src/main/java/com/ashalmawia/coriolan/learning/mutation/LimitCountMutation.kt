package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.Status
import org.joda.time.DateTime

class LimitCountMutation(preferences: Preferences, journal: Journal, date: DateTime) : Mutation {

    private val limitNew = preferences.getNewCardsDailyLimit(date)
    private val limitReview = preferences.getReviewCardsDailyLimit(date)

    private val counts = journal.cardsStudiedOnDate(date)

    override fun apply(tasks: List<Task>): List<Task> {
        if (limitNew == null && limitReview == null) {
            return tasks
        }

        return transformed(tasks, limitNew(), limitReview())
    }

    private fun limitNew() = limitNew?.minus(counts.new) ?: Int.MAX_VALUE
    private fun limitReview() = limitReview?.minus(counts.review) ?: Int.MAX_VALUE

    private fun  transformed(cards: List<Task>, limitNew: Int, limitReview: Int): List<Task> {
        var countNew = 0
        var countReview = 0
        return cards.filter {
            when (it.state.spacedRepetition.status) {
                Status.NEW -> countNew++ < limitNew
                Status.IN_PROGRESS, Status.LEARNT -> countReview++ < limitReview
                Status.RELEARN -> true
            }
        }
    }
}