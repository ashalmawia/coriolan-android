package com.ashalmawia.coriolan.util

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.TextViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.CardType
import org.joda.time.DateTime

fun ViewGroup.inflate(resource: Int, attachToRoot: Boolean): View {
    return LayoutInflater.from(context).inflate(resource, this, attachToRoot)
}

val DateTime.timespamp
    get() = toDate().time

fun TextView.setStartDrawableTint(@ColorRes colorRes: Int) {
    val wrap = DrawableCompat.wrap(compoundDrawablesRelative[0])
    DrawableCompat.setTint(wrap, ContextCompat.getColor(context, colorRes))
    TextViewCompat.setCompoundDrawablesRelative(this, wrap, null, null, null)
}

fun Int?.orZero(): Int {
    return this ?: 0
}

fun List<Card>.forward() = filter { it.type == CardType.FORWARD }
fun List<Card>.reverse() = filter { it.type == CardType.REVERSE }
fun  List<Task>.forwardAndReverseWithState() = partition { it.card.type == CardType.FORWARD }

fun  List<Task>.new() = this.filter { it.state.spacedRepetition.status == Status.NEW }
fun  List<Task>.review() = this.filter { it.state.spacedRepetition.status != Status.NEW }