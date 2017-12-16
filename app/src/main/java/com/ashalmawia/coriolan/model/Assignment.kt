package com.ashalmawia.coriolan.model

import java.util.*
import kotlin.collections.ArrayList

class Assignment(
        val date: Date,

        cards: List<Card>
) {
    protected val queue: Queue<Card> = LinkedList(cards)
    protected val done: MutableList<Card> = ArrayList()
    var current: Card? = null

    fun hasNext(): Boolean {
        return queue.size > 0
    }

    fun next(): Card {
        val card = queue.poll() ?: throw IllegalStateException("queue is empty")
        current = card
        return card
    }

    fun reschedule(card: Card) {
        queue.add(card)
    }

    fun done(card: Card) {
        done.add(card)
    }
}