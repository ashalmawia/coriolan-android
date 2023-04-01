package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val SCHEMA_VERSION = 1

/**
 * Production classes should never instantiate this class directly but prefer using
 * Companion object's get() function instead, otherwise having multiple instances of SQLiteOpenHelper
 * will be a well-known sourse of bugs.
 */
class SqliteRepositoryOpenHelper(
        context: Context,
        dbName: String = "data.db"
) : SQLiteOpenHelper(context, dbName, null, SCHEMA_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        initializeDatabaseSchema(db)
    }

    fun initializeDatabaseSchema(db: SQLiteDatabase) {
        db.execSQL("""CREATE TABLE $SQLITE_TABLE_LANGUAGES(
                |$SQLITE_COLUMN_ID INTEGER PRIMARY KEY,
                |$SQLITE_COLUMN_LANG_VALUE TEXT UNIQUE NOT NULL
                |);""".trimMargin()
        )
        db.execSQL("""CREATE TABLE $SQLITE_TABLE_TERMS(
                |$SQLITE_COLUMN_ID INTEGER PRIMARY KEY,
                |$SQLITE_COLUMN_VALUE TEXT NOT NULL,
                |$SQLITE_COLUMN_LANGUAGE_ID INTEGER NOT NULL,
                |$SQLITE_COLUMN_EXTRAS TEXT,
                |FOREIGN KEY ($SQLITE_COLUMN_LANGUAGE_ID) REFERENCES $SQLITE_TABLE_LANGUAGES ($SQLITE_COLUMN_ID)
                |   ON DELETE RESTRICT
                |   ON UPDATE CASCADE,
                |UNIQUE ($SQLITE_COLUMN_VALUE, $SQLITE_COLUMN_LANGUAGE_ID)
                |   ON CONFLICT ABORT
                |);""".trimMargin()
        )
        db.execSQL("""CREATE TABLE $SQLITE_TABLE_DOMAINS(
                |$SQLITE_COLUMN_ID INTEGER PRIMARY KEY,
                |$SQLITE_COLUMN_NAME TEXT,
                |$SQLITE_COLUMN_LANG_ORIGINAL INTEGER NOT NULL,
                |$SQLITE_COLUMN_LANG_TRANSLATIONS INTEGER NOT NULL,
                |FOREIGN KEY ($SQLITE_COLUMN_LANG_ORIGINAL) REFERENCES $SQLITE_TABLE_LANGUAGES ($SQLITE_COLUMN_ID)
                |   ON DELETE RESTRICT
                |   ON UPDATE CASCADE,
                |FOREIGN KEY ($SQLITE_COLUMN_LANG_TRANSLATIONS) REFERENCES $SQLITE_TABLE_LANGUAGES ($SQLITE_COLUMN_ID)
                |   ON DELETE RESTRICT
                |   ON UPDATE CASCADE,
                |UNIQUE ($SQLITE_COLUMN_LANG_ORIGINAL, $SQLITE_COLUMN_LANG_TRANSLATIONS)
                |);""".trimMargin()
        )
        db.execSQL("""CREATE TABLE $SQLITE_TABLE_DECKS(
                |$SQLITE_COLUMN_ID INTEGER PRIMARY KEY,
                |$SQLITE_COLUMN_NAME TEXT NOT NULL,
                |$SQLITE_COLUMN_DOMAIN_ID INTEGER NOT NULL,
                |FOREIGN KEY ($SQLITE_COLUMN_DOMAIN_ID) REFERENCES $SQLITE_TABLE_DOMAINS ($SQLITE_COLUMN_ID)
                |   ON DELETE CASCADE
                |   ON UPDATE CASCADE,
                |UNIQUE ($SQLITE_COLUMN_NAME, $SQLITE_COLUMN_DOMAIN_ID)
                |   ON CONFLICT ABORT
                );""".trimMargin()
        )
        db.execSQL("""CREATE TABLE $SQLITE_TABLE_CARDS(
                |$SQLITE_COLUMN_ID INTEGER PRIMARY KEY,
                |$SQLITE_COLUMN_FRONT_ID INTEGER NOT NULL,
                |$SQLITE_COLUMN_DECK_ID INTEGER NOT NULL,
                |$SQLITE_COLUMN_DOMAIN_ID INTEGER NOT NULL,
                |FOREIGN KEY ($SQLITE_COLUMN_FRONT_ID) REFERENCES $SQLITE_TABLE_TERMS ($SQLITE_COLUMN_ID)
                |   ON DELETE RESTRICT
                |   ON UPDATE CASCADE,
                |FOREIGN KEY ($SQLITE_COLUMN_DECK_ID) REFERENCES $SQLITE_TABLE_DECKS ($SQLITE_COLUMN_ID)
                |   ON DELETE RESTRICT
                |   ON UPDATE CASCADE,
                |FOREIGN KEY ($SQLITE_COLUMN_DOMAIN_ID) REFERENCES $SQLITE_TABLE_DOMAINS ($SQLITE_COLUMN_ID)
                |   ON DELETE CASCADE
                |   ON UPDATE CASCADE
                |);""".trimMargin()
        )
        db.execSQL("""CREATE TABLE $SQLITE_TABLE_CARDS_REVERSE(
                |$SQLITE_COLUMN_CARD_ID INTEGER NOT NULL,
                |$SQLITE_COLUMN_TERM_ID INTEGER NOT NULL,
                |PRIMARY KEY ($SQLITE_COLUMN_CARD_ID, $SQLITE_COLUMN_TERM_ID),
                |FOREIGN KEY ($SQLITE_COLUMN_CARD_ID) REFERENCES $SQLITE_TABLE_CARDS ($SQLITE_COLUMN_ID)
                |   ON DELETE CASCADE
                |   ON UPDATE CASCADE,
                |FOREIGN KEY ($SQLITE_COLUMN_TERM_ID) REFERENCES $SQLITE_TABLE_TERMS ($SQLITE_COLUMN_ID)
                |   ON DELETE RESTRICT
                |   ON UPDATE CASCADE
                |);""".trimMargin()
        )
        db.execSQL("""CREATE TABLE $SQLITE_TABLE_CARD_STATES(
                |   $SQLITE_COLUMN_CARD_ID INTEGER PRIMARY KEY,
                |   $SQLITE_COLUMN_STATE_SR_DUE INTEGER NOT NULL,
                |   $SQLITE_COLUMN_STATE_SR_PERIOD INTEGER NOT NULL,
                |   FOREIGN KEY ($SQLITE_COLUMN_CARD_ID) REFERENCES $SQLITE_TABLE_CARDS ($SQLITE_COLUMN_ID)
                |      ON DELETE CASCADE
                |      ON UPDATE CASCADE
                |);""".trimMargin())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }
}