package com.ashalmawia.coriolan.util

import android.app.Activity
import android.content.Intent
import com.ashalmawia.coriolan.ui.StartActivity

fun Activity.restartApp() {
    val intent = Intent(this, StartActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
    finishAffinity()
}