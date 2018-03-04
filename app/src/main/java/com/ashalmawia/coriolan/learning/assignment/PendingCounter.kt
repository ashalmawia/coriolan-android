package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.learning.scheduler.Status
import com.ashalmawia.coriolan.model.Card

interface Counts {

    fun countNew(): Int
    fun countReview(): Int
    fun countRelearn(): Int

    fun isAnythingPending(): Boolean {
        return countNew() > 0 || countReview() > 0 || countRelearn() > 0
    }
}

interface PendingCounter : Counts {

    fun onCardCorrect(card: Card)
    fun onCardWrong(card: Card)
    fun onCardDeleted(card: Card)

    companion object {
        fun createFrom(counts: Map<Status, Int>): PendingCounter {
            return PendingCounterImpl(counts)
        }
    }
}

private class PendingCounterImpl(
        counts: Map<Status, Int>
) : PendingCounter {

    private var new = or0(counts[Status.NEW])
    private var review = or0(counts[Status.IN_PROGRESS]) + or0(counts[Status.LEARNT])
    private var relearn = or0(counts[Status.RELEARN])

    override fun countNew(): Int {
        return new
    }

    override fun countReview(): Int {
        return review
    }

    override fun countRelearn(): Int {
        return relearn
    }

    override fun onCardCorrect(card: Card) {
        when (card.state.status) {
            Status.NEW -> {
                new--
                relearn++
            }
            Status.IN_PROGRESS, Status.LEARNT -> {
                review--
            }
            Status.RELEARN -> {
                relearn--
            }
        }
    }

    override fun onCardWrong(card: Card) {
        when (card.state.status) {
            Status.NEW -> {
                new--
                relearn++
            }
            Status.IN_PROGRESS, Status.LEARNT -> {
                review--
                relearn++
            }
            Status.RELEARN -> {
                // do nothing, as relearn stayed relearn
            }
        }
    }

    override fun onCardDeleted(card: Card) {
        when (card.state.status) {
            Status.NEW -> {
                new--
            }
            Status.IN_PROGRESS, Status.LEARNT -> {
                review--
            }
            Status.RELEARN -> {
                relearn--
            }
        }
    }
}

private fun or0(value: Int?): Int {
    return value ?: 0
}