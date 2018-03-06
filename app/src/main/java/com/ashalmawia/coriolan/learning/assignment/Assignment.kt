package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.model.Card
import org.joda.time.DateTime
import java.util.*

open class Assignment(val date: DateTime, cards: List<Card>) {
    private val queue = LinkedList(cards)

    var current: Card? = null
        protected set

    val pendingCounter = lazy(LazyThreadSafetyMode.NONE, { createPendingCounter() })

    fun hasNext(): Boolean {
        return queue.size > 0
    }

    fun reschedule(card: Card) {
        // todo: reschedule not to the very end https://trello.com/c/02EhW776
        queue.offer(card)
    }

    fun delete(card: Card) {
        queue.remove(card)
        pendingCounter.value.onCardDeleted(card)
    }

    private fun createPendingCounter(): PendingCounter {
        val counts = cards().groupBy { it.state.status }.mapValues { it.value.size }
        return PendingCounter.createFrom(counts)
    }

    fun next(): Card {
        val next = getNext()
        current = next
        return next
    }

    private fun getNext(): Card {
        return queue.poll() ?: throw IllegalStateException("queue is empty")
    }

    fun onCardUpdated(old: Card, new: Card) {
        if (current == old) {
            current = new
        } else {
            if (queue.contains(old)) {
                queue.remove(old)
                queue.offer(new)
            }
        }
    }

    private fun cards(): List<Card> {
        val cur = current
        return if (cur != null) {
            queue.plus(cur)
        } else {
            queue.toList()
        }
    }
}