package com.ashalmawia.coriolan.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Accepts one or multiple anchors and catches all
 * touch events addressed to them, applies to itself
 * and then passes to the respective anchor.
 *
 * This way we can e.g. create an overflow ripple to fill
 * the whole screen with a color when user taps a button.
 */
class TouchInterceptorView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val anchors = mutableListOf<View>()

    init {
        isClickable = true
    }

    /**
     * Anchor views must not overlap each other.
     */
    fun addAnchor(vararg anchor: View) {
        anchors.addAll(anchor)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        for (anchor in anchors) {
            if (isInsideAnchorView(event, anchor)) {
                super.onTouchEvent(event)
                return anchor.onTouchEvent(adaptToAnchorView(event, anchor))
            }
        }

        return false
    }

    private fun isInsideAnchorView(event: MotionEvent, anchor: View): Boolean {
        val array = IntArray(2)
        anchor.getLocationOnScreen(array)

        val rect = Rect(array[0], array[1], array[0] + anchor.measuredWidth, array[1] + anchor.measuredHeight)

        return rect.contains(event.rawX.toInt(), event.rawY.toInt())
    }

    private fun adaptToAnchorView(event: MotionEvent, anchor: View): MotionEvent {
        val array = IntArray(2)
        anchor.getLocationOnScreen(array)

        val x = event.rawX - array[0]
        val y = event.rawY - array[1]

        return MotionEvent.obtain(event.downTime, event.eventTime, event.action, x, y, event.metaState)
    }
}