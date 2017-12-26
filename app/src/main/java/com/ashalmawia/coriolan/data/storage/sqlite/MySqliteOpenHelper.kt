package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val SQLITE_VERSION = 1

class MySqliteOpenHelper(context: Context) : SQLiteOpenHelper(context, "data.db", null, SQLITE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("""CREATE TABLE $SQLITE_TABLE_EXPRESSIONS(
            |$SQLITE_COLUMN_ID INTEGER PRIMARY KEY,
            |$SQLITE_COLUMN_VALUE TEXT NOT NULL,
            |$SQLITE_COLUMN_TYPE TEXT NOT NULL
            |);""".trimMargin()
        )
        db.execSQL("""CREATE TABLE $SQLITE_TABLE_CARDS(
            |$SQLITE_COLUMN_ID INTEGER PRIMARY KEY,
            |$SQLITE_COLUMN_FRONT_ID INTEGER NOT NULL,
            |$SQLITE_COLUMN_REVERSE_ID INTEGER NOT NULL,
            |$SQLITE_COLUMN_DECK_ID INTEGER NOT NULL
            |);""".trimMargin()
        )
        // TODO: support multiple translations: https://trello.com/c/EJBtdetZ
//        db.execSQL("""CREATE TABLE $SQLITE_TABLE_CARDS_REVERSE(
//            |$SQLITE_COLUMN_CARD_ID INTEGER,
//            |$SQLITE_COLUMN_EXPRESSION_ID INTEGER
//            |);""".trimMargin()
//        )
        db.execSQL("""CREATE TABLE $SQLITE_TABLE_DECKS(
            |$SQLITE_COLUMN_ID INTEGER PRIMARY KEY,
            |$SQLITE_COLUMN_NAME TEXT NOT NULL
            );""".trimMargin())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}