package com.ashalmawia.coriolan.learning.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.ashalmawia.coriolan.util.timespamp
import java.util.concurrent.TimeUnit

private const val REQUEST_CODE = 777

object TodayManager {

    private val listeners = mutableListOf<TodayChangeListener>()

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
        return PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0)
    }
}

interface TodayChangeListener {

    fun onDayChanged()
}