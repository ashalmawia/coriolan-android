package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.model.Card
import java.util.*

class MockAssignment(cards: List<Card>) : Assignment(Date()) {

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
}