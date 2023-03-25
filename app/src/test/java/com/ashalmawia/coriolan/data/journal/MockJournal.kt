package com.ashalmawia.coriolan.data.journal

import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.util.orZero
import org.joda.time.DateTime

class MockJournal : Journal {

    private val data = mutableMapOf<Status, Int>()

    fun setTodayLearned(new: Int, review: Int) {
        data[Status.NEW] = new
        data[Status.IN_PROGRESS] = review
    }

    override fun cardsStudiedOnDate(date: DateTime): Counts {
        return Counts(data[Status.NEW].orZero(), data[Status.IN_PROGRESS].orZero(), data[Status.RELEARN].orZero(), -1)
    }

    override fun cardsStudiedOnDate(date: DateTime, exercise: ExerciseId): Counts {
        return Counts(data[Status.NEW].orZero(), data[Status.IN_PROGRESS].orZero(), data[Status.RELEARN].orZero(), -1)
    }

    override fun incrementCardStudied(date: DateTime, targetStatus: Status, exercise: ExerciseId) {
        data[targetStatus] = data[targetStatus].orZero() + 1
    }

    override fun decrementCardStudied(date: DateTime, targetStatus: Status, exercise: ExerciseId) {
        data[targetStatus] = data[targetStatus].orZero() - 1
    }
}