package com.ashalmawia.coriolan.data

import com.ashalmawia.coriolan.learning.scheduler.Status
import com.ashalmawia.coriolan.util.orZero

interface Counts {

    fun countNew(): Int
    fun countReview(): Int
    fun countRelearn(): Int

    fun isAnythingPending(): Boolean {
        return countNew() > 0 || countReview() > 0 || countRelearn() > 0
    }

    companion object {
        fun createFrom(counts: Map<Status, Int>): Counts {
            return SimpleCounts(counts[Status.NEW].orZero(),
                    counts[Status.IN_PROGRESS].orZero() + counts[Status.LEARNT].orZero(),
                    counts[Status.RELEARN].orZero())
        }
        fun createFrom(new: Int, review: Int, relearn: Int): Counts {
            return SimpleCounts(new, review, relearn)
        }
    }
}

data class SimpleCounts(private val new: Int, private val review: Int, private val relearn: Int) : Counts {
    override fun countNew(): Int = new

    override fun countReview(): Int = review

    override fun countRelearn(): Int = relearn
}

fun emptyCounts(): Counts = SimpleCounts(0, 0, 0)