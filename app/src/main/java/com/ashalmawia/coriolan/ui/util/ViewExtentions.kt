package com.ashalmawia.coriolan.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.EditText
import android.widget.Spinner

fun EditText.showKeyboard() {
    val service = context.getSystemService(Context.INPUT_METHOD_SERVICE)
    if (service is InputMethodManager) {
        service.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

var View.visible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

val View.layoutInflator: LayoutInflater
    get() = LayoutInflater.from(context)

fun Spinner.setOnItemSelectedListener(listener: (Int) -> Unit) {
    onItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            listener(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }
}