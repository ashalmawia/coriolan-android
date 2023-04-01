package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.ashalmawia.coriolan.model.Extras
import com.ashalmawia.coriolan.model.Term
import com.ashalmawia.coriolan.model.Language
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
fun Cursor.getTermId(alias: String? = null): Long { return getLong(SQLITE_COLUMN_TERM_ID, alias) }
fun Cursor.getOriginalLangId(alias: String? = null): Long { return getLong(SQLITE_COLUMN_LANG_ORIGINAL, alias) }
fun Cursor.getTranslationsLangId(alias: String? = null): Long { return getLong(SQLITE_COLUMN_LANG_TRANSLATIONS, alias) }
fun Cursor.getLanguageId(alias: String? = null): Long { return getLong(SQLITE_COLUMN_LANGUAGE_ID, alias) }
fun Cursor.getCardId(alias: String? = null): Long { return getLong(SQLITE_COLUMN_CARD_ID, alias) }
fun Cursor.getType(alias: String? = null): Int { return getInt(SQLITE_COLUMN_TYPE, alias)}

fun Cursor.getDateDue(alias: String? = null): DateTime { return getDate(SQLITE_COLUMN_STATE_SR_DUE, alias) }
fun Cursor.getPeriod(alias: String? = null): Int { return getInt(SQLITE_COLUMN_STATE_SR_PERIOD, alias) }
fun Cursor.hasSavedSRState(alias: String? = null): Boolean { return !isNull(SQLITE_COLUMN_STATE_SR_DUE, alias) }

fun Cursor.getLanguage(alias: String? = null): Language {
    return Language(
            getLong(SQLITE_COLUMN_ID, alias),
            getString(SQLITE_COLUMN_LANG_VALUE, alias)
    )
}

fun Cursor.getExtras(deserializer: ExtrasDeserializer, alias: String? = null): Extras {
    val serialized = getStringOrNull(SQLITE_COLUMN_EXTRAS, alias)
    return deserializer.deserialize(serialized)
}

fun Cursor.getTerm(deserializer: ExtrasDeserializer, aliasTerms: String, aliasLanguages: String): Term {
    return Term(
            getId(aliasTerms),
            getValue(aliasTerms),
            getLanguage(aliasLanguages),
            getExtras(deserializer, aliasTerms)
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