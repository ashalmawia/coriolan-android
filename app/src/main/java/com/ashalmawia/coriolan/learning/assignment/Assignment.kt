package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.model.Card
import org.joda.time.DateTime

abstract class Assignment(
        val date: DateTime
) {
    var current: Card? = null
        protected set

    val pendingCounter = lazy(LazyThreadSafetyMode.NONE, { createPendingCounter() })

    protected abstract fun getNext(): Card

    abstract fun hasNext(): Boolean
    abstract fun reschedule(card: Card)

    fun delete(card: Card) {
        innerDelete(card)
        pendingCounter.value.onCardDeleted(card)
    }

    private fun createPendingCounter(): PendingCounter {
        val counts = cards().groupBy { it.state.status }.mapValues { it.value.size }
        return PendingCounter.createFrom(counts)
    }

    protected abstract fun innerDelete(card: Card)

    fun next(): Card {
        val next = getNext()
        current = next
        onCurrent(next)
        return next
    }

    fun onCardUpdated(old: Card, new: Card) {
        if (current == old) {
            current = new
        } else {
            onCardUpdatedInner(old, new)
        }
    }

    protected open fun onCurrent(card: Card) {
        // for overriding
    }

    protected abstract fun cards(): List<Card>
    protected abstract fun onCardUpdatedInner(old: Card, new: Card)
}