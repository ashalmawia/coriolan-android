package com.ashalmawia.coriolan.learning

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.ashalmawia.coriolan.debug.DEBUG_OVERRIDE_TODAY
import com.ashalmawia.coriolan.util.timespamp
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import java.util.concurrent.TimeUnit

private const val REQUEST_CODE = 777

typealias LearningDay = DateTime

object TodayManager {

    private val listeners = mutableListOf<TodayChangeListener>()

    // for debug & testing
    private var overridenToday: LearningDay? = null

    /**
     * Returns the beginning of the Coriolan day in the past which is the closest to the current moment.
     *
     * E.g. if Coriolan day starts at 4am, then
     *      - called on Fri at 6am, will return Fri 4am ('cause it's already Fri for Coriolan)
     *      - called on Fri at 3am, will return Thu 4am ('cause it's still Thu for Coriolan)
     */
    fun today(): LearningDay {
        return if (DEBUG_OVERRIDE_TODAY) todayWithOverride() else realToday()
    }

    private fun realToday(): LearningDay {
        val today = LocalDate.now().toDateTime(LocalTime(4, 0))
        return if (today.isAfterNow) {
            today.minusDays(1)
        } else {
            today
        }
    }

    private fun todayWithOverride(): LearningDay {
        return overridenToday ?: realToday()
    }

    fun overrideToday(date: LearningDay) {
        overridenToday = date
        dayChanged()
    }

    fun register(listener: TodayChangeListener) {
        listeners.add(listener)
    }

    fun unregister(listener: TodayChangeListener) {
        listeners.remove(listener)
    }

    fun dayChanged() {
        for (listener in listeners) {
            listener.onDayChanged()
        }
    }

    fun initialize(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val tomorrow = today().plusDays(1)
        alarmManager.setRepeating(AlarmManager.RTC, tomorrow.timespamp, TimeUnit.DAYS.toMillis(1), createIntent(context))
    }

    private fun createIntent(context: Context): PendingIntent {
        val intent = Intent(context, DayChangedBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_MUTABLE)
    }
}

interface TodayChangeListener {

    fun onDayChanged()
}