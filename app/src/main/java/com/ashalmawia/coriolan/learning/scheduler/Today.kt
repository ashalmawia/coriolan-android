package com.ashalmawia.coriolan.learning.scheduler

import com.ashalmawia.coriolan.debug.DEBUG_OVERRIDE_TODAY
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime

/**
 * Returns the beginning of the Coriolan day in the past which is the closest to the current moment.
 *
 * E.g. if Coriolan day starts at 4am, then
 *      - called on Fri at 6am, will return Fri 4am ('cause it's already Fri for Coriolan)
 *      - called on Fri at 3am, will return Thu 4am ('cause it's still Thu for Coriolan)
 */
fun today(): DateTime {
    return if (DEBUG_OVERRIDE_TODAY) todayWithOverride() else realToday()
}

private fun realToday(): DateTime {
    val today = LocalDate.now().toDateTime(LocalTime(4, 0))
    return if (today.isAfterNow) {
        today.minusDays(1)
    } else {
        today
    }
}

private var overridenToday: DateTime? = null
private fun todayWithOverride(): DateTime {
    return overridenToday ?: realToday()
}
fun overrideToday(date: DateTime) {
    overridenToday = date
    TodayManager.dayChanged()
}