package com.ashalmawia.coriolan.data.logbook.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val VERSION = 1

class SqliteJornalOpenHelper(val context: Context)
    : SQLiteOpenHelper(context, "journal.db", null, VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        if (db == null) {
            return
        }

        db.execSQL("""
            |CREATE TABLE $SQLITE_TABLE_JOURNAL(
            |   $SQLITE_COLUMN_DATE INTEGER PRIMARY KEY,
            |   $SQLITE_COLUMN_PAYLOAD TEXT NOT NULL
            |);""".trimMargin())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}