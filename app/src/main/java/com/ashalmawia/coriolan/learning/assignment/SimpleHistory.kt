package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.model.Card
import java.util.*

class SimpleHistory : History {

    private val stack = Stack<CardWithState>()

    override fun canGoBack(): Boolean = stack.isNotEmpty()

    override fun record(card: CardWithState) {
        stack.add(card)
    }

    override fun forget(card: Card) {
        stack.removeAll { it.card == card }
    }

    override fun goBack(): CardWithState {
        return stack.pop()
    }
}