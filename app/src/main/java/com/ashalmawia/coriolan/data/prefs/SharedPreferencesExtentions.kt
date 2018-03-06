package com.ashalmawia.coriolan.data.prefs

import android.content.SharedPreferences

fun SharedPreferences.getLongOrNull(key: String): Long? {
    return if (contains(key))
        getLong(key, 0L)
    else
        null
}

fun SharedPreferences.getIntOrNull(key: String): Int? {
    return if (contains(key))
        getInt(key, 0)
    else
        null
}