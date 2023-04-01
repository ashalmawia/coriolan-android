package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.exercise.CardAction
import com.ashalmawia.coriolan.util.orZero
import org.joda.time.DateTime

class LimitCountMutation(preferences: Preferences, logbook: Logbook, date: DateTime) : Mutation {

    private val limitNew = preferences.getNewCardsDailyLimit(date)
    private val limitReview = preferences.getReviewCardsDailyLimit(date)

    private val counts = logbook.cardsStudiedOnDate(date)

    override fun apply(tasks: List<Task>): List<Task> {
        if (limitNew == null && limitReview == null) {
            return tasks
        }

        return transformed(tasks, limitNew(), limitReview())
    }

    private fun limitNew() = limitNew?.minus(counts[CardAction.NEW_CARD_FIRST_SEEN].orZero()) ?: Int.MAX_VALUE
    private fun limitReview() = limitReview?.minus(counts[CardAction.CARD_REVIEWED].orZero()) ?: Int.MAX_VALUE

    private fun  transformed(cards: List<Task>, limitNew: Int, limitReview: Int): List<Task> {
        var countNew = 0
        var countReview = 0
        return cards.filter {
            when (it.learningProgress.spacedRepetition.status) {
                Status.NEW -> countNew++ < limitNew
                Status.IN_PROGRESS, Status.LEARNT -> countReview++ < limitReview
                Status.RELEARN -> true
            }
        }
    }
}