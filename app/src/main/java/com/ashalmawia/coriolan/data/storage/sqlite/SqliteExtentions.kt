package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.ashalmawia.coriolan.model.ExpressionType
import com.ashalmawia.coriolan.model.Language
import com.ashalmawia.coriolan.model.toExpressionType
import com.ashalmawia.coriolan.util.getDate
import com.ashalmawia.coriolan.util.getLong
import com.ashalmawia.coriolan.util.getString
import com.ashalmawia.coriolan.util.getInt
import com.ashalmawia.coriolan.util.isNull
import org.joda.time.DateTime

fun Cursor.getValue(alias: String? = null): String { return getString(SQLITE_COLUMN_VALUE, alias) }
fun Cursor.getLangValue(alias: String? = null): String { return getString(SQLITE_COLUMN_LANG_VALUE, alias) }
fun Cursor.getName(alias: String? = null): String { return getString(SQLITE_COLUMN_NAME, alias) }

fun Cursor.getId(alias: String? = null): Long { return getLong(SQLITE_COLUMN_ID, alias) }
fun Cursor.getDeckId(alias: String? = null): Long { return getLong(SQLITE_COLUMN_DECK_ID, alias) }
//fun Cursor.getDomainId(alias: String? = null): Long { return getLong(SQLITE_COLUMN_DOMAIN_ID, alias) }
fun Cursor.getFrontId(alias: String? = null): Long { return getLong(SQLITE_COLUMN_FRONT_ID, alias) }
fun Cursor.getExpressionId(alias: String? = null): Long { return getLong(SQLITE_COLUMN_EXPRESSION_ID, alias) }

fun Cursor.getDateDue(alias: String? = null): DateTime { return getDate(SQLITE_COLUMN_DUE, alias) }
fun Cursor.getPeriod(alias: String? = null): Int { return getInt(SQLITE_COLUMN_PERIOD, alias) }
fun Cursor.hasSavedState(alias: String? = null): Boolean { return !isNull(SQLITE_COLUMN_DUE, alias) }

fun Cursor.getExpressionType(alias: String? = null): ExpressionType {
    val intValue = getInt(SQLITE_COLUMN_TYPE, alias)
    return toExpressionType(intValue)
}
fun Cursor.getLanguage(alias: String? = null): Language {
    return Language(
            getLong(SQLITE_COLUMN_ID, alias),
            getString(SQLITE_COLUMN_LANG_VALUE, alias)
    )
}

fun ContentValues.put(key: String, value: DateTime) {
    put(key, value.toDate().time)
}
fun ContentValues.getAsDate(key: String): DateTime? {
    val timestamp = getAsLong(key)
    return if (timestamp == null) null else DateTime(timestamp)
}

fun SQLiteDatabase.insertOrUpdate(table: String, cv: ContentValues): Long {
    return insertWithOnConflict(table, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
}