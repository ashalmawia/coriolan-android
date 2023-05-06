package com.ashalmawia.coriolan.learning

import org.joda.time.DateTime

const val INTERVAL_NEVER_SCHEDULED = -1
const val INTERVAL_FIRST_ASNWER_WRONG = -2
const val INTERVAL_LEARNT = 30 * 4               // 4 months

data class LearningProgress(
        val state: SchedulingState,
        val exerciseData: ExerciseData
) {
    val status = state.status

    companion object {

        fun empty() = LearningProgress(SchedulingState.new(), ExerciseData())
    }
}

data class SchedulingState(
        val due: DateTime,
        val interval: Int
) {
    val status: Status = statusFromInterval(interval)

    companion object {
        fun new() = SchedulingState(TodayManager.today(), INTERVAL_NEVER_SCHEDULED)

        fun statusFromInterval(interval: Int): Status {
            return when (interval) {
                INTERVAL_NEVER_SCHEDULED -> Status.NEW
                0, INTERVAL_FIRST_ASNWER_WRONG -> Status.RELEARN
                in 1 until INTERVAL_LEARNT -> Status.IN_PROGRESS
                else /* >= INTERVAL_LEARNT */ -> Status.LEARNT
            }
        }
    }
}