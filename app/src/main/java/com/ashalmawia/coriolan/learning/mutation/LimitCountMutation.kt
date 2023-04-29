package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.StudyTargets
import com.ashalmawia.coriolan.learning.Task

class LimitCountMutation(private val targets: StudyTargets) : Mutation {

    override fun apply(tasks: List<Task>): List<Task> {
        if (targets.unlimited()) {
            return tasks
        }

        return transformed(tasks, limitNew(), limitReview())
    }

    private fun limitNew() = targets.new ?: Int.MAX_VALUE
    private fun limitReview() = targets.review ?: Int.MAX_VALUE

    private fun transformed(cards: List<Task>, limitNew: Int, limitReview: Int): List<Task> {
        var countNew = 0
        var countReview = 0
        return cards.filter {
            when (it.exerciseState.status) {
                Status.NEW -> countNew++ < limitNew
                Status.IN_PROGRESS, Status.LEARNT -> countReview++ < limitReview
                Status.RELEARN -> true
            }
        }
    }
}