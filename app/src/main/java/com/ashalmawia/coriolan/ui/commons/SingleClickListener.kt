package com.ashalmawia.coriolan.ui.commons

import android.view.View

/**
 * Minimal time interval to pass between to clicks.
 * All clicks in this interval except the first one will be ignored.
 */
private const val TIME_INTERVAL_MIN = 300

class SingleClickListener(private val listener: (View) -> Unit) : View.OnClickListener {

    private var lastClicked: Long = 0L

    override fun onClick(v: View) {
        val now = System.currentTimeMillis()
        if (now - lastClicked >= TIME_INTERVAL_MIN) {
            listener(v)
        }
        lastClicked = now
    }
}

fun View.setOnSingleClickListener(listener: (View) -> Unit) = setOnClickListener(SingleClickListener(listener))