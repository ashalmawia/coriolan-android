package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.model.Card
import java.util.*

abstract class Assignment(
        val date: Date,
        cards: List<Card>
) {
    private val done: MutableList<Card> = ArrayList()

    var current: Card? = null

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

    fun done(card: Card) {
        done.add(card)
    }
}