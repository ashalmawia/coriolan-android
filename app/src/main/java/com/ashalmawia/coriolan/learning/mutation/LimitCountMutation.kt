package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithProgress
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.StudyTargets

class LimitCountMutation(private val targets: StudyTargets) : Mutation {

    override fun apply(cards: List<CardWithProgress>): List<CardWithProgress> {
        if (targets.unlimited()) {
            return cards
        }

        return transformed(cards, limitNew(), limitReview())
    }

    private fun limitNew() = targets.new ?: Int.MAX_VALUE
    private fun limitReview() = targets.review ?: Int.MAX_VALUE

    private fun transformed(cards: List<CardWithProgress>, limitNew: Int, limitReview: Int): List<CardWithProgress> {
        var countNew = 0
        var countReview = 0
        return cards.filter {
            when (it.status) {
                Status.NEW -> countNew++ < limitNew
                Status.IN_PROGRESS, Status.LEARNT -> countReview++ < limitReview
                Status.RELEARN -> true
            }
        }
    }
}