package com.ashalmawia.coriolan.data.journal.sqlite

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
            |   $SQLITE_COLUMN_DATE INTEGER NOT NULL,
            |   $SQLITE_COLUMN_EXERCISE TEXT NOT NULL,
            |   $SQLITE_COLUMN_CARDS_NEW INTEGER NOT NULL,
            |   $SQLITE_COLUMN_CARDS_REVIEW INTEGER NOT NULL,
            |   $SQLITE_COLUMN_CARDS_RELEARN INTEGER NOT NULL,
            |   $SQLITE_COLUMN_CARDS_LEARNT INTEGER NOT NULL,
            |   
            |   PRIMARY KEY ($SQLITE_COLUMN_DATE, $SQLITE_COLUMN_EXERCISE)
            |);""".trimMargin())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}