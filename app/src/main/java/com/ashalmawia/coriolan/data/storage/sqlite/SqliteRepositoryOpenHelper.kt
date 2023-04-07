package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS_DECK_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS_DOMAIN_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS_FRONT_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS_TYPE
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTranslations.TRANSLATIONS
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTranslations.TRANSLATIONS_CARD_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTranslations.TRANSLATIONS_TERM_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.DECKS
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.DECKS_DOMAIN_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.DECKS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.DECKS_NAME
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.DOMAINS
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.DOMAINS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.DOMAINS_LANG_ORIGINAL
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.DOMAINS_LANG_TRANSLATIONS
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.DOMAINS_NAME
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractLanguages.LANGUAGES
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractLanguages.LANGUAGES_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractLanguages.LANGUAGES_VALUE
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.STATES
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.STATES_CARD_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.STATES_DUE_DATE
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.STATES_EXERCISE
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.STATES_INTERVAL
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.TERMS
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.TERMS_EXTRAS
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.TERMS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.TERMS_LANGUAGE_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.TERMS_VALUE

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
        db.execSQL("""CREATE TABLE $TRANSLATIONS(
                |$TRANSLATIONS_CARD_ID INTEGER NOT NULL,
                |$TRANSLATIONS_TERM_ID INTEGER NOT NULL,
                |PRIMARY KEY ($TRANSLATIONS_CARD_ID, $TRANSLATIONS_TERM_ID),
                |FOREIGN KEY ($TRANSLATIONS_CARD_ID) REFERENCES $CARDS ($CARDS_ID)
                |   ON DELETE CASCADE
                |   ON UPDATE CASCADE,
                |FOREIGN KEY ($TRANSLATIONS_TERM_ID) REFERENCES $TERMS ($TERMS_ID)
                |   ON DELETE RESTRICT
                |   ON UPDATE CASCADE
                |);""".trimMargin()
        )
        db.execSQL("""CREATE TABLE $STATES(
                |$STATES_CARD_ID INTEGER,
                |$STATES_EXERCISE TEXT,
                |$STATES_DUE_DATE INTEGER NOT NULL,
                |$STATES_INTERVAL INTEGER NOT NULL,
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