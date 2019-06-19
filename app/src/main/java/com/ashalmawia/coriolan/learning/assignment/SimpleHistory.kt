package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.State
import java.util.*

class SimpleHistory<T : State> : History<T> {

    private val stack = Stack<CardWithState<T>>()

    override fun canGoBack(): Boolean = stack.isNotEmpty()

    override fun record(card: CardWithState<T>) {
        stack.add(card)
    }

    override fun goBack(): CardWithState<T> {
        return stack.pop()
    }
}