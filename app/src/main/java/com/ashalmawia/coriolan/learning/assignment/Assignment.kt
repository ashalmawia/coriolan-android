package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.util.OpenForTesting
import org.joda.time.DateTime
import java.util.*
import kotlin.math.min

private const val RESCHEDULING_STEP = 20

@OpenForTesting
class Assignment(
        val date: DateTime,
        private val history: History,
        cards: List<CardWithState>
) {
    private val queue = LinkedList(cards)

    var current: CardWithState? = null
        protected set

    fun counts(): Counts {
        val cards = cards()
        // todo: decouple
        val counts = cards.groupBy { it.state.spacedRepetition.status }.mapValues { it.value.size }
        return Counts.createFrom(counts, cards.size)
    }
    fun hasNext(): Boolean {
        return queue.size > 0
    }

    fun reschedule(card: CardWithState) {
        val index = min(RESCHEDULING_STEP, queue.size)
        queue.add(index, card)
    }

    fun delete(card: Card) {
        queue.removeAll { it.card == card }
        if (current?.card == card) {
            current = null
        }
        history.forget(card)
    }

    fun next(): CardWithState {
        val current = this.current
        if (current != null) {
            history.record(current)
        }

        val next = getNext()
        this.current = next
        return next
    }

    private fun getNext(): CardWithState {
        return queue.poll() ?: throw IllegalStateException("queue is empty")
    }

    fun replace(old: Card, new: CardWithState) {
        if (current?.card?.id == old.id) {
            current = new
        } else {
            val found = queue.find { it.card == old }
            if (found != null) {
                queue.remove(found)
                queue.offer(new)
            }
        }
    }

    fun undo(): CardWithState {
        if (!canUndo()) {
            throw IllegalStateException("can not undo")
        }

        queue.add(0, current!!)
        val previous = history.goBack()
        queue.removeAll { it.card == previous.card }        // remove reschedules
        current = previous
        return previous
    }

    fun canUndo() = history.canGoBack()

    private fun cards(): List<CardWithState> {
        val cur = current
        return if (cur != null) {
            queue.plus(cur)
        } else {
            queue.toList()
        }
    }
}