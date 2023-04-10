package com.ashalmawia.coriolan.data.storage.sqlite

import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.data.storage.DataProcessingException
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS_DECK_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS_DOMAIN_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS_FRONT_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS_PAYLOAD
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS_TYPE
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.cardWihoutTranslations
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.cardsFrontId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.cardsId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.cardsPayload
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.createCardContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.createCardPayload
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.DECKS
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.DECKS_DOMAIN_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.DECKS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.createDeckContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.deck
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.decksDomainId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.DOMAINS
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.DOMAINS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.DOMAINS_LANG_ORIGINAL
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.DOMAINS_LANG_TRANSLATIONS
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.allColumnsDomains
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.createDomainContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.domainsId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.domainsName
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractLanguages.LANGUAGES
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractLanguages.LANGUAGES_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractLanguages.LANGUAGES_VALUE
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractLanguages.allColumnsLanguages
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractLanguages.createLanguageContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractLanguages.language
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.STATES
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.STATES_IS_ACTIVE
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.STATES_CARD_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.STATES_DUE_DATE
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.STATES__CARD_ACTIVE
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.allColumnsStates
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.createAllLearningProgressContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.exerciseState
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.statesCardId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.statesDateDue
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.statesExerciseId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.statesHasSavedExerciseState
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.statesInterval
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.TERMS
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.TERMS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.TERMS_LANGUAGE_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.TERMS_VALUE
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.createTermContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.createTermPayload
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.term
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.termsId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.SqliteUtils.from
import com.ashalmawia.coriolan.data.storage.sqlite.payload.CardPayload
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.exercise.flashcards.ExerciseState
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.Language
import com.ashalmawia.coriolan.model.Term
import com.ashalmawia.coriolan.util.timespamp
import com.ashalmawia.errors.Errors
import org.joda.time.DateTime

private val TAG = SqliteStorage::class.java.simpleName

class SqliteStorage(private val helper: SqliteRepositoryOpenHelper) : Repository {

    override fun addLanguage(value: String): Language {
        val db = helper.writableDatabase
        val cv = createLanguageContentValues(value)

        try {
            val id = db.insertOrThrow(LANGUAGES, null, cv)
            return Language(id, value)
        } catch (e: SQLiteConstraintException) {
            throw DataProcessingException("failed to add langauge [$value], constraint violation", e)
        }
    }

    override fun languageById(id: Long): Language? {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            SELECT * FROM $LANGUAGES
            WHERE $LANGUAGES_ID = ?
        """.trimMargin(), arrayOf(id.toString()))

        cursor.use {
            return if (it.moveToNext()) {
                it.language()
            } else {
                null
            }
        }
    }

    override fun languageByName(name: String): Language? {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            SELECT *
               FROM $LANGUAGES
               WHERE $LANGUAGES_VALUE = ?
        """.trimMargin(), arrayOf(name))

        cursor.use {
            return if (it.moveToNext()) {
                it.language()
            } else {
                null
            }
        }
    }

    override fun addTerm(value: String, language: Language, transcription: String?): Term {
        try {
            val id = helper.writableDatabase.insert(TERMS,
                    null,
                    createTermContentValues(value, language, createTermPayload(transcription)))

            if (id < 0) {
                throw DataProcessingException("failed to add term [$value], lang $language: maybe missing lang")
            }

            return Term(id, value, language, transcription)
        } catch (e: SQLiteConstraintException) {
            throw DataProcessingException("failed to add term [$value], lang $language: constraint violation", e)
        }
    }

    override fun updateTerm(term: Term, transcription: String?): Term {
        val cv = createTermContentValues(
                term.value, term.language.id, createTermPayload(transcription), term.id
        )

        val db = helper.writableDatabase
        db.update(TERMS, cv, "$TERMS_ID == ?", arrayOf(term.id.toString()))

        return term.copy(transcription = transcription)
    }

    override fun termById(id: Long): Term? {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            SELECT *
            FROM 
                $TERMS
                LEFT JOIN $LANGUAGES ON $TERMS_LANGUAGE_ID = $LANGUAGES_ID
            WHERE $TERMS_ID = ?
            
            """.trimMargin(), arrayOf(id.toString()))

        if (cursor.count == 0) return null
        if (cursor.count > 1) throw IllegalStateException("more that one term for id $id")

        cursor.use {
            it.moveToFirst()
            return it.term()
        }
    }

    override fun termByValues(value: String, language: Language): Term? {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            SELECT *
            
            FROM $TERMS
                LEFT JOIN $LANGUAGES
                   ON $TERMS_LANGUAGE_ID = $LANGUAGES_ID
            
            WHERE $TERMS_VALUE = ? AND $TERMS_LANGUAGE_ID = ?
            
        """.trimMargin(), arrayOf(value, language.id.toString()))

        cursor.use {
            if (it.count == 0) {
                return null
            }

            if (it.count > 1) {
                Errors.illegalState(TAG, "found ${it.count} values for the value[$value], " +
                        "language[$language]")
                return null
            }

            it.moveToFirst()
            return it.term()
        }
    }

    override fun isUsed(term: Term): Boolean {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            SELECT COUNT(*)
            FROM $CARDS
            WHERE
               $CARDS_FRONT_ID = ${term.id}
               OR
               $CARDS_PAYLOAD LIKE '%{"id":${term.id}}%'
        """.trimMargin(), arrayOf())

        cursor.use {
            it.moveToFirst()
            val count = it.getInt(0)
            return count > 0
        }
    }

    override fun deleteTerm(term: Term) {
        val db = helper.writableDatabase
        try {
            val result = db.delete(TERMS, "$TERMS_ID = ?", arrayOf(term.id.toString()))
            if (result == 0) {
                throw DataProcessingException("failed to delete term $term: not in the database")
            }
        } catch (e: SQLiteConstraintException) {
            throw DataProcessingException("failed to delete term $term: constraint violation")
        }
    }

    override fun createDomain(name: String?, langOriginal: Language, langTranslations: Language): Domain {
        val db = helper.writableDatabase
        val cv = createDomainContentValues(name, langOriginal, langTranslations)

        try {
            val id = db.insertOrThrow(DOMAINS, null, cv)
            return Domain(id, name, langOriginal, langTranslations)
        } catch (e: SQLiteConstraintException) {
            throw DataProcessingException("failed to create domain $langOriginal -> $langTranslations, constraint violation", e)
        }
    }

    override fun domainById(id: Long): Domain {
        val db = helper.readableDatabase

        val LANGS1 = "L1"
        val LANGS2 = "L2"

        val cursor = db.rawQuery("""
            SELECT
               ${allColumnsDomains()},
               ${allColumnsLanguages(LANGS1)},
               ${allColumnsLanguages(LANGS2)}
            
               FROM $DOMAINS
                  LEFT JOIN $LANGUAGES AS $LANGS1
                      ON $DOMAINS_LANG_ORIGINAL = ${LANGUAGES_ID.from(LANGS1)}
                  LEFT JOIN $LANGUAGES AS $LANGS2
                      ON $DOMAINS_LANG_TRANSLATIONS = ${LANGUAGES_ID.from(LANGS2)}
            
               WHERE
                  $DOMAINS_ID = ?
        """.trimMargin(), arrayOf(id.toString()))

        cursor.use {
            it.moveToNext()
            return Domain(
                    it.domainsId(),
                    it.domainsName(),
                    it.language(LANGS1),
                    it.language(LANGS2)
            )
        }
    }

    override fun allDomains(): List<Domain> {
        val db = helper.readableDatabase

        val LANGS1 = "L1"
        val LANGS2 = "L2"

        val cursor = db.rawQuery("""
            SELECT
               ${allColumnsDomains()},
               ${allColumnsLanguages(LANGS1)},
               ${allColumnsLanguages(LANGS2)}
            
            FROM $DOMAINS
               LEFT JOIN $LANGUAGES AS $LANGS1
                   ON $DOMAINS_LANG_ORIGINAL = ${LANGUAGES_ID.from(LANGS1)}
               LEFT JOIN $LANGUAGES AS $LANGS2
                   ON $DOMAINS_LANG_TRANSLATIONS = ${LANGUAGES_ID.from(LANGS2)}
        """.trimMargin(), arrayOf())

        val list = mutableListOf<Domain>()
        cursor.use {
            while (it.moveToNext()) {
                list.add(Domain(
                        it.domainsId(),
                        it.domainsName(),
                        it.language(LANGS1),
                        it.language(LANGS2)
                ))
            }
        }

        return list
    }

    override fun addCard(domain: Domain, deckId: Long, original: Term, translations: List<Term>): Card {
        verifyTranslations(original, translations)

        val db = helper.writableDatabase
        val type = if (domain.langOriginal().id == original.language.id) CardType.FORWARD else CardType.REVERSE
        val cardId = try {
            db.insert(
                    CARDS,
                    null,
                    createCardContentValues(domain.id, deckId, original, type, createCardPayload(translations)))
        } catch (e: SQLiteConstraintException) {
            throw DataProcessingException("failed to add card ($original -> $translations), constraint violation", e)
        }

        if (cardId < 0) {
            throw DataProcessingException("failed to insert card ($original -> $translations)")
        }

        return Card(cardId, deckId, domain, type, original, translations)
    }

    private fun verifyTranslations(original: Term, translations: List<Term>) {
        if (translations.isEmpty()) {
            throw DataProcessingException("failed to process card with original[$original]: translations were empty")
        }
        try {
            verifyTermsExist(translations)
        } catch (e: DataProcessingException) {
            throw DataProcessingException("failed to process card with original[$original]: translations were missing", e)
        }
    }

    private fun verifyTermsExist(terms: List<Term>) {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            SELECT $TERMS_ID
            FROM $TERMS
            WHERE $TERMS_ID IN ( ${terms.map { it.id }.joinToString()} )
        """.trimIndent(), arrayOf())

        val found = mutableSetOf<Long>()
        cursor.use {
            while (it.moveToNext()) {
                found.add(it.termsId())
            }
        }

        if (found.size != terms.size) {
            val missingTerms = terms - found
            throw DataProcessingException("could not find terms with ids: $missingTerms")
        }
    }

    override fun cardById(id: Long, domain: Domain): Card? {
        val list = cardsWithIds(listOf(id), domain)
        return list.firstOrNull()
    }

    private fun cardsWithIds(ids: List<Long>, domain: Domain): List<Card> {
        if (ids.isEmpty()) throw IllegalArgumentException("ids list must not be empty")

        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            SELECT *
            FROM 
                $CARDS
            
                LEFT JOIN $TERMS
                    ON $CARDS_FRONT_ID = $TERMS_ID
        
                LEFT JOIN $LANGUAGES
                    ON $TERMS_LANGUAGE_ID = $LANGUAGES_ID
            
            WHERE $CARDS_ID IN (${ids.joinToString()})
        """.trimMargin(), arrayOf())

        return extractCardsFromCursor(cursor, domain)
    }

    private fun extractCardsFromCursor(cursor: Cursor, domain: Domain): List<Card> {
        val payloads = mutableMapOf<Long, CardPayload>()

        val cards = mutableListOf<Card>()
        cursor.use {
            while (cursor.moveToNext()) {
                val card = cursor.cardWihoutTranslations(domain)
                cards.add(card)
                payloads[card.id] = cursor.cardsPayload()
            }
        }

        val translations = termsWithIds(
                payloads.values.flatMap { it.translationIds }.map { it.id }
        )
        return cards.map { card ->
            card.copy(translations = payloads[card.id]!!.translationIds.map { translations[it.id]!! })
        }
    }

    override fun cardByValues(domain: Domain, original: Term): Card? {
        val db = helper.readableDatabase

        // find all cards with the same original
        val cursor = db.rawQuery("""
            SELECT *
            
               FROM $CARDS
            
                   LEFT JOIN $TERMS
                       ON $CARDS_FRONT_ID = $TERMS_ID
            
                   LEFT JOIN $LANGUAGES
                       ON $TERMS_LANGUAGE_ID = $LANGUAGES_ID
            
               WHERE
                   $CARDS_DOMAIN_ID = ?
                       AND
                   $CARDS_FRONT_ID = ?
        """.trimMargin(), arrayOf(domain.id.toString(), original.id.toString()))

        val (card, payload) = cursor.use {
            // go over these cards
            if (cursor.moveToNext()) {
                // we found the card we need
                // we can assume that there are no other cards like this due to merging
                Pair(cursor.cardWihoutTranslations(domain), cursor.cardsPayload())
            } else {
                null
            }
        } ?: return null

        return card.copy(
                translations = termsWithIds(payload.translationIds.map { it.id }).values.toList()
        )
    }

    override fun updateCard(card: Card, deckId: Long, original: Term, translations: List<Term>): Card {
        verifyTranslations(original, translations)

        val db = helper.writableDatabase
        val cv = createCardContentValues(
                card.domain.id, deckId, original, card.type, createCardPayload(translations), card.id
        )
        val updated = try {
            db.update(CARDS, cv, "$CARDS_ID = ?", arrayOf(card.id.toString()))
        } catch (e: SQLiteConstraintException) {
            throw DataProcessingException("failed to add card ($original -> $translations), constraint violation", e)
        }

        if (updated == 0) {
            throw DataProcessingException("failed to update card ${card.id}")
        }

        return Card(card.id, deckId, card.domain, card.type, original, translations)
    }

    override fun deleteCard(card: Card) {
        val db = helper.writableDatabase

        try {
            val result = db.delete(
                    CARDS,
                    "$CARDS_ID = ?",
                    arrayOf(card.id.toString())
            )

            if (result == 0) {
                throw DataProcessingException("failed to delete card[$card.id]: no such card")
            }
        } catch (e: Exception) {
            throw DataProcessingException("failed to delete card[$card.id]", e)
        }
    }

    override fun allCards(domain: Domain): List<Card> {
        val db = helper.readableDatabase

        val CARDS = "Cards"
        val TERMS = "Terms"
        val LANGUAGES = "Languages"

        val cursor = db.rawQuery("""
            SELECT *
            
            FROM 
                $CARDS
                LEFT JOIN $TERMS
                   ON $CARDS_FRONT_ID = $TERMS_ID
                LEFT JOIN $LANGUAGES
                   ON $TERMS_LANGUAGE_ID = $LANGUAGES_ID
            
            WHERE
               $CARDS_DOMAIN_ID = ?
        """.trimMargin(), arrayOf(domain.id.toString()))

        return extractCardsFromCursor(cursor, domain)
    }

    override fun allDecks(domain: Domain): List<Deck> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            SELECT *
               FROM $DECKS
               WHERE $DECKS_DOMAIN_ID = ?
        """.trimMargin(), arrayOf(domain.id.toString()))

        val list = mutableListOf<Deck>()
        cursor.use {
            while (it.moveToNext()) {
                list.add(it.deck(domain))
            }
        }

        return list
    }

    override fun deckById(id: Long): Deck {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            SELECT *
            FROM $DECKS
            WHERE $DECKS_ID = ?
        """.trimIndent(), arrayOf(id.toString()))

        cursor.use {
            if (it.count == 0) throw DataProcessingException("could not find deck for id $id")
            if (it.count > 1) throw DataProcessingException("more that one value for deck id $id")

            it.moveToFirst()
            val domain = domainById(it.decksDomainId())
            return it.deck(domain)
        }
    }

    override fun addDeck(domain: Domain, name: String): Deck {
        val db = helper.writableDatabase
        val cv = createDeckContentValues(domain.id, name)

        try {
            val id = db.insert(DECKS, null, cv)
            if (id < 0) {
                throw DataProcessingException("failed to add deck with name[$name] to domain[$domain.id]")
            }

            return Deck(id, domain, name)
        } catch (e: Exception) {
            throw DataProcessingException("failed to add deck with name[$name] to domain[$domain.id], constraint violation", e)
        }
    }

    override fun updateDeck(deck: Deck, name: String): Deck {
        val db = helper.writableDatabase
        val cv = createDeckContentValues(deck.domain.id, name)

        try {
            val updated = db.update(DECKS, cv, "$DECKS_ID = ?", arrayOf(deck.id.toString()))

            if (updated == 0) {
                throw DataProcessingException("failed to updated deck [${deck.name}] -> [$name]")
            }

            return Deck(deck.id, deck.domain, name)
        } catch (e: SQLiteConstraintException) {
            throw DataProcessingException("failed to updated deck [${deck.name}] -> [$name], constraint violation")
        }
    }

    override fun deleteDeck(deck: Deck): Boolean {
        val db = helper.writableDatabase

        try {
            val deleted = db.delete(DECKS, "$DECKS_ID = ?", arrayOf(deck.id.toString()))
            if (deleted == 0) {
                throw DataProcessingException("deck with the id ${deck.id} was not in the database")
            }
            return true
        } catch (e: SQLiteConstraintException) {
            // deck is not empty
            return false
        }
    }

    override fun cardsOfDeck(deck: Deck): List<Card> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            SELECT *
            
            FROM 
               $CARDS
            
               LEFT JOIN $TERMS
                   ON $CARDS_FRONT_ID = $TERMS_ID
            
               LEFT JOIN $LANGUAGES
                   ON $TERMS_LANGUAGE_ID = $LANGUAGES_ID
            
            WHERE $CARDS_DECK_ID = ?
            
            """.trimMargin(), arrayOf(deck.id.toString()))

        return extractCardsFromCursor(cursor, deck.domain)
    }

    private fun termsWithIds(ids: Collection<Long>): Map<Long, Term> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            SELECT *
            FROM $TERMS
            
               LEFT JOIN $LANGUAGES
                   ON $TERMS_LANGUAGE_ID = $LANGUAGES_ID
                   
            WHERE $TERMS_ID IN ( ${ids.joinToString()} )
        """.trimIndent(), arrayOf())

        val terms = mutableMapOf<Long, Term>()
        cursor.use {
            while (it.moveToNext()) {
                val term = it.term()
                terms[term.id] = term
            }
        }
        return terms
    }

    override fun getCardLearningProgress(card: Card): LearningProgress {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            SELECT *
               FROM $STATES
               WHERE $STATES_CARD_ID = ?
        """.trimMargin(), arrayOf(card.id.toString()))

        cursor.use {
            return extractLearningProgress(cursor)
        }
    }

    override fun updateCardLearningProgress(card: Card, learningProgress: LearningProgress) {
        val cvList = createAllLearningProgressContentValues(card.id, learningProgress)

        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            cvList.forEach {
                val result = db.insertWithOnConflict(
                        STATES, null, it, SQLiteDatabase.CONFLICT_REPLACE)
                if (result < 0) {
                    throw DataProcessingException("failed to updated card state for card ${card.id}: error occured")
                }
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            throw DataProcessingException("failed to updated card state for card $card.id: constraint violation", e)
        } finally {
            db.endTransaction()
        }
    }

    private fun pendingCardIds(deck: Deck, date: DateTime, types: Array<CardType>): Map<Long, LearningProgress> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            SELECT
                $CARDS_ID, $CARDS_DECK_ID, $CARDS_TYPE,
                ${allColumnsStates()}
            
            FROM $CARDS
               LEFT JOIN $STATES
                   ON $CARDS_ID = $STATES_CARD_ID
            
            WHERE
               $CARDS_DECK_ID = ?
                   AND
               $CARDS_TYPE IN (${types.joinToString { "'${it.value}'" }})
                   AND
               ($STATES_DUE_DATE IS NULL OR $STATES_DUE_DATE <= ? AND $STATES_IS_ACTIVE = $STATES__CARD_ACTIVE)
        """.trimMargin(), arrayOf(deck.id.toString(), date.timespamp.toString()))

        val pendingStates = mutableMapOf<Long, MutableMap<ExerciseId, ExerciseState>>()
        cursor.use {
            while (cursor.moveToNext()) {
                val cardId = cursor.cardsId()
                val map = pendingStates[cardId] ?: mutableMapOf()
                if (cursor.statesHasSavedExerciseState()) {
                    val exerciseId = cursor.statesExerciseId()
                    val state = ExerciseState(cursor.statesDateDue(), cursor.statesInterval())
                    map[exerciseId] = state
                }
                pendingStates[cardId] = map
            }
        }

        return pendingStates.mapValues { LearningProgress(it.value) }
    }
    override fun pendingCards(deck: Deck, date: DateTime): List<Pair<Card, LearningProgress>> {
        val pendingWithProgress = pendingCardIds(deck, date, CardType.values())
        val pendingCardsIds = pendingWithProgress.keys.toList()
        return if (pendingCardsIds.isEmpty()) {
            emptyList()
        } else {
            val cards = cardsWithIds(pendingCardsIds, deck.domain)
            cards.map { card ->
                Pair(card, pendingWithProgress[card.id]!!)
            }
        }
    }

    override fun deckPendingCounts(deck: Deck, cardType: CardType, date: DateTime): Counts {
        val totalCount = countCardsOfDeck(deck, cardType)
        val deckDue = pendingCardIds(deck, date, arrayOf(cardType))
        return Counts(
                deckDue.count { it.value.globalStatus == Status.NEW },
                deckDue.count { it.value.globalStatus == Status.IN_PROGRESS
                        || it.value.globalStatus == Status.LEARNT },
                deckDue.count { it.value.globalStatus == Status.RELEARN },
                totalCount
        )
    }

    private fun countCardsOfDeck(deck: Deck, cardType: CardType): Int {
        return DatabaseUtils.queryNumEntries(
                helper.readableDatabase,
                CARDS,
                "$CARDS_DECK_ID = ${deck.id} AND $CARDS_TYPE = '${cardType.value}'"
        ).toInt()
    }

    override fun getStatesForCardsWithOriginals(originalIds: List<Long>): Map<Long, LearningProgress> {
        val db = helper.readableDatabase

        val cardIdsToFrontIds = cardIdsToFrontIds(db, originalIds)

        val cursor = db.rawQuery("""
            SELECT *
               FROM $STATES
               WHERE
                   $STATES_CARD_ID IN (${cardIdsToFrontIds.keys.joinToString()})
        """.trimMargin(), arrayOf())

        val map = mutableMapOf<Long, LearningProgress>()  // front id as key
        cursor.use {
            while (it.moveToNext()) {
                val cardId = it.statesCardId()
                val termId = cardIdsToFrontIds[cardId]!!
                // TODO: support multiple exercises
                val progress = if (it.statesHasSavedExerciseState()) {
                    LearningProgress(mapOf(
                            it.statesExerciseId() to it.exerciseState()
                    ))
                } else {
                    LearningProgress(emptyMap())
                }
                map[termId] = progress
            }
        }
        return cardIdsToFrontIds.values.associateWith { map[it] ?: LearningProgress(emptyMap()) }
    }

    private fun cardIdsToFrontIds(db: SQLiteDatabase, frontIds: List<Long>): Map<Long, Long> {
        val cursor = db.rawQuery("""
            SELECT $CARDS_ID, $CARDS_FRONT_ID
                FROM $CARDS
                WHERE $CARDS_FRONT_ID IN (${frontIds.joinToString()})
        """.trimIndent(), arrayOf())

        val map = mutableMapOf<Long, Long>()
        cursor.use {
            while (it.moveToNext()) {
                val cardId = it.cardsId()
                val frontId = it.cardsFrontId()
                map[cardId] = frontId
            }
        }
        return map
    }

    override fun invalidateCache() {
        // nothing to do here
    }

    private fun extractLearningProgress(cursor: Cursor): LearningProgress {
        val map = mutableMapOf<ExerciseId, ExerciseState>()
        while (cursor.moveToNext()) {
            val exerciseId = cursor.statesExerciseId()
            val state = cursor.exerciseState()
            map[exerciseId] = state
        }
        return LearningProgress(map)
    }
}