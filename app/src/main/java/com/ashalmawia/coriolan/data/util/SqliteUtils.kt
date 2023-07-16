package com.ashalmawia.coriolan.data.util

import android.database.sqlite.SQLiteDatabase
import com.ashalmawia.coriolan.data.storage.sqlite.string

fun dropAllTables(db: SQLiteDatabase) {
    val cursor = db.rawQuery("""
            SELECT name
                FROM sqlite_master
                WHERE type='table'
        """.trimIndent(), null)
    val tables = cursor.use {
        val result = mutableListOf<String>()
        while (it.moveToNext()) {
            val name = it.string("name", null)
            if (name == "android_metadata" || name == "sqlite_sequence") {
                continue
            }
            result.add(name)
        }
        result
    }

    tables.forEach { name ->
        db.execSQL("DROP TABLE IF EXISTS '$name'")
    }
}