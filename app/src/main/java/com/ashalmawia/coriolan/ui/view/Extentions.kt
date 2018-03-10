package com.ashalmawia.coriolan.ui.view

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

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