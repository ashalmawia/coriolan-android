package com.ashalmawia.coriolan.learning

import org.joda.time.DateTime

typealias LearningDay = DateTime

interface TodayProvider {

    /**
     * Returns the beginning of the Coriolan day in the past which is the closest to the current moment.
     *
     * E.g. if Coriolan day starts at 4am, then
     *      - called on Fri at 6am, will return Fri 4am ('cause it's already Fri for Coriolan)
     *      - called on Fri at 3am, will return Thu 4am ('cause it's still Thu for Coriolan)
     */
    fun today(): LearningDay

    fun dayChanged()

    fun register(listener: TodayChangeListener)

    fun unregister(listener: TodayChangeListener)
}