package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_DECK_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_DOMAIN_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_FRONT_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_REVERSE
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_REVERSE_CARD_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_REVERSE_TERM_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_TYPE
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DECKS
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DECKS_DOMAIN_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DECKS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DECKS_NAME
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DOMAINS
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DOMAINS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DOMAINS_LANG_ORIGINAL
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DOMAINS_LANG_TRANSLATIONS
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DOMAINS_NAME
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.LANGUAGES
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.LANGUAGES_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.LANGUAGES_VALUE
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.STATES
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.STATES_CARD_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.STATES_DUE_DATE
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.STATES_EXERCISE
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.STATES_PERIOD
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.TERMS
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.TERMS_EXTRAS
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.TERMS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.TERMS_LANGUAGE_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.TERMS_VALUE

private const val SCHEMA_VERSION = 1

class SqliteRepositoryOpenHelper(
        context: Context,
        dbName: String = "data.db"
) : SQLiteOpenHelper(context, dbName, null, SCHEMA_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        initializeDatabaseSchema(db)
    }

    fun initializeDatabaseSchema(db: SQLiteDatabase) {
        db.execSQL("""CREATE TABLE $LANGUAGES(
                |$LANGUAGES_ID INTEGER PRIMARY KEY,
                |$LANGUAGES_VALUE TEXT UNIQUE NOT NULL
                |);""".trimMargin()
        )
        db.execSQL("""CREATE TABLE $TERMS(
                |$TERMS_ID INTEGER PRIMARY KEY,
                |$TERMS_VALUE TEXT NOT NULL,
                |$TERMS_LANGUAGE_ID INTEGER NOT NULL,
                |$TERMS_EXTRAS TEXT,
                |FOREIGN KEY ($TERMS_LANGUAGE_ID) REFERENCES $LANGUAGES ($LANGUAGES_ID)
                |   ON DELETE RESTRICT
                |   ON UPDATE CASCADE,
                |UNIQUE ($TERMS_VALUE, $TERMS_LANGUAGE_ID)
                |   ON CONFLICT ABORT
                |);""".trimMargin()
        )
        db.execSQL("""CREATE TABLE $DOMAINS(
                |$DOMAINS_ID INTEGER PRIMARY KEY,
                |$DOMAINS_NAME TEXT,
                |$DOMAINS_LANG_ORIGINAL INTEGER NOT NULL,
                |$DOMAINS_LANG_TRANSLATIONS INTEGER NOT NULL,
                |FOREIGN KEY ($DOMAINS_LANG_ORIGINAL) REFERENCES $LANGUAGES ($LANGUAGES_ID)
                |   ON DELETE RESTRICT
                |   ON UPDATE CASCADE,
                |FOREIGN KEY ($DOMAINS_LANG_TRANSLATIONS) REFERENCES $LANGUAGES ($LANGUAGES_ID)
                |   ON DELETE RESTRICT
                |   ON UPDATE CASCADE,
                |UNIQUE ($DOMAINS_LANG_ORIGINAL, $DOMAINS_LANG_TRANSLATIONS)
                |);""".trimMargin()
        )
        db.execSQL("""CREATE TABLE $DECKS(
                |$DECKS_ID INTEGER PRIMARY KEY,
                |$DECKS_NAME TEXT NOT NULL,
                |$DECKS_DOMAIN_ID INTEGER NOT NULL,
                |FOREIGN KEY ($DECKS_DOMAIN_ID) REFERENCES $DOMAINS ($DOMAINS_ID)
                |   ON DELETE CASCADE
                |   ON UPDATE CASCADE,
                |UNIQUE ($DECKS_NAME, $DECKS_DOMAIN_ID)
                |   ON CONFLICT ABORT
                );""".trimMargin()
        )
        db.execSQL("""CREATE TABLE $CARDS(
                |$CARDS_ID INTEGER PRIMARY KEY,
                |$CARDS_FRONT_ID INTEGER NOT NULL,
                |$CARDS_DECK_ID INTEGER NOT NULL,
                |$CARDS_DOMAIN_ID INTEGER NOT NULL,
                |$CARDS_TYPE TEXT NOT NULL,
                |FOREIGN KEY ($CARDS_FRONT_ID) REFERENCES $TERMS ($TERMS_ID)
                |   ON DELETE RESTRICT
                |   ON UPDATE CASCADE,
                |FOREIGN KEY ($CARDS_DECK_ID) REFERENCES $DECKS ($DECKS_ID)
                |   ON DELETE RESTRICT
                |   ON UPDATE CASCADE,
                |FOREIGN KEY ($CARDS_DOMAIN_ID) REFERENCES $DOMAINS ($DOMAINS_ID)
                |   ON DELETE CASCADE
                |   ON UPDATE CASCADE
                |);""".trimMargin()
        )
        db.execSQL("""CREATE TABLE $CARDS_REVERSE(
                |$CARDS_REVERSE_CARD_ID INTEGER NOT NULL,
                |$CARDS_REVERSE_TERM_ID INTEGER NOT NULL,
                |PRIMARY KEY ($CARDS_REVERSE_CARD_ID, $CARDS_REVERSE_TERM_ID),
                |FOREIGN KEY ($CARDS_REVERSE_CARD_ID) REFERENCES $CARDS ($CARDS_ID)
                |   ON DELETE CASCADE
                |   ON UPDATE CASCADE,
                |FOREIGN KEY ($CARDS_REVERSE_TERM_ID) REFERENCES $TERMS ($TERMS_ID)
                |   ON DELETE RESTRICT
                |   ON UPDATE CASCADE
                |);""".trimMargin()
        )
        db.execSQL("""CREATE TABLE $STATES(
                |$STATES_CARD_ID INTEGER,
                |$STATES_EXERCISE TEXT,
                |$STATES_DUE_DATE INTEGER NOT NULL,
                |$STATES_PERIOD INTEGER NOT NULL,
                |PRIMARY KEY($STATES_CARD_ID, $STATES_EXERCISE),
                |FOREIGN KEY ($STATES_CARD_ID) REFERENCES $CARDS ($CARDS_ID)
                |   ON DELETE CASCADE
                |   ON UPDATE CASCADE
                |);""".trimMargin())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }
}