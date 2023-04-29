package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.util.new
import com.ashalmawia.coriolan.util.review

enum class StudyOrder {
    ORDER_ADDED,
    RANDOM,
    NEWEST_FIRST;

    companion object {
        // todo: move default study order to settings
        fun default() = RANDOM
    }
}

class OrderAdded: NewCardsOrderMutation() {
    override fun apply(tasks: List<Task>): List<Task> {
        return tasks
    }
}

class NewestFirst: NewCardsOrderMutation() {
    override fun apply(tasks: List<Task>): List<Task> {
        return tasks.new().reversed().plus(tasks.review())
    }
}

class Random : NewCardsOrderMutation() {
    override fun apply(tasks: List<Task>): List<Task> {
        return tasks.shuffled()
    }
}