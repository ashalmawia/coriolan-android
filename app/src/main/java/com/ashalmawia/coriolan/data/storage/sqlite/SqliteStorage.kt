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
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS_TYPE
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.card
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.cardsCardType
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.cardsDeckId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.cardsFrontId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.cardsId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.createCardContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.DECKS
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.DECKS_DOMAIN_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.DECKS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.createDeckContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.deck
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
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.STATES_CARD_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.STATES_DUE_DATE
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.allColumnsStates
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.createAllLearningProgressContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.exerciseState
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.statesCardId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.statesDateDue
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.statesExerciseId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.statesHasSavedExerciseState
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.statesPeriod
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.TERMS
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.TERMS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.TERMS_LANGUAGE_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.TERMS_VALUE
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.createTermContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.term
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTranslations.CARDS_REVERSE
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTranslations.CARDS_REVERSE_CARD_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTranslations.CARDS_REVERSE_TERM_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTranslations.generateCardsReverseContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTranslations.reverseCardId
import com.ashalmawia.coriolan.data.storage.sqlite.contract.SqliteUtils.from
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.exercise.sr.ExerciseState
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.Extras
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

    override fun addTerm(value: String, language: Language, extras: Extras?): Term {
        try {
            val id = helper.writableDatabase.insert(TERMS,
                    null,
                    createTermContentValues(value, language, extras))

            if (id < 0) {
                throw DataProcessingException("failed to add term [$value], lang $language: maybe missing lang")
            }

            return Term(id, value, language, extras ?: Extras.empty())
        } catch (e: SQLiteConstraintException) {
            throw DataProcessingException("failed to add term [$value], lang $language: constraint violation", e)
        }
    }

    override fun updateTerm(term: Term, extras: Extras?): Term {
        val cv = createTermContentValues(
                term.value, term.language.id, extras, term.id
        )

        val db = helper.writableDatabase
        db.update(TERMS, cv, "$TERMS_ID == ?", arrayOf(term.id.toString()))

        return term.copy(extras = extras ?: Extras.empty())
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
               LEFT JOIN $CARDS_REVERSE
                  ON $CARDS_ID = $CARDS_REVERSE_CARD_ID
            
            WHERE
               $CARDS_FRONT_ID = ?
               OR
               $CARDS_REVERSE_TERM_ID = ?
        """.trimMargin(), arrayOf(term.id.toString(), term.id.toString()))

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
        if (translations.isEmpty()) {
            throw DataProcessingException("failed to add card with original[$original]: translations were empty")
        }

        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            val type = if (domain.langOriginal().id == original.language.id) CardType.FORWARD else CardType.REVERSE
            val cardId = db.insert(
                    CARDS,
                    null,
                    createCardContentValues(domain.id, deckId, original, type))

            if (cardId < 0) {
                throw DataProcessingException("failed to insert card ($original -> $translations)")
            }

            // write the card-to-term relation (many-to-many)
            val cardsReverseCV = generateCardsReverseContentValues(cardId, translations)
            cardsReverseCV.forEach {
                val result = db.insert(CARDS_REVERSE, null, it)

                if (result < 0) {
                    throw DataProcessingException("failed to insert translation entry card ($original -> $translations)")
                }
            }

            val card = Card(cardId, deckId, domain, type, original, translations)

            db.setTransactionSuccessful()

            return card
        } catch (e: SQLiteConstraintException) {
            throw DataProcessingException("failed to add card ($original -> $translations), constraint violation", e)
        } finally {
            db.endTransaction()
        }
    }

    override fun cardById(id: Long, domain: Domain): Card? {
        val list = cardsWtihIds(listOf(id), domain)
        return list.firstOrNull()
    }

    private fun cardsWtihIds(ids: List<Long>, domain: Domain): List<Card> {
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

        val translations = if (ids.size > 1) {
             allCardsReverse(db)
        } else {
            val id = ids.first()
            mapOf(id to translationsByCardId(id))
        }

        val cards = mutableListOf<Card>()
        cursor.use {
            while (cursor.moveToNext()) {
                val card = cursor.card(domain, translations)
                cards.add(card)
            }
        }
        return cards
    }

    override fun cardByValues(domain: Domain, original: Term): Card? {
        val db = helper.readableDatabase

        val reverse = allCardsReverse(db)

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

        cursor.use {
            // go over these cards
            while (cursor.moveToNext()) {
                // we found the card we need
                // we can assume that there are no other cards like this due to merging
                return cursor.card(domain, reverse)
            }

            return null
        }
    }

    override fun updateCard(card: Card, deckId: Long, original: Term, translations: List<Term>): Card {
        if (translations.isEmpty()) {
            throw DataProcessingException("failed to update card with id[$card.id]: translations were empty")
        }

        val db = helper.writableDatabase
        db.beginTransaction()

        try {
            val cv = createCardContentValues(card.domain.id, deckId, original, card.type, card.id)
            val updated = db.update(CARDS, cv, "$CARDS_ID = ?", arrayOf(card.id.toString()))

            if (updated == 0) {
                throw DataProcessingException("failed to update card ${card.id}")
            }

            // delete all the old translations from the card
            card.translations.forEach {
                db.delete(
                        CARDS_REVERSE,
                        "$CARDS_REVERSE_CARD_ID = ? AND $CARDS_REVERSE_TERM_ID = ?",
                        arrayOf(card.id.toString(), it.id.toString())
                )
            }

            // add new translations to the card
            val reverseCV = generateCardsReverseContentValues(card.id, translations)
            reverseCV.forEach {
                val result = db.insertOrUpdate(CARDS_REVERSE, it)
                if (result < 0) {
                    throw DataProcessingException("failed to update card ${card.id}: translation not in the database")
                }
            }

            db.setTransactionSuccessful()

            return Card(card.id, deckId, card.domain, card.type, original, translations)
        } catch (e: SQLiteConstraintException) {
            throw DataProcessingException("failed to update card[$card.id], constraint violation", e)
        } finally {
            db.endTransaction()
        }
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

        val reverse = allCardsReverse(db)

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

        val cards = mutableListOf<Card>()

        cursor.use {
            while (it.moveToNext()) {
                val cardId = it.cardsId()
                cards.add(Card(
                        cardId,
                        it.cardsDeckId(),
                        domain,
                        it.cardsCardType(),
                        it.term(),
                        reverse[cardId]!!
                ))
            }
            return cards
        }
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

    override fun deckById(id: Long, domain: Domain): Deck? {
        val db = helper.readableDatabase

        val cursor = db.rawQuery(
                "SELECT * FROM $DECKS WHERE $DECKS_ID = ?",
                arrayOf(id.toString()))

        cursor.use {
            if (it.count == 0) return null
            if (it.count > 1) throw IllegalStateException("more that one value for deck id $id")

            it.moveToFirst()
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

        val reverse = allCardsReverse(db)

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

        val list = mutableListOf<Card>()
        cursor.use {
            while (it.moveToNext()) {
                list.add(it.card(deck.domain, reverse))
            }
            return list
        }
    }

    private fun translationsByCardId(id: Long): List<Term> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            SELECT *
            
            FROM $CARDS_REVERSE
            
               LEFT JOIN $TERMS
                   ON $CARDS_REVERSE_TERM_ID = $TERMS_ID
            
               LEFT JOIN $LANGUAGES
                   ON $TERMS_LANGUAGE_ID = $LANGUAGES_ID
            
            WHERE $CARDS_REVERSE_CARD_ID = ?
                
            """.trimMargin(), arrayOf(id.toString()))

        val translations = mutableListOf<Term>()
        cursor.use {
            while (it.moveToNext()) {
                translations.add(it.term())
            }
            return translations
        }
    }

    private fun allCardsReverse(db: SQLiteDatabase): Map<Long, List<Term>> {
        val cursor = db.rawQuery("""
            SELECT *
            
            FROM 
                $CARDS_REVERSE
                
                LEFT JOIN $TERMS
                    ON $CARDS_REVERSE_TERM_ID = $TERMS_ID
                
                LEFT JOIN $LANGUAGES
                    ON $TERMS_LANGUAGE_ID = $LANGUAGES_ID
            """.trimMargin(), null)

        val reverse = mutableMapOf<Long, MutableList<Term>>()
        cursor.use {
            while (it.moveToNext()) {
                reverse
                        .getOrPut(it.reverseCardId()) { mutableListOf() }
                        .add(it.term())
            }
            return reverse
        }
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
               ${onlyPending()}
        """.trimMargin(), arrayOf(deck.id.toString(), date.timespamp.toString()))

        val pendingStates = mutableMapOf<Long, MutableMap<ExerciseId, ExerciseState>>()
        cursor.use {
            while (cursor.moveToNext()) {
                val cardId = cursor.cardsId()
                val map = pendingStates[cardId] ?: mutableMapOf()
                if (cursor.statesHasSavedExerciseState()) {
                    val exerciseId = cursor.statesExerciseId()
                    val state = ExerciseState(cursor.statesDateDue(), cursor.statesPeriod())
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
            val cards = cardsWtihIds(pendingCardsIds, deck.domain)
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

    private fun onlyPending() = "($STATES_DUE_DATE IS NULL OR $STATES_DUE_DATE <= ?)"

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