package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.learning.scheduler.Status
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.util.orZero

interface PendingCounter : Counts {

    fun onCardCorrect(card: Card)
    fun onCardWrong(card: Card)
    fun onCardDeleted(card: Card)

    companion object {
        fun createFrom(counts: Map<Status, Int>): PendingCounter {
            return PendingCounterImpl(counts)
        }
        fun createFrom(new: Int, review: Int, relearn: Int): PendingCounter {
            return PendingCounterImpl(new, review, relearn)
        }
    }
}

private class PendingCounterImpl(
        private var new: Int,
        private var review: Int,
        private var relearn: Int
) : PendingCounter {

    constructor(counts: Map<Status, Int>) : this(
            counts[Status.NEW].orZero(),
            counts[Status.IN_PROGRESS].orZero() + counts[Status.LEARNT].orZero(),
            counts[Status.RELEARN].orZero())

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