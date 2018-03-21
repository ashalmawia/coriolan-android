package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.learning.scheduler.CardWithState
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.learning.scheduler.Status
import com.ashalmawia.coriolan.model.Card
import org.joda.time.DateTime

class MockAssignment(cards: List<CardWithState<MockState>>) : Assignment<MockState>(DateTime.now(), cards) {

    fun mockCurrent(card: Card) {
        current = CardWithState(card, MockState())
    }
}

class MockState() : State {
    override val status: Status
        get() = Status.NEW
}