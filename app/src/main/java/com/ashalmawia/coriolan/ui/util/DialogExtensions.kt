package com.ashalmawia.coriolan.ui.util

import android.app.Activity
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.ashalmawia.coriolan.R

fun AlertDialog.setNegativeButton(title: String, listener: () -> Unit = {}) {
    setButton(AlertDialog.BUTTON_NEGATIVE, title) { _, _ -> listener() }
}

fun AlertDialog.setNegativeButton(@StringRes titleRes: Int, listener: () -> Unit = {}) {
    setNegativeButton(context.getString(titleRes), listener)
}

fun AlertDialog.setPositiveButton(title: String, listener: () -> Unit = {}) {
    setButton(AlertDialog.BUTTON_POSITIVE, title) { _, _ -> listener() }
}

fun AlertDialog.setPositiveButton(@StringRes titleRes: Int, listener: () -> Unit = {}) {
    setPositiveButton(context.getString(titleRes), listener)
}
fun Activity.finishWithAlert(@StringRes titleRes: Int, @StringRes messageRes: Int) {
    AlertDialog.Builder(this)
            .setTitle(titleRes)
            .setMessage(messageRes)
            .setPositiveButton(R.string.button_ok) { _, _ -> finish() }
            .create()
            .show()
}

fun Activity.showStoragePermissionDeniedAlert() {
    finishWithAlert(R.string.permissions__permission_denied_title, R.string.permissions__storage_permission_denied_message)
}

fun AlertDialog.Builder.positiveButton(@StringRes titleRes: Int, listener: () -> Unit): AlertDialog.Builder {
    setPositiveButton(titleRes) { _, _ -> listener() }
    return this
}

fun AlertDialog.Builder.negativeButton(@StringRes titleRes: Int, listener: () -> Unit = {}): AlertDialog.Builder {
    setNegativeButton(titleRes) { _, _ -> listener() }
    return this
}