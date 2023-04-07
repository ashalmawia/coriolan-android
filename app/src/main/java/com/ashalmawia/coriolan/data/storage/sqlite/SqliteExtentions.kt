package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.ashalmawia.coriolan.data.storage.sqlite.contract.SqliteUtils.from
import org.joda.time.DateTime

fun ContentValues.put(key: String, value: DateTime) {
    put(key, value.toDate().time)
}

fun SQLiteDatabase.insertOrUpdate(table: String, cv: ContentValues): Long {
    return insertWithOnConflict(table, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
}

fun Cursor.isNull(name: String, alias: String? = null): Boolean { return isNull(getColumnIndexOrThrow(name.from(alias))) }
fun Cursor.string(name: String, alias: String? = null): String { return getString(getColumnIndexOrThrow(name.from(alias))) }
fun Cursor.stringOrNull(name: String, alias: String? = null): String? {
    val columnIndex = getColumnIndex(name.from(alias))
    return if (columnIndex == -1) null else getString(columnIndex)
}

fun Cursor.int(name: String, alias: String? = null): Int { return getInt(getColumnIndexOrThrow(name.from(alias))) }
fun Cursor.long(name: String, alias: String? = null): Long { return getLong(getColumnIndexOrThrow(name.from(alias))) }
fun Cursor.date(column: String, alias: String? = null): DateTime {
    val longValue = long(column, alias)
    return DateTime(longValue)
}