package com.ashalmawia.coriolan.data.storage.sqlite

import com.ashalmawia.coriolan.data.backup.*

class SqliteBackupHelper(
        private val helper: SqliteRepositoryOpenHelper
) : BackupableRepository {

    override fun beginTransaction() {
        helper.writableDatabase.beginTransaction()
    }

    override fun commitTransaction() {
        helper.writableDatabase.setTransactionSuccessful()
        helper.writableDatabase.endTransaction()
    }

    override fun rollbackTransaction() {
        helper.writableDatabase.endTransaction()
    }

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
                list.add(TermInfo(cursor.getId(), cursor.getValue(), cursor.getLanguageId()))
            }
            return list
        }
    }

    override fun allTermExtras(offset: Int, limit: Int): List<TermExtraInfo> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $SQLITE_TABLE_TERM_EXTRAS
            |   ORDER BY $SQLITE_COLUMN_ID ASC
            |   LIMIT $limit OFFSET $offset
        """.trimMargin(), arrayOf())

        cursor.use {
            val list = mutableListOf<TermExtraInfo>()
            while (cursor.moveToNext()) {
                list.add(TermExtraInfo(
                        cursor.getId(), cursor.getTermId(), cursor.getType(), cursor.getValue()))
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
                list.add(CardInfo(cardId, cursor.getDeckId(), cursor.getDomainId(), cursor.getFrontId(), translationsByCardId(cardId)))
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

    override fun allCardStates(offset: Int, limit: Int): List<CardStateInfo> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $SQLITE_TABLE_CARD_STATES
            |   ORDER BY $SQLITE_COLUMN_CARD_ID ASC
            |   LIMIT $limit OFFSET $offset
        """.trimMargin(), arrayOf())

        cursor.use {
            val list = mutableListOf<CardStateInfo>()
            while (cursor.moveToNext()) {
                list.add(CardStateInfo(cursor.getCardId(), cursor.getDateDue(), cursor.getPeriod()))
            }
            return list
        }
    }

    override fun clearAll() {
        helper.deleteDatabase()
    }

    override fun writeLanguages(languages: List<LanguageInfo>) {
        val db = helper.writableDatabase

        languages.forEach {
            db.insertOrThrow(SQLITE_TABLE_LANGUAGES, null,
                    createLanguageContentValues(id = it.id, value = it.value)
            )
        }
    }

    override fun writeDomains(domains: List<DomainInfo>) {
        val db = helper.writableDatabase

        domains.forEach {
            db.insertOrThrow(SQLITE_TABLE_DOMAINS, null,
                    createDomainContentValues(it.name, it.origLangId, it.transLangId, it.id)
            )
        }
    }

    override fun writeTerms(terms: List<TermInfo>) {
        val db = helper.writableDatabase

        terms.forEach {
            db.insertOrThrow(SQLITE_TABLE_TERMS, null,
                    createTermContentValues(it.value, it.languageId, it.id)
            )
        }
    }

    override fun writeTermExtras(extras: List<TermExtraInfo>) {
        val db = helper.writableDatabase

        extras.forEach {
            db.insertOrThrow(SQLITE_TABLE_TERM_EXTRAS, null,
                    createTermExtrasContentValues(it.termId, it.type, it.value, it.id)
            )
        }
    }

    override fun writeCards(cards: List<CardInfo>) {
        val db = helper.writableDatabase

        cards.forEach {
            db.insertOrThrow(SQLITE_TABLE_CARDS, null,
                    createCardContentValues(it.domainId, it.deckId, it.originalId, it.id)
            )
            generateCardsReverseContentValues(it.id, it.translationIds).forEach {
                db.insertOrThrow(SQLITE_TABLE_CARDS_REVERSE, null, it)
            }
        }
    }

    override fun writeDecks(decks: List<DeckInfo>) {
        val db = helper.writableDatabase

        decks.forEach {
            db.insertOrThrow(SQLITE_TABLE_DECKS, null,
                    createDeckContentValues(it.domainId, it.name, it.id)
            )
        }
    }

    override fun writeCardStates(states: List<CardStateInfo>) {
        val db = helper.writableDatabase
        states.forEach {
            db.insertOrThrow(SQLITE_TABLE_CARD_STATES, null,
                    createCardStateContentValues(it.cardId, it.due, it.period)
            )
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