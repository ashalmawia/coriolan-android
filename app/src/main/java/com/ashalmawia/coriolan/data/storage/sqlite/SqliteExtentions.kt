package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.ashalmawia.coriolan.model.Expression
import com.ashalmawia.coriolan.model.ExpressionType
import com.ashalmawia.coriolan.model.Language
import com.ashalmawia.coriolan.model.toExpressionType
import com.ashalmawia.coriolan.util.*
import org.joda.time.DateTime

fun Cursor.getValue(alias: String? = null): String { return getString(SQLITE_COLUMN_VALUE, alias) }
fun Cursor.getLangValue(alias: String? = null): String { return getString(SQLITE_COLUMN_LANG_VALUE, alias) }
fun Cursor.getName(alias: String? = null): String { return getString(SQLITE_COLUMN_NAME, alias) }
fun Cursor.getNameIfAny(alias: String? = null): String? { return getStringOrNull(SQLITE_COLUMN_NAME, alias) }

fun Cursor.getId(alias: String? = null): Long { return getLong(SQLITE_COLUMN_ID, alias) }
fun Cursor.getDeckId(alias: String? = null): Long { return getLong(SQLITE_COLUMN_DECK_ID, alias) }
fun Cursor.getDomainId(alias: String? = null): Long { return getLong(SQLITE_COLUMN_DOMAIN_ID, alias) }
fun Cursor.getFrontId(alias: String? = null): Long { return getLong(SQLITE_COLUMN_FRONT_ID, alias) }
fun Cursor.getExpressionId(alias: String? = null): Long { return getLong(SQLITE_COLUMN_EXPRESSION_ID, alias) }
fun Cursor.getOriginalLangId(alias: String? = null): Long { return getLong(SQLITE_COLUMN_LANG_ORIGINAL, alias) }
fun Cursor.getTranslationsLangId(alias: String? = null): Long { return getLong(SQLITE_COLUMN_LANG_TRANSLATIONS, alias) }
fun Cursor.getLanguageId(alias: String? = null): Long { return getLong(SQLITE_COLUMN_LANGUAGE_ID, alias) }
fun Cursor.getCardId(alias: String? = null): Long { return getLong(SQLITE_COLUMN_CARD_ID, alias) }

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

fun Cursor.getExpression(aliasExpressions: String, aliasLanguages: String): Expression {
    return Expression(
            getId(aliasExpressions),
            getValue(aliasExpressions),
            getExpressionType(aliasExpressions),
            getLanguage(aliasLanguages)
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