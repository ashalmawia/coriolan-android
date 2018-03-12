package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ashalmawia.coriolan.learning.Exercise

private const val SQLITE_VERSION = 1

class SqliteRepositoryOpenHelper(context: Context, private val exercises: List<Exercise>)
    : SQLiteOpenHelper(context, "data.db", null, SQLITE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        if (db == null) {
            return
        }

        db.execSQL("""CREATE TABLE $SQLITE_TABLE_LANGUAGES(
            |$SQLITE_COLUMN_ID INTEGER PRIMARY KEY,
            |$SQLITE_COLUMN_LANG_VALUE TEXT UNIQUE NOT NULL
            |);""".trimMargin()
        )
        db.execSQL("""CREATE TABLE $SQLITE_TABLE_EXPRESSIONS(
            |$SQLITE_COLUMN_ID INTEGER PRIMARY KEY,
            |$SQLITE_COLUMN_VALUE TEXT NOT NULL,
            |$SQLITE_COLUMN_TYPE TEXT NOT NULL,
            |$SQLITE_COLUMN_LANGUAGE_ID INTEGER NUL NULL,
            |FOREIGN KEY ($SQLITE_COLUMN_LANGUAGE_ID) REFERENCES $SQLITE_TABLE_LANGUAGES ($SQLITE_COLUMN_ID)
            |   ON DELETE RESTRICT
            |   ON UPDATE CASCADE,
            |UNIQUE ($SQLITE_COLUMN_VALUE, $SQLITE_COLUMN_LANGUAGE_ID)
            |   ON CONFLICT ABORT
            |);""".trimMargin()
        )
        db.execSQL("""CREATE TABLE $SQLITE_TABLE_DOMAINS(
            |$SQLITE_COLUMN_ID INTEGER PRIMARY KEY,
            |$SQLITE_COLUMN_NAME TEXT UNIQUE NOT NULL,
            |$SQLITE_COLUMN_LANG_ORIGINAL INTEGER NOT NULL,
            |$SQLITE_COLUMN_LANG_TRANSLATIONS INTEGER NOT NULL,
            |FOREIGN KEY ($SQLITE_COLUMN_LANG_ORIGINAL) REFERENCES $SQLITE_TABLE_LANGUAGES ($SQLITE_COLUMN_ID)
            |   ON DELETE RESTRICT
            |   ON UPDATE CASCADE,
            |FOREIGN KEY ($SQLITE_COLUMN_LANG_TRANSLATIONS) REFERENCES $SQLITE_TABLE_LANGUAGES ($SQLITE_COLUMN_ID)
            |   ON DELETE RESTRICT
            |   ON UPDATE CASCADE
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
            |FOREIGN KEY ($SQLITE_COLUMN_FRONT_ID) REFERENCES $SQLITE_TABLE_EXPRESSIONS ($SQLITE_COLUMN_ID)
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
            |$SQLITE_COLUMN_EXPRESSION_ID INTEGER NOT NULL,
            |PRIMARY KEY ($SQLITE_COLUMN_CARD_ID, $SQLITE_COLUMN_EXPRESSION_ID),
            |FOREIGN KEY ($SQLITE_COLUMN_CARD_ID) REFERENCES $SQLITE_TABLE_CARDS ($SQLITE_COLUMN_ID)
            |   ON DELETE CASCADE
            |   ON UPDATE CASCADE,
            |FOREIGN KEY ($SQLITE_COLUMN_EXPRESSION_ID) REFERENCES $SQLITE_TABLE_EXPRESSIONS ($SQLITE_COLUMN_ID)
            |   ON DELETE RESTRICT
            |   ON UPDATE CASCADE
            |);""".trimMargin()
        )
        createTablesForExercises(db, exercises)
    }

    private fun createTablesForExercises(db: SQLiteDatabase, exercises: List<Exercise>) {
        exercises.forEach { createTableExerciseState(db, sqliteTableExerciseState(it)) }
    }

    private fun createTableExerciseState(db: SQLiteDatabase, tableName: String) {
        db.execSQL("""CREATE TABLE $tableName(
                |$SQLITE_COLUMN_CARD_ID INTEGER PRIMARY KEY,
                |$SQLITE_COLUMN_DUE INTEGER NOT NULL,
                |$SQLITE_COLUMN_PERIOD INTEGER NOT NULL,
                |FOREIGN KEY ($SQLITE_COLUMN_CARD_ID) REFERENCES $SQLITE_TABLE_CARDS ($SQLITE_COLUMN_ID)
                |   ON DELETE CASCADE
                |   ON UPDATE CASCADE
                |);""".trimMargin())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    override fun onConfigure(db: SQLiteDatabase?) {
        super.onConfigure(db)
        if (db == null) {
            return
        }

        db.setForeignKeyConstraintsEnabled(true)
    }
}