package com.ashalmawia.coriolan.data.journal

import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.data.SimpleCounts
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
        return SimpleCounts(new[date].orZero(), review[date].orZero(), relearn[date].orZero())
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
}