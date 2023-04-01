package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.Task

object SortReviewsByPeriodMutation : Mutation {

    override fun apply(tasks: List<Task>): List<Task> {
        return tasks.sortedBy { it.learningProgress.spacedRepetition.period }
    }
}