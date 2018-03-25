package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.learning.scheduler.CardWithState
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.util.OpenForTesting
import org.joda.time.DateTime
import java.util.*

@OpenForTesting
class Assignment<T : State>(val date: DateTime, cards: List<CardWithState<T>>) {
    private val queue = LinkedList(cards)

    var current: CardWithState<T>? = null
        protected set

    fun counts(): Counts {
        val counts = cards().groupBy { it.state.status }.mapValues { it.value.size }
        return Counts.createFrom(counts)
    }
    fun hasNext(): Boolean {
        return queue.size > 0
    }

    fun reschedule(card: CardWithState<T>) {
        // todo: reschedule not to the very end https://trello.com/c/02EhW776
        queue.offer(card)
    }

    fun delete(card: Card) {
        queue.removeAll { it.card == card }
    }

    fun next(): CardWithState<T> {
        val next = getNext()
        current = next
        return next
    }

    private fun getNext(): CardWithState<T> {
        return queue.poll() ?: throw IllegalStateException("queue is empty")
    }

    fun replace(old: Card, new: CardWithState<T>) {
        if (current?.card == old) {
            current = new
        } else {
            val found = queue.find { it.card == old }
            if (found != null) {
                queue.remove(found)
                queue.offer(new)
            }
        }
    }

    private fun cards(): List<CardWithState<T>> {
        val cur = current
        return if (cur != null) {
            queue.plus(cur)
        } else {
            queue.toList()
        }
    }
}