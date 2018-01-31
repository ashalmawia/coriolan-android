package com.ashalmawia.coriolan.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.*

fun ViewGroup.inflate(resource: Int, attachToRoot: Boolean) :View {
    return LayoutInflater.from(context).inflate(resource, this, attachToRoot)
}

fun Date.addDays(days: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.DAY_OF_MONTH, days)
    return calendar.time
}