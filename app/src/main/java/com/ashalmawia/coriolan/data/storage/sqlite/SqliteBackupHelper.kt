package com.ashalmawia.coriolan.data.storage.sqlite

import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.backup.CardInfo
import com.ashalmawia.coriolan.data.backup.DeckInfo
import com.ashalmawia.coriolan.data.backup.DomainInfo
import com.ashalmawia.coriolan.data.backup.ExerciseStateInfo
import com.ashalmawia.coriolan.data.backup.LanguageInfo
import com.ashalmawia.coriolan.data.backup.TermInfo
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_REVERSE
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_REVERSE_CARD_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DECKS
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DECKS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DOMAINS
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DOMAINS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.LANGUAGES
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.LANGUAGES_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.STATES
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.STATES_CARD_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.TERMS
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.TERMS_ID
import com.ashalmawia.coriolan.util.string

class SqliteBackupHelper(
        private val helper: SqliteRepositoryOpenHelper
) : BackupableRepository {

    private val deserializer: ExtrasDeserializer = CreateContentValues

    override fun allLanguages(offset: Int, limit: Int): List<LanguageInfo> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $LANGUAGES
            |   ORDER BY $LANGUAGES_ID ASC
            |   LIMIT $limit OFFSET $offset
        """.trimMargin(), arrayOf())

        cursor.use {
            val list = mutableListOf<LanguageInfo>()
            while (cursor.moveToNext()) {
                list.add(LanguageInfo(cursor.languagesId(), cursor.languagesValue()))
            }
            return list
        }
    }

    override fun allDomains(offset: Int, limit: Int): List<DomainInfo> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $DOMAINS
            |   ORDER BY $DOMAINS_ID ASC
            |   LIMIT $limit OFFSET $offset
        """.trimMargin(), arrayOf())

        cursor.use {
            val list = mutableListOf<DomainInfo>()
            while (cursor.moveToNext()) {
                list.add(DomainInfo(
                        cursor.domainsId(),
                        cursor.domainsName() ?: "",
                        cursor.domainsOriginalLangId(),
                        cursor.domainsTranslationsLangId()
                ))
            }
            return list
        }
    }

    override fun allTerms(offset: Int, limit: Int): List<TermInfo> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $TERMS
            |   ORDER BY $TERMS_ID ASC
            |   LIMIT $limit OFFSET $offset
        """.trimMargin(), arrayOf())

        cursor.use {
            val list = mutableListOf<TermInfo>()
            while (cursor.moveToNext()) {
                list.add(TermInfo(
                        cursor.termsId(),
                        cursor.termsValue(),
                        cursor.termsLanguageId(),
                        cursor.termsExtras(deserializer)
                ))
            }
            return list
        }
    }

    override fun allCards(offset: Int, limit: Int): List<CardInfo> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $CARDS
            |   ORDER BY $CARDS_ID ASC
            |   LIMIT $limit OFFSET $offset
        """.trimMargin(), arrayOf())

        cursor.use {
            val list = mutableListOf<CardInfo>()
            while (cursor.moveToNext()) {
                val cardId = cursor.cardsId()
                list.add(CardInfo(
                        cardId,
                        cursor.cardsDeckId(),
                        cursor.cardsDomainId(),
                        cursor.cardsFrontId(),
                        // TODO: make this a single query
                        translationsByCardId(cardId),
                        cursor.cardsCardType()
                    )
                )
            }
            return list
        }
    }

    private fun translationsByCardId(id: Long): List<Long> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
                |SELECT * FROM $CARDS_REVERSE
                |WHERE $CARDS_REVERSE_CARD_ID = ?
            """.trimMargin(),
                arrayOf(id.toString()))

        val translations = mutableListOf<Long>()
        while (cursor.moveToNext()) {
            translations.add(cursor.reverseTermId())
        }

        cursor.close()
        return translations
    }

    override fun allDecks(offset: Int, limit: Int): List<DeckInfo> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $DECKS
            |   ORDER BY $DECKS_ID ASC
            |   LIMIT $limit OFFSET $offset
        """.trimMargin(), arrayOf())

        cursor.use {
            val list = mutableListOf<DeckInfo>()
            while (cursor.moveToNext()) {
                list.add(DeckInfo(cursor.decksId(), cursor.decksDomainId(), cursor.decksName()))
            }
            return list
        }
    }

    override fun allCardStates(offset: Int, limit: Int): List<ExerciseStateInfo> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $STATES
            |   ORDER BY $STATES_CARD_ID ASC
            |   LIMIT $limit OFFSET $offset
        """.trimMargin(), arrayOf())

        cursor.use {
            val list = mutableListOf<ExerciseStateInfo>()
            while (cursor.moveToNext()) {
                list.add(ExerciseStateInfo(
                        cursor.statesCardId(), cursor.statesExerciseId(), cursor.statesDateDue(), cursor.statesPeriod()
                ))
            }
            return list
        }
    }

    override fun overrideRepositoryData(override: (BackupableRepository) -> Unit) {
        val db = helper.writableDatabase

        // must be done outside a transaction
        db.setForeignKeyConstraintsEnabled(false)

        db.beginTransaction()
        try {
            dropAllTables()
            helper.initializeDatabaseSchema(db)
            override(this)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

        // must be done outside a transaction
        db.setForeignKeyConstraintsEnabled(true)
    }

    private fun dropAllTables() {
        val db = helper.writableDatabase

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

    override fun writeLanguages(languages: List<LanguageInfo>) {
        val db = helper.writableDatabase

        languages.forEach {
            db.insertOrThrow(LANGUAGES, null,
                    CreateContentValues.createLanguageContentValues(id = it.id, value = it.value)
            )
        }
    }

    override fun writeDomains(domains: List<DomainInfo>) {
        val db = helper.writableDatabase

        domains.forEach {
            db.insertOrThrow(DOMAINS, null,
                    CreateContentValues.createDomainContentValues(it.name, it.origLangId, it.transLangId, it.id)
            )
        }
    }

    override fun writeTerms(terms: List<TermInfo>) {
        val db = helper.writableDatabase

        terms.forEach {
            db.insertOrThrow(TERMS, null,
                    CreateContentValues.createTermContentValues(it.value, it.languageId, it.extras, it.id)
            )
        }
    }

    override fun writeCards(cards: List<CardInfo>) {
        val db = helper.writableDatabase

        cards.forEach {
            db.insertOrThrow(CARDS, null,
                    CreateContentValues.createCardContentValues(it.domainId, it.deckId, it.originalId, it.cardType!!, it.id)
            )
            CreateContentValues.generateCardsReverseContentValues(it.id, it.translationIds).forEach { cv ->
                db.insertOrThrow(CARDS_REVERSE, null, cv)
            }
        }
    }

    override fun writeDecks(decks: List<DeckInfo>) {
        val db = helper.writableDatabase

        decks.forEach {
            db.insertOrThrow(DECKS, null,
                    CreateContentValues.createDeckContentValues(it.domainId, it.name, it.id)
            )
        }
    }

    override fun writeCardStates(states: List<ExerciseStateInfo>) {
        val db = helper.writableDatabase
        states.forEach {
            val cv = CreateContentValues.createCardStateContentValues(it.cardId, it.exerciseId, it.due, it.period)
            db.insertOrThrow(STATES, null, cv)
        }
    }

    override fun hasAtLeastOneCard(): Boolean {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT count(*)
            |   FROM $CARDS
        """.trimMargin(), arrayOf())

        cursor.use {
            cursor.moveToNext()
            return cursor.getInt(0) > 0
        }
    }
}