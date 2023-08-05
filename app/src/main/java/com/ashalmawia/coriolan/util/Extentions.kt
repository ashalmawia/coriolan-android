package com.ashalmawia.coriolan.util

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.TextViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.CardId
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.DeckId
import com.ashalmawia.coriolan.model.DomainId
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
fun Int?.orMax(): Int {
    return this ?: Int.MAX_VALUE
}

fun List<Card>.forward() = filter { it.type == CardType.FORWARD }
fun List<Card>.reverse() = filter { it.type == CardType.REVERSE }

fun Long.asDomainId() = DomainId(this)
fun Long.asDeckId() = DeckId(this)
fun Long.asCardId() = CardId(this)