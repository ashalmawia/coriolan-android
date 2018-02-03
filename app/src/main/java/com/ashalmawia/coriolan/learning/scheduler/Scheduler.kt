package com.ashalmawia.coriolan.learning.scheduler

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime

interface Scheduler {

    companion object {
        fun default(): Scheduler {
            return SpacedRepetitionScheduler()
        }
    }

    fun wrong(state: State): State

    fun correct(state: State): State
}

/**
 * Returns the beginning of the Coriolan day in the past which is the closest to the current moment.
 *
 * E.g. if Coriolan day starts at 4am, then
 *      - called on Fri at 6am, will return Fri 4am ('cause it's already Fri for Coriolan)
 *      - called on Fri at 3am, will return Thu 4am ('cause it's still Thu for Coriolan)
 */
fun today(): DateTime {
    val today = LocalDate.now().toDateTime(LocalTime(4, 0))
    if (today.isAfterNow) {
        return today.minusDays(1)
    } else {
        return today
    }
}