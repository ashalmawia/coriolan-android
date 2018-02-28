package com.ashalmawia.coriolan.util

import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.widget.TextViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.app_toolbar.*
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

fun AppCompatActivity.setUpToolbar(title: String) {
    setSupportActionBar(toolbar)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    supportActionBar!!.title = title
}

fun AppCompatActivity.setUpToolbar(@StringRes titleRes: Int) {
    val title = getString(titleRes)
    setUpToolbar(title)
}