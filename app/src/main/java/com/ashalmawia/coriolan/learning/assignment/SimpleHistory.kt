package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.model.Card
import java.util.*

class SimpleHistory : History {

    private val stack = Stack<Task>()

    override fun canGoBack(): Boolean = stack.isNotEmpty()

    override fun record(task: Task) {
        stack.add(task)
    }

    override fun forget(card: Card) {
        stack.removeAll { it.card == card }
    }

    override fun goBack(): Task {
        return stack.pop()
    }
}