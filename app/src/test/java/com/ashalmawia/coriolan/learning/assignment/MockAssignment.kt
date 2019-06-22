package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.model.Card
import org.joda.time.DateTime

class MockAssignment(cards: List<CardWithState<MockState>>)
    : Assignment<MockState>(DateTime.now(), MockHistoryFactory.create<MockState>(), cards) {

    fun mockCurrent(card: Card) {
        current = CardWithState(card, MockState())
    }
}

class MockState() : State {
    override val status: Status
        get() = Status.NEW
}