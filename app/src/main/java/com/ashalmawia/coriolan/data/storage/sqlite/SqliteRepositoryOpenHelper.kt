package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ashalmawia.coriolan.learning.ExerciseDescriptor
import com.ashalmawia.coriolan.learning.scheduler.StateType

private const val SCHEMA_VERSION = 1
private const val DATABASE_NAME = "data.db"

/**
 * Production classes should never instantiate this class directly but prefer using
 * Companion object's get() function instead, otherwise having multiple instances of SQLiteOpenHelper
 * will be a well-known sourse of bugs.
 */
class SqliteRepositoryOpenHelper(context: Context, private val exercises: List<ExerciseDescriptor<*, *>>, dbName: String = DATABASE_NAME)
    : SQLiteOpenHelper(context, dbName, null, SCHEMA_VERSION) {

    companion object {
        private var value: SqliteRepositoryOpenHelper? = null

        fun get(context: Context, exercises: List<ExerciseDescriptor<*, *>>): SqliteRepositoryOpenHelper {
            if (value == null) {
                value = SqliteRepositoryOpenHelper(context, exercises)
            }
            return value!!
        }
    }

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
            |$SQLITE_COLUMN_NAME TEXT NULLABLE,
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

    private fun createTablesForExercises(db: SQLiteDatabase, exercises: List<ExerciseDescriptor<*, *>>) {
        exercises.filterNot { it.stateType == StateType.UNKNOWN }
                .forEach { createTableExerciseState(db, it.stateType, sqliteTableExerciseState(it.stableId)) }
    }

    private fun createTableExerciseState(db: SQLiteDatabase, type: StateType, tableName: String) {
        when (type) {
            StateType.SR_STATE -> db.execSQL("""
                |CREATE TABLE $tableName(
                |   $SQLITE_COLUMN_CARD_ID INTEGER PRIMARY KEY,
                |   $SQLITE_COLUMN_DUE INTEGER NOT NULL,
                |   $SQLITE_COLUMN_PERIOD INTEGER NOT NULL,
                |   FOREIGN KEY ($SQLITE_COLUMN_CARD_ID) REFERENCES $SQLITE_TABLE_CARDS ($SQLITE_COLUMN_ID)
                |      ON DELETE CASCADE
                |      ON UPDATE CASCADE
                |);""".trimMargin())

            else -> throw IllegalArgumentException("state type $type is not handled")
        }
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