package com.ashalmawia.coriolan.data.storage.sqlite

import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.backup.CardInfo
import com.ashalmawia.coriolan.data.backup.DeckInfo
import com.ashalmawia.coriolan.data.backup.DomainInfo
import com.ashalmawia.coriolan.data.backup.LearningProgressInfo
import com.ashalmawia.coriolan.data.backup.LanguageInfo
import com.ashalmawia.coriolan.data.backup.TermInfo
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.cardsCardType
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.cardsDeckId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.cardsDomainId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.cardsFrontId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.cardsId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.cardsPayload
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.createCardContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.createCardPayload
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.DECKS
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.DECKS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.createDeckContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.decksDomainId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.decksId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.decksName
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.DOMAINS
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.DOMAINS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.createDomainContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.domainsId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.domainsName
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.domainsOriginalLangId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.domainsTranslationsLangId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractLanguages.LANGUAGES
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractLanguages.LANGUAGES_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractLanguages.createLanguageContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractLanguages.languagesId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractLanguages.languagesValue
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.STATES
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.STATES_CARD_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.createCardStateContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.statesCardId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.statesDateDue
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.statesInterval
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.TERMS
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.TERMS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.createTermContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.createTermPayload
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.termsPayload
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.termsId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.termsLanguageId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.termsValue

class SqliteBackupHelper(
        private val helper: SqliteRepositoryOpenHelper
) : BackupableRepository {

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
                        cursor.termsPayload().transcription
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
                        cursor.cardsPayload().translationIds.map { it.id },
                        cursor.cardsCardType()
                    )
                )
            }
            return list
        }
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

    override fun allExerciseStates(offset: Int, limit: Int): List<LearningProgressInfo> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $STATES
            |   ORDER BY $STATES_CARD_ID ASC
            |   LIMIT $limit OFFSET $offset
        """.trimMargin(), arrayOf())

        cursor.use {
            val list = mutableListOf<LearningProgressInfo>()
            while (cursor.moveToNext()) {
                list.add(LearningProgressInfo(
                        cursor.statesCardId(), cursor.statesDateDue(), cursor.statesInterval()
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
                    createLanguageContentValues(id = it.id, value = it.value)
            )
        }
    }

    override fun writeDomains(domains: List<DomainInfo>) {
        val db = helper.writableDatabase

        domains.forEach {
            db.insertOrThrow(DOMAINS, null,
                    createDomainContentValues(it.name, it.origLangId, it.transLangId, it.id)
            )
        }
    }

    override fun writeTerms(terms: List<TermInfo>) {
        val db = helper.writableDatabase

        terms.forEach {
            db.insertOrThrow(TERMS, null,
                    createTermContentValues(it.value, it.languageId, createTermPayload(it.transcription), it.id)
            )
        }
    }

    override fun writeCards(cards: List<CardInfo>) {
        val db = helper.writableDatabase

        cards.forEach {
            val payload = createCardPayload(it.translationIds)
            db.insertOrThrow(CARDS, null,
                    createCardContentValues(it.domainId, it.deckId, it.originalId, it.cardType!!, payload, it.id)
            )
        }
    }

    override fun writeDecks(decks: List<DeckInfo>) {
        val db = helper.writableDatabase

        decks.forEach {
            db.insertOrThrow(DECKS, null,
                    createDeckContentValues(it.domainId, it.name, it.id)
            )
        }
    }

    override fun writeExerciseStates(states: List<LearningProgressInfo>) {
        val db = helper.writableDatabase
        states.forEach {
            val cv = createCardStateContentValues(it.cardId, it.due, it.interval)
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