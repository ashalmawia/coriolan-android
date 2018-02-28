package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.model.Card
import org.joda.time.DateTime
import java.util.*

class StraightForwardAssignment(date: DateTime, cards: List<Card>) : Assignment(date) {
    private val queue: Queue<Card> = LinkedList(cards)

    override fun hasNext(): Boolean {
        return queue.size > 0
    }

    override fun getNext(): Card {
        return queue.poll() ?: throw IllegalStateException("queue is empty")
    }

    override fun reschedule(card: Card) {
        queue.offer(card)
    }

    override fun createPendingCounter(): PendingCounter {
        val counts = queue.groupBy { it.state.status }.mapValues { it.value.size }
        return PendingCounter.createFrom(counts)
    }

    override fun innerDelete(card: Card) {
        queue.remove(card)
    }

    override fun onCardUpdatedInner(old: Card, new: Card) {
        if (queue.contains(old)) {
            queue.remove(old)
            queue.offer(new)
        }
    }
}