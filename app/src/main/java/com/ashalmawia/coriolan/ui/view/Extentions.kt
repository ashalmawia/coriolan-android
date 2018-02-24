package com.ashalmawia.coriolan.ui.view

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun EditText.showKeyboard() {
    val service = context.getSystemService(Context.INPUT_METHOD_SERVICE)
    if (service is InputMethodManager) {
        service.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}