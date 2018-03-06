package com.ashalmawia.coriolan.data

interface Counts {

    fun countNew(): Int
    fun countReview(): Int
    fun countRelearn(): Int

    fun isAnythingPending(): Boolean {
        return countNew() > 0 || countReview() > 0 || countRelearn() > 0
    }
}

data class SimpleCounts(private val new: Int, private val review: Int, private val relearn: Int) : Counts {
    override fun countNew(): Int = new

    override fun countReview(): Int = review

    override fun countRelearn(): Int = relearn
}

fun emptyCounts(): Counts = SimpleCounts(0, 0, 0)