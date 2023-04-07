package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractLanguages
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTranslations

private const val SCHEMA_VERSION = 1

class SqliteRepositoryOpenHelper(
        context: Context,
        dbName: String = "data.db"
) : SQLiteOpenHelper(context, dbName, null, SCHEMA_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        initializeDatabaseSchema(db)
    }

    fun initializeDatabaseSchema(db: SQLiteDatabase) {
        db.execSQL(ContractLanguages.createQuery)
        db.execSQL(ContractTerms.createQuery)
        db.execSQL(ContractDomains.createQuery)
        db.execSQL(ContractDecks.createQuery)
        db.execSQL(ContractCards.createQuery)
        db.execSQL(ContractTranslations.createQuery)
        db.execSQL(ContractStates.createQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }
}