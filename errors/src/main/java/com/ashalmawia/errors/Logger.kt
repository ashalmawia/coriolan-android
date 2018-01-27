package com.ashalmawia.errors

import android.util.Log
import com.crashlytics.android.Crashlytics

interface Logger {
    fun logError(tag: String, error: Throwable)
}

internal class AndroidLogger : Logger {

    override fun logError(tag: String, error: Throwable) {
        Log.e(tag, error.message, error)
    }
}

internal class CrashlyticsLogger : Logger {

    override fun logError(tag: String, error: Throwable) {
        Crashlytics.logException(error)
    }
}