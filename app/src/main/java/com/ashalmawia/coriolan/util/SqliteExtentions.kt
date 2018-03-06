package com.ashalmawia.coriolan.util

import android.database.Cursor
import com.ashalmawia.coriolan.data.storage.sqlite.from
import org.joda.time.DateTime

fun Cursor.isNull(name: String, alias: String? = null): Boolean { return isNull(getColumnIndexOrThrow(name.from(alias))) }

fun Cursor.getString(name: String, alias: String?): String { return getString(getColumnIndexOrThrow(name.from(alias))) }
fun Cursor.getInt(name: String, alias: String?): Int { return getInt(getColumnIndexOrThrow(name.from(alias))) }
fun Cursor.getLong(name: String, alias: String?): Long { return getLong(getColumnIndexOrThrow(name.from(alias))) }
fun Cursor.getDate(column: String, alias: String? = null): DateTime {
    val longValue = getLong(column, alias)
    return DateTime(longValue)
}