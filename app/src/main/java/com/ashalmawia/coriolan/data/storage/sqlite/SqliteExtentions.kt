package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.ContentValues
import android.database.Cursor
import com.ashalmawia.coriolan.model.ExpressionType
import com.ashalmawia.coriolan.model.toExpressionType
import java.util.*

private fun Cursor.isNull(name: String): Boolean { return isNull(getColumnIndex(name)) }

private fun Cursor.getString(name: String): String { return getString(getColumnIndex(name)) }
private fun Cursor.getInt(name: String): Int { return getInt(getColumnIndex(name)) }
private fun Cursor.getLong(name: String): Long { return getLong(getColumnIndex(name)) }

fun Cursor.getValue(): String { return getString(SQLITE_COLUMN_VALUE) }
fun Cursor.getName(): String { return getString(SQLITE_COLUMN_NAME) }

fun Cursor.getId(): Long { return getLong(SQLITE_COLUMN_ID) }
fun Cursor.getFrontId(): Long { return getLong(SQLITE_COLUMN_FRONT_ID) }
fun Cursor.getExpressionId(): Long { return getLong(SQLITE_COLUMN_EXPRESSION_ID) }

fun Cursor.getDateDue(): Date { return getDate(SQLITE_COLUMN_DUE) }
fun Cursor.getPeriod(): Int { return getInt(SQLITE_COLUMN_PERIOD) }
fun Cursor.hasSavedState(): Boolean { return !isNull(SQLITE_COLUMN_DUE) }

fun Cursor.getExpressionType(): ExpressionType {
    val intValue = getInt(SQLITE_COLUMN_TYPE)
    return toExpressionType(intValue)
}
fun Cursor.getDate(column: String): Date {
    val longValue = getLong(column)
    return Date(longValue)
}

fun ContentValues.put(key: String, value: Date) {
    put(key, value.time)
}
fun ContentValues.getAsDate(key: String): Date? {
    val timestamp = getAsLong(key)
    return if (timestamp == null) null else Date(timestamp)
}