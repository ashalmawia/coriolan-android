package com.ashalmawia.coriolan.learning.exercise.flashcards

import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.TodayManager
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

const val INTERVAL_NEVER_SCHEDULED = -1
const val INTERVAL_FIRST_ASNWER_WRONG = -2
const val INTERVAL_LEARNT = 30 * 4               // 4 months

private val format = DateTimeFormat.forPattern("dd MMM hh:mm").withLocale(Locale.ENGLISH)

data class ExerciseState(
        val due: DateTime,
        val interval: Int
) {

    val status: Status
        get() = statusFromInterval(interval)

    override fun toString(): String {
        return "due: ${format.print(due)}, interval: $interval"
    }

    companion object {
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

fun emptyState(): ExerciseState = ExerciseState(TodayManager.today(), INTERVAL_NEVER_SCHEDULED)