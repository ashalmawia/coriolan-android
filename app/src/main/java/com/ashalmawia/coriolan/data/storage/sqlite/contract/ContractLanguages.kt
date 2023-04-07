package com.ashalmawia.coriolan.data.storage.sqlite.contract

import android.content.ContentValues
import android.database.Cursor
import com.ashalmawia.coriolan.model.Language
import com.ashalmawia.coriolan.data.storage.sqlite.long
import com.ashalmawia.coriolan.data.storage.sqlite.string

object ContractLanguages {

    const val LANGUAGES = "Languages"

    const val LANGUAGES_ID = "Lang_id"
    const val LANGUAGES_VALUE = "Lang_Value"
    const val LANGUAGES_PAYLOAD = "Lang_Payload"

    private val allColumns = arrayOf(
            LANGUAGES_ID,
            LANGUAGES_VALUE,
            LANGUAGES_PAYLOAD
    )
    fun allColumnsLanguages(alias: String? = null) = SqliteUtils.allColumns(allColumns, alias)

    val createQuery = """
        CREATE TABLE $LANGUAGES(
            $LANGUAGES_ID INTEGER PRIMARY KEY,
            $LANGUAGES_VALUE TEXT UNIQUE NOT NULL,
            $LANGUAGES_PAYLOAD TEXT
        );""".trimMargin()

    fun createLanguageContentValues(value: String, id: Long? = null): ContentValues {
        val cv = ContentValues()
        if (id != null) {
            cv.put(LANGUAGES_ID, id)
        }
        cv.put(LANGUAGES_VALUE, value)
        return cv
    }

    fun Cursor.languagesId(alias: String? = null): Long { return long(LANGUAGES_ID, alias) }
    fun Cursor.languagesValue(alias: String? = null): String { return string(LANGUAGES_VALUE, alias) }
    fun Cursor.language(alias: String? = null): Language {
        return Language(
                languagesId(alias),
                languagesValue(alias)
        )
    }

}