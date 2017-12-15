package com.ashalmawia.coriolan.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun ViewGroup.inflate(resource: Int, attachToRoot: Boolean) :View {
    return LayoutInflater.from(context).inflate(resource, this, attachToRoot)
}