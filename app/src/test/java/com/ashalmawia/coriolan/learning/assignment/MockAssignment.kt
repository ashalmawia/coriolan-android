package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.model.Card
import org.joda.time.DateTime

class MockAssignment(cards: List<Card>) : Assignment(DateTime.now(), cards) {

    fun mockCurrent(card: Card) {
        current = card
    }
}