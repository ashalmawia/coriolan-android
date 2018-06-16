package com.ashalmawia.coriolan.data

import com.ashalmawia.coriolan.learning.scheduler.Status
import com.ashalmawia.coriolan.util.orZero

data class Counts(val new: Int, val review: Int, val relearn: Int, val total: Int) {

    fun isAnythingPending(): Boolean {
        return new > 0 || review > 0 || relearn > 0
    }

    companion object {
        fun createFrom(counts: Map<Status, Int>, total: Int): Counts {
            return Counts(
                    counts[Status.NEW].orZero(),
                    counts[Status.IN_PROGRESS].orZero() + counts[Status.LEARNT].orZero(),
                    counts[Status.RELEARN].orZero(),
                    total
            )
        }

        fun empty() = Counts(0, 0, 0, 0)
    }
}

data class CountsSummary(val forward: Counts, val reverse: Counts) {

    val total = Counts(
            forward.new + reverse.new,
            forward.review + reverse.review,
            forward.relearn + reverse.relearn,
            forward.total + reverse.total
    )
}