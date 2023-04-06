package com.ashalmawia.coriolan.data.storage.sqlite

import com.ashalmawia.coriolan.data.backup.*
import com.ashalmawia.coriolan.util.getString

class SqliteBackupHelper(
        private val helper: SqliteRepositoryOpenHelper
) : BackupableRepository {

    private val deserializer: ExtrasDeserializer = CreateContentValues

    override fun allLanguages(offset: Int, limit: Int): List<LanguageInfo> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $SQLITE_TABLE_LANGUAGES
            |   ORDER BY $SQLITE_COLUMN_ID ASC
            |   LIMIT $limit OFFSET $offset
        """.trimMargin(), arrayOf())

        cursor.use {
            val list = mutableListOf<LanguageInfo>()
            while (cursor.moveToNext()) {
                list.add(LanguageInfo(cursor.getId(), cursor.getLangValue()))
            }
            return list
        }
    }

    override fun allDomains(offset: Int, limit: Int): List<DomainInfo> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $SQLITE_TABLE_DOMAINS
            |   ORDER BY $SQLITE_COLUMN_ID ASC
            |   LIMIT $limit OFFSET $offset
        """.trimMargin(), arrayOf())

        cursor.use {
            val list = mutableListOf<DomainInfo>()
            while (cursor.moveToNext()) {
                list.add(DomainInfo(cursor.getId(), cursor.getName(), cursor.getOriginalLangId(), cursor.getTranslationsLangId()))
            }
            return list
        }
    }

    override fun allTerms(offset: Int, limit: Int): List<TermInfo> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $SQLITE_TABLE_TERMS
            |   ORDER BY $SQLITE_COLUMN_ID ASC
            |   LIMIT $limit OFFSET $offset
        """.trimMargin(), arrayOf())

        cursor.use {
            val list = mutableListOf<TermInfo>()
            while (cursor.moveToNext()) {
                list.add(TermInfo(
                        cursor.getId(),
                        cursor.getValue(),
                        cursor.getLanguageId(),
                        cursor.getExtras(deserializer)
                ))
            }
            return list
        }
    }

    override fun allCards(offset: Int, limit: Int): List<CardInfo> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $SQLITE_TABLE_CARDS
            |   ORDER BY $SQLITE_COLUMN_ID ASC
            |   LIMIT $limit OFFSET $offset
        """.trimMargin(), arrayOf())

        cursor.use {
            val list = mutableListOf<CardInfo>()
            while (cursor.moveToNext()) {
                val cardId = cursor.getId()
                list.add(CardInfo(
                        cardId,
                        cursor.getDeckId(),
                        cursor.getDomainId(),
                        cursor.getFrontId(),
                        translationsByCardId(cardId),
                        cursor.getCardType()
                    )
                )
            }
            return list
        }
    }

    private fun translationsByCardId(id: Long): List<Long> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
                |SELECT * FROM $SQLITE_TABLE_CARDS_REVERSE
                |WHERE $SQLITE_COLUMN_CARD_ID = ?
            """.trimMargin(),
                arrayOf(id.toString()))

        // TODO: this is disastrously inoptimal, but who cares? https://trello.com/c/fkgQn5KD
        val translations = mutableListOf<Long>()
        while (cursor.moveToNext()) {
            translations.add(cursor.getTermId())
        }

        cursor.close()
        return translations
    }

    override fun allDecks(offset: Int, limit: Int): List<DeckInfo> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $SQLITE_TABLE_DECKS
            |   ORDER BY $SQLITE_COLUMN_ID ASC
            |   LIMIT $limit OFFSET $offset
        """.trimMargin(), arrayOf())

        cursor.use {
            val list = mutableListOf<DeckInfo>()
            while (cursor.moveToNext()) {
                list.add(DeckInfo(cursor.getId(), cursor.getDomainId(), cursor.getName()))
            }
            return list
        }
    }

    override fun allCardStates(offset: Int, limit: Int): List<ExerciseStateInfo> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $SQLITE_TABLE_CARD_STATES
            |   ORDER BY $SQLITE_COLUMN_CARD_ID ASC
            |   LIMIT $limit OFFSET $offset
        """.trimMargin(), arrayOf())

        cursor.use {
            val list = mutableListOf<ExerciseStateInfo>()
            while (cursor.moveToNext()) {
                list.add(ExerciseStateInfo(
                        cursor.getCardId(), cursor.getExerciseId(), cursor.getDateDue(), cursor.getPeriod()
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
                val name = it.getString("name", null)
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
            db.insertOrThrow(SQLITE_TABLE_LANGUAGES, null,
                    CreateContentValues.createLanguageContentValues(id = it.id, value = it.value)
            )
        }
    }

    override fun writeDomains(domains: List<DomainInfo>) {
        val db = helper.writableDatabase

        domains.forEach {
            db.insertOrThrow(SQLITE_TABLE_DOMAINS, null,
                    CreateContentValues.createDomainContentValues(it.name, it.origLangId, it.transLangId, it.id)
            )
        }
    }

    override fun writeTerms(terms: List<TermInfo>) {
        val db = helper.writableDatabase

        terms.forEach {
            db.insertOrThrow(SQLITE_TABLE_TERMS, null,
                    CreateContentValues.createTermContentValues(it.value, it.languageId, it.extras, it.id)
            )
        }
    }

    override fun writeCards(cards: List<CardInfo>) {
        val db = helper.writableDatabase

        cards.forEach {
            db.insertOrThrow(SQLITE_TABLE_CARDS, null,
                    CreateContentValues.createCardContentValues(it.domainId, it.deckId, it.originalId, it.cardType!!, it.id)
            )
            CreateContentValues.generateCardsReverseContentValues(it.id, it.translationIds).forEach {
                db.insertOrThrow(SQLITE_TABLE_CARDS_REVERSE, null, it)
            }
        }
    }

    override fun writeDecks(decks: List<DeckInfo>) {
        val db = helper.writableDatabase

        decks.forEach {
            db.insertOrThrow(SQLITE_TABLE_DECKS, null,
                    CreateContentValues.createDeckContentValues(it.domainId, it.name, it.id)
            )
        }
    }

    override fun writeCardStates(states: List<ExerciseStateInfo>) {
        val db = helper.writableDatabase
        states.forEach {
            val cv = CreateContentValues.createCardStateContentValues(it.cardId, it.exerciseId, it.due, it.period)
            db.insertOrThrow(SQLITE_TABLE_CARD_STATES, null, cv)
        }
    }

    override fun hasAtLeastOneCard(): Boolean {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT count(*)
            |   FROM $SQLITE_TABLE_CARDS
        """.trimMargin(), arrayOf())

        cursor.use {
            cursor.moveToNext()
            return cursor.getInt(0) > 0
        }
    }
}