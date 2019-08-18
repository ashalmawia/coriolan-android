package com.ashalmawia.coriolan.util

import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.widget.TextViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.State
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

fun <S : State> List<CardWithState<S>>.forwardWithState() = filter { it.card.type == CardType.FORWARD }
fun <S : State> List<CardWithState<S>>.reverseWithState() = filter { it.card.type == CardType.REVERSE }
fun <S : State> List<CardWithState<S>>.forwardAndReverseWithState() = partition { it.card.type == CardType.FORWARD }

fun <S : State> List<CardWithState<S>>.new() = this.filter { it.state.status == Status.NEW }
fun <S : State> List<CardWithState<S>>.review() = this.filter { it.state.status != Status.NEW }