package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.model.Card
import org.joda.time.DateTime
import java.util.*

class MockAssignment(cards: List<Card>) : Assignment(DateTime.now()) {

    val cards: Queue<Card> = LinkedList(cards)

    fun mockCurrent(card: Card) {
        current = card
    }

    override fun hasNext(): Boolean {
        return !cards.isEmpty()
    }

    override fun getNext(): Card {
        return cards.poll()
    }

    override fun reschedule(card: Card) {
        cards.offer(card)
    }

    override fun createPendingCounter(): PendingCounter {
        return MockPendingCounter()
    }

    override fun innerDelete(card: Card) {
        cards.remove(card)
    }

    override fun onCardUpdatedInner(old: Card, new: Card) {
        if (cards.contains(old)) {
            cards.remove(old)
            cards.add(new)
        }
    }
}