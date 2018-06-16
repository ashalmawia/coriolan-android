package com.ashalmawia.coriolan.data.journal

import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.util.orZero
import org.joda.time.DateTime

class MockJournal : Journal {

    private val new = mutableMapOf<DateTime, Int>()
    private val review = mutableMapOf<DateTime, Int>()
    private val relearn = mutableMapOf<DateTime, Int>()

    fun setTodayLearned(new: Int, review: Int, date: DateTime) {
        this.new[date] = new
        this.review[date] = review
    }

    override fun cardsStudiedOnDate(date: DateTime): Counts {
        return Counts(new[date].orZero(), review[date].orZero(), relearn[date].orZero(), -1)
    }

    override fun recordNewCardStudied(date: DateTime) {
        new[date] = new[date].orZero() + 1
    }

    override fun recordReviewStudied(date: DateTime) {
        review[date] = review[date].orZero() + 1
    }

    override fun recordCardRelearned(date: DateTime) {
        relearn[date] = relearn[date].orZero() + 1
    }

    override fun undoNewCardStudied(date: DateTime) {
        new[date] = new[date]!!.dec()
    }

    override fun undoReviewStudied(date: DateTime) {
        review[date] = review[date]!!.dec()
    }

    override fun undoCardRelearned(date: DateTime) {
        relearn[date] = relearn[date]!!.dec()
    }
}