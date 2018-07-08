package com.ashalmawia.coriolan.ui.dialog

import android.app.AlertDialog
import android.support.annotation.StringRes

fun AlertDialog.setNegativeButton(title: String, listener: () -> Unit = {}) {
    setButton(AlertDialog.BUTTON_NEGATIVE, title, { _, _ -> listener() })
}

fun AlertDialog.setNegativeButton(@StringRes titleRes: Int, listener: () -> Unit = {}) {
    setNegativeButton(context.getString(titleRes), listener)
}

fun AlertDialog.setPositiveButton(title: String, listener: () -> Unit = {}) {
    setButton(AlertDialog.BUTTON_POSITIVE, title, { _, _ -> listener() })
}

fun AlertDialog.setPositiveButton(@StringRes titleRes: Int, listener: () -> Unit = {}) {
    setPositiveButton(context.getString(titleRes), listener)
}