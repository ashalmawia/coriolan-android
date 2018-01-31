package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.model.Card
import java.util.*

abstract class Assignment(
        val date: Date
) {
    var current: Card? = null
        protected set

    abstract fun hasNext(): Boolean
    protected abstract fun getNext(): Card
    abstract fun reschedule(card: Card)

    fun next(): Card {
        val next = getNext()
        current = next
        onCurrent(next)
        return next
    }

    protected open fun onCurrent(card: Card) {
        // for overriding
    }
}