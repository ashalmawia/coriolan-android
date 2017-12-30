package com.ashalmawia.coriolan.data.storage.sqlite

import android.database.Cursor
import com.ashalmawia.coriolan.model.ExpressionType
import com.ashalmawia.coriolan.model.toExpressionType

private fun Cursor.getString(name: String): String { return getString(getColumnIndex(name)) }
private fun Cursor.getInt(name: String): Int { return getInt(getColumnIndex(name)) }
private fun Cursor.getLong(name: String): Long { return getLong(getColumnIndex(name)) }

fun Cursor.getValue(): String { return getString(SQLITE_COLUMN_VALUE) }
fun Cursor.getName(): String { return getString(SQLITE_COLUMN_NAME) }

fun Cursor.getId(): Long { return getLong(SQLITE_COLUMN_ID) }
fun Cursor.getFrontId(): Long { return getLong(SQLITE_COLUMN_FRONT_ID) }
fun Cursor.getExpressionId(): Long { return getLong(SQLITE_COLUMN_EXPRESSION_ID) }

fun Cursor.getExpressionType(): ExpressionType {
    val intValue = getInt(SQLITE_COLUMN_TYPE)
    return toExpressionType(intValue)
}