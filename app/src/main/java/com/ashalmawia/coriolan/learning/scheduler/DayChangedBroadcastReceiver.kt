package com.ashalmawia.coriolan.learning.scheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DayChangedBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        TodayManager.dayChanged()
    }
}