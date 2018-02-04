package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.model.Card
import org.joda.time.DateTime
import java.util.*

private const val MIN_TURNS_FOR_NEXT_REVIEW = 4

class RandomAssignment(date: DateTime, cards: List<Card>) : Assignment(date) {

    private val cards = cards.map { it -> CardEntry(it, -MIN_TURNS_FOR_NEXT_REVIEW) }.toMutableList()
    private val random = Random()

    private var turn = 0

    override fun hasNext(): Boolean {
        return !cards.isEmpty()
    }

    override fun getNext(): Card {
        if (cards.size == 0) {
            throw IllegalStateException("list is empty")
        }

        val index = nextIndex()
        return cards.removeAt(index).card
    }

    override fun onCurrent(card: Card) {
        turn++
    }

    /**
     * Returns the "common index" which is an index shared between the two collections:
     * of normal and of rescheduled cards.
     *
     * Normally, it will randomly pick one of the cards, taking into account that it
     * If we only
     */
    private fun nextIndex(): Int {
        val selectable = cards.filter { turn - it.turn >= MIN_TURNS_FOR_NEXT_REVIEW }
        if (selectable.isEmpty()) {
            // we only have a couple of rescheduled's left, no sense to use random
            // just give them out in the default order
            return 0
        } else {
            // we have something to randomize!
            // the cards collection is expected to be sorted in a way that rescheduled cards are always in the end,
            // and the later the card was rescheduled the closer it will be to the end
            // so we can easily presume that the {selectable} items are the first {selectable.size} items of {cards}
            return random.nextInt(selectable.size)
        }
    }

    override fun reschedule(card: Card) {
        cards.add(CardEntry(card, turn))
    }

    override fun createPendingCounter(): PendingCounter {
        val counts = cards.groupBy { it.card.state.status }.mapValues { it.value.size }
        return PendingCounter.createFrom(counts)
    }
}

private data class CardEntry(val card: Card, var turn: Int)