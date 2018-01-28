package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.model.Card
import java.util.*
import kotlin.collections.ArrayList

class StraightForwardAssignment(date: Date, cards: List<Card>) : Assignment(date, cards) {
    private val queue: Queue<Card> = LinkedList(cards)

    override fun hasNext(): Boolean {
        return queue.size > 0
    }

    override fun getNext(): Card {
        return queue.poll() ?: throw IllegalStateException("queue is empty")
    }

    override fun reschedule(card: Card) {
        queue.add(card)
    }
}