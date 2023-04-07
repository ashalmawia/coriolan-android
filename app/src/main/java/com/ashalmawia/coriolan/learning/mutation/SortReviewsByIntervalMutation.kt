package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.Task

object SortReviewsByIntervalMutation : Mutation {

    override fun apply(tasks: List<Task>): List<Task> {
        return tasks.sortedBy { it.exerciseState.interval }
    }
}