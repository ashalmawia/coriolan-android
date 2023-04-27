package com.ashalmawia.coriolan.model

import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.util.orZero

data class Counts(val new: Int, val review: Int, val relearn: Int) {

    fun isAnythingPending(): Boolean {
        return new > 0 || review > 0 || relearn > 0
    }

    operator fun plus(other: Counts): Counts {
        return Counts(
                new = new + other.new,
                review = review + other.review,
                relearn = relearn + other.relearn
        )
    }

    companion object {
        fun createFrom(counts: Map<Status, Int>): Counts {
            return Counts(
                    counts[Status.NEW].orZero(),
                    counts[Status.IN_PROGRESS].orZero() + counts[Status.LEARNT].orZero(),
                    counts[Status.RELEARN].orZero()
            )
        }

        fun empty() = Counts(0, 0, 0)
    }
}