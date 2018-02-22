package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.model.Card

class MockPendingCounter : PendingCounter {
    override fun countNew(): Int {
        return 0
    }

    override fun countReview(): Int {
        return 0
    }

    override fun countRelearn(): Int {
        return 0
    }

    override fun onCardCorrect(card: Card) {
    }

    override fun onCardWrong(card: Card) {
    }

    override fun onCardDeleted(card: Card) {
    }

    override fun isAnythingPending(): Boolean {
        return false
    }
}