package com.ashalmawia.coriolan.ui.util

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.os.Build.VERSION
import androidx.annotation.StringRes
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
fun Activity.showAlert(@StringRes titleRes: Int, @StringRes messageRes: Int) {
    AlertDialog.Builder(this)
            .setTitle(titleRes)
            .setMessage(messageRes)
            .setPositiveButton(R.string.button_ok) { _, _ -> finish() }
            .create()
            .show()
}

fun Activity.showStoragePermissionDeniedAlert() {
    showAlert(R.string.permissions__permission_denied_title, R.string.permissions__storage_permission_denied_message)
}

fun manageStoragePermission(): String {
    return if (VERSION.SDK_INT >= 30) {
        Manifest.permission.MANAGE_EXTERNAL_STORAGE
    } else {
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    }
}