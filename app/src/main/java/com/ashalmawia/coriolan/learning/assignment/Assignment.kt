package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.model.Card
import org.joda.time.DateTime

abstract class Assignment(
        val date: DateTime
) {
    var current: Card? = null
        protected set

    val pendingCounter = lazy(LazyThreadSafetyMode.NONE, { createPendingCounter() })

    abstract fun hasNext(): Boolean
    protected abstract fun getNext(): Card
    abstract fun reschedule(card: Card)
    protected abstract fun createPendingCounter(): PendingCounter

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