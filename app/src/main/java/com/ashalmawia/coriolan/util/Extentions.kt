package com.ashalmawia.coriolan.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.joda.time.DateTime

fun ViewGroup.inflate(resource: Int, attachToRoot: Boolean): View {
    return LayoutInflater.from(context).inflate(resource, this, attachToRoot)
}

val DateTime.timespamp
    get() = toDate().time