package com.ashalmawia.coriolan.learning.scheduler

import com.ashalmawia.coriolan.data.Counts

class MockCounts(val new: Int, val review: Int, val relearn: Int) : Counts {

    override fun countNew(): Int {
        return new
    }

    override fun countReview(): Int {
        return review
    }

    override fun countRelearn(): Int {
        return relearn
    }
}