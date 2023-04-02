package com.ashalmawia.coriolan.data.storage.sqlite

import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import com.ashalmawia.coriolan.data.storage.DataProcessingException
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.exercise.sr.ExerciseState
import com.ashalmawia.coriolan.model.*
import com.ashalmawia.coriolan.util.timespamp
import com.ashalmawia.errors.Errors
import org.joda.time.DateTime

private val TAG = SqliteStorage::class.java.simpleName

class SqliteStorage(private val helper: SqliteRepositoryOpenHelper) : Repository {

    override fun addLanguage(value: String): Language {
        val db = helper.writableDatabase
        val cv = CreateContentValues.createLanguageContentValues(value)

        try {
            val id = db.insertOrThrow(SQLITE_TABLE_LANGUAGES, null, cv)
            return Language(id, value)
        } catch (e: SQLiteConstraintException) {
            throw DataProcessingException("failed to add langauge [$value], constraint violation", e)
        }
    }

    override fun languageById(id: Long): Language? {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT * FROM $SQLITE_TABLE_LANGUAGES
            |WHERE $SQLITE_COLUMN_ID = ?
        """.trimMargin(), arrayOf(id.toString()))

        cursor.use {
            return if (it.moveToNext()) {
                Language(it.getId(), it.getLangValue())
            } else {
                null
            }
        }
    }

    override fun languageByName(name: String): Language? {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $SQLITE_TABLE_LANGUAGES
            |   WHERE $SQLITE_COLUMN_LANG_VALUE = ?
        """.trimMargin(), arrayOf(name))

        cursor.use {
            return if (it.moveToNext()) {
                Language(it.getId(), it.getLangValue())
            } else {
                null
            }
        }
    }

    override fun addTerm(value: String, language: Language, extras: Extras?): Term {
        try {
            val id = helper.writableDatabase.insert(SQLITE_TABLE_TERMS,
                    null,
                    CreateContentValues.createTermContentValues(value, language, extras))

            if (id < 0) {
                throw DataProcessingException("failed to add term [$value], lang $language: maybe missing lang")
            }

            return Term(id, value, language, extras ?: Extras.empty())
        } catch (e: SQLiteConstraintException) {
            throw DataProcessingException("failed to add term [$value], lang $language: constraint violation", e)
        }
    }

    override fun updateTerm(term: Term, extras: Extras?): Term {
        val cv = CreateContentValues.createTermContentValues(
                term.value, term.language.id, extras, term.id
        )

        val db = helper.writableDatabase
        db.update(SQLITE_TABLE_TERMS, cv, "$SQLITE_COLUMN_ID == ?", arrayOf(term.id.toString()))

        return term.copy(extras = extras ?: Extras.empty())
    }

    override fun termById(id: Long): Term? {
        val db = helper.readableDatabase

        val TERMS = "Terms"
        val LANGUAGES = "Languages"

        val cursor = db.rawQuery("""
            |SELECT
            |   ${allColumnsTerms(TERMS)},
            |   ${allColumnsLanguages(LANGUAGES)}
            |
            |   FROM $SQLITE_TABLE_TERMS AS $TERMS
            |       LEFT JOIN $SQLITE_TABLE_LANGUAGES AS $LANGUAGES
            |           ON ${SQLITE_COLUMN_LANGUAGE_ID.from(TERMS)} = ${SQLITE_COLUMN_ID.from(LANGUAGES)}
            |
            |   WHERE ${SQLITE_COLUMN_ID.from(TERMS)} = ?
            |
            |""".trimMargin(), arrayOf(id.toString()))

        if (cursor.count == 0) return null
        if (cursor.count > 1) throw IllegalStateException("more that one term for id $id")

        cursor.use {
            it.moveToFirst()
            return it.getTerm(CreateContentValues, TERMS, LANGUAGES)
        }
    }

    override fun termByValues(value: String, language: Language): Term? {
        val db = helper.readableDatabase

        val TERMS = "Terms"
        val LANGUAGES = "Langs"

        val cursor = db.rawQuery("""
            |SELECT
            |   ${allColumnsTerms(TERMS)},
            |   ${allColumnsLanguages(LANGUAGES)}
            |
            |   FROM $SQLITE_TABLE_TERMS AS $TERMS
            |       LEFT JOIN $SQLITE_TABLE_LANGUAGES AS $LANGUAGES
            |           ON ${SQLITE_COLUMN_LANGUAGE_ID.from(TERMS)} = ${SQLITE_COLUMN_ID.from(LANGUAGES)}
            |
            |   WHERE ${SQLITE_COLUMN_VALUE.from(TERMS)} = ?
            |       AND ${SQLITE_COLUMN_LANGUAGE_ID.from(TERMS)} = ?
            |
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
            return it.getTerm(CreateContentValues, TERMS, LANGUAGES)
        }
    }

    override fun isUsed(term: Term): Boolean {
        val db = helper.readableDatabase

        val CARDS = "C"
        val REVERSE = "T"
        val COUNT = "count"

        val cursor = db.rawQuery("""
            |SELECT
            |   COUNT(*) AS $COUNT
            |
            |FROM $SQLITE_TABLE_CARDS AS $CARDS
            |   LEFT JOIN $SQLITE_TABLE_CARDS_REVERSE AS $REVERSE
            |      ON $CARDS.$SQLITE_COLUMN_ID = $REVERSE.$SQLITE_COLUMN_CARD_ID
            |
            |WHERE
            |   $CARDS.$SQLITE_COLUMN_FRONT_ID = ?
            |   OR
            |   $REVERSE.$SQLITE_COLUMN_TERM_ID = ?
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
            val result = db.delete(SQLITE_TABLE_TERMS, "$SQLITE_COLUMN_ID = ?", arrayOf(term.id.toString()))
            if (result == 0) {
                throw DataProcessingException("failed to delete term $term: not in the database")
            }
        } catch (e: SQLiteConstraintException) {
            throw DataProcessingException("failed to delete term $term: constraint violation")
        }
    }

    override fun createDomain(name: String?, langOriginal: Language, langTranslations: Language): Domain {
        val db = helper.writableDatabase
        val cv = CreateContentValues.createDomainContentValues(name, langOriginal, langTranslations)

        try {
            val id = db.insertOrThrow(SQLITE_TABLE_DOMAINS, null, cv)
            return Domain(id, name, langOriginal, langTranslations)
        } catch (e: SQLiteConstraintException) {
            throw DataProcessingException("failed to create domain $langOriginal -> $langTranslations, constraint violation", e)
        }
    }

    override fun domainById(id: Long): Domain {
        val db = helper.readableDatabase

        val DOMAINS = "D"
        val LANGS1 = "L1"
        val LANGS2 = "L2"

        val cursor = db.rawQuery("""
            |SELECT
            |   ${allColumnsDomains(DOMAINS)},
            |   ${allColumnsLanguages(LANGS1)},
            |   ${allColumnsLanguages(LANGS2)}
            |
            |   FROM $SQLITE_TABLE_DOMAINS AS $DOMAINS
            |      LEFT JOIN $SQLITE_TABLE_LANGUAGES AS $LANGS1
            |          ON ${SQLITE_COLUMN_LANG_ORIGINAL.from(DOMAINS)} = ${SQLITE_COLUMN_ID.from(LANGS1)}
            |      LEFT JOIN $SQLITE_TABLE_LANGUAGES AS $LANGS2
            |          ON ${SQLITE_COLUMN_LANG_TRANSLATIONS.from(DOMAINS)} = ${SQLITE_COLUMN_ID.from(LANGS2)}
            |
            |   WHERE
            |      ${SQLITE_COLUMN_ID.from(DOMAINS)} = ?
        """.trimMargin(), arrayOf(id.toString()))

        cursor.use {
            it.moveToNext()
            return Domain(
                    it.getId(DOMAINS),
                    it.getNameIfAny(DOMAINS),
                    it.getLanguage(LANGS1),
                    it.getLanguage(LANGS2)
            )
        }
    }

    override fun allDomains(): List<Domain> {
        val db = helper.readableDatabase

        val DOMAINS = "D"
        val LANGS1 = "L1"
        val LANGS2 = "L2"

        val cursor = db.rawQuery("""
            |SELECT
            |   ${allColumnsDomains(DOMAINS)},
            |   ${allColumnsLanguages(LANGS1)},
            |   ${allColumnsLanguages(LANGS2)}
            |
            |FROM $SQLITE_TABLE_DOMAINS AS $DOMAINS
            |   LEFT JOIN $SQLITE_TABLE_LANGUAGES AS $LANGS1
            |       ON ${SQLITE_COLUMN_LANG_ORIGINAL.from(DOMAINS)} = ${SQLITE_COLUMN_ID.from(LANGS1)}
            |   LEFT JOIN $SQLITE_TABLE_LANGUAGES AS $LANGS2
            |       ON ${SQLITE_COLUMN_LANG_TRANSLATIONS.from(DOMAINS)} = ${SQLITE_COLUMN_ID.from(LANGS2)}
        """.trimMargin(), arrayOf())

        val list = mutableListOf<Domain>()
        cursor.use {
            while (it.moveToNext()) {
                list.add(Domain(
                        it.getId(DOMAINS),
                        it.getName(DOMAINS),
                        it.getLanguage(LANGS1),
                        it.getLanguage(LANGS2)
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
            val cardId = db.insert(
                    SQLITE_TABLE_CARDS,
                    null,
                    CreateContentValues.createCardContentValues(domain.id, deckId, original))

            if (cardId < 0) {
                throw DataProcessingException("failed to insert card ($original -> $translations)")
            }

            // write the card-to-term relation (many-to-many)
            val cardsReverseCV = CreateContentValues.generateCardsReverseContentValues(cardId, translations)
            cardsReverseCV.forEach {
                val result = db.insert(SQLITE_TABLE_CARDS_REVERSE, null, it)

                if (result < 0) {
                    throw DataProcessingException("failed to insert translation entry card ($original -> $translations)")
                }
            }

            val card = Card(cardId, deckId, domain, original, translations)

            db.setTransactionSuccessful()

            return card
        } catch (e: SQLiteConstraintException) {
            throw DataProcessingException("failed to add card ($original -> $translations), constraint violation", e)
        } finally {
            db.endTransaction()
        }
    }

    override fun cardById(id: Long, domain: Domain): Card? {
        val db = helper.readableDatabase

        val CARDS = "Cards"
        val TERMS = "Terms"
        val LANGUAGES = "Languages"

        val cursor = db.rawQuery("""
            |SELECT
            |   ${allColumnsCards(CARDS)},
            |   ${allColumnsTerms(TERMS)},
            |   ${allColumnsLanguages(LANGUAGES)}
            |
            |   FROM $SQLITE_TABLE_CARDS AS $CARDS
            |
            |       LEFT JOIN $SQLITE_TABLE_TERMS AS $TERMS
            |           ON ${SQLITE_COLUMN_FRONT_ID.from(CARDS)} = ${SQLITE_COLUMN_ID.from(TERMS)}
            |
            |       LEFT JOIN $SQLITE_TABLE_LANGUAGES AS $LANGUAGES
            |           ON ${SQLITE_COLUMN_LANGUAGE_ID.from(TERMS)} = ${SQLITE_COLUMN_ID.from(LANGUAGES)}
            |
            |   WHERE ${SQLITE_COLUMN_ID.from(CARDS)} = ?
        """.trimMargin(), arrayOf(id.toString()))

        cursor.use {
            return if (cursor.moveToNext()) {
                Card(
                        id,
                        it.getDeckId(CARDS),
                        domain,
                        it.getTerm(CreateContentValues, TERMS, LANGUAGES),
                        translationsByCardId(id)
                )
            } else {
                null
            }
        }
    }

    override fun cardByValues(domain: Domain, original: Term): Card? {
        val db = helper.readableDatabase

        val reverse = allCardsReverse(db)

        val CARDS = "Cards"
        val TERMS = "Terms"
        val LANGUAGES = "Languages"

        // find all cards with the same original
        val cursor = db.rawQuery("""
            |SELECT
            |   ${allColumnsCards(CARDS)},
            |   ${allColumnsTerms(TERMS)},
            |   ${allColumnsLanguages(LANGUAGES)}
            |
            |   FROM $SQLITE_TABLE_CARDS AS $CARDS
            |
            |       LEFT JOIN $SQLITE_TABLE_TERMS AS $TERMS
            |           ON ${SQLITE_COLUMN_FRONT_ID.from(CARDS)} = ${SQLITE_COLUMN_ID.from(TERMS)}
            |
            |       LEFT JOIN $SQLITE_TABLE_LANGUAGES AS $LANGUAGES
            |           ON ${SQLITE_COLUMN_LANGUAGE_ID.from(TERMS)} = ${SQLITE_COLUMN_ID.from(LANGUAGES)}
            |
            |   WHERE
            |       ${SQLITE_COLUMN_DOMAIN_ID.from(CARDS)} = ?
            |           AND
            |       ${SQLITE_COLUMN_FRONT_ID.from(CARDS)} = ?
        """.trimMargin(), arrayOf(domain.id.toString(), original.id.toString()))

        cursor.use {
            // go over these cards
            while (cursor.moveToNext()) {
                val id = cursor.getId(CARDS)

                // we found the card we need
                // we can assume that there are no other cards like this due to merging
                return Card(id, cursor.getDeckId(CARDS), domain, original, reverse[id]!!)
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
            val cv = CreateContentValues.createCardContentValues(card.domain.id, deckId, original, card.id)
            val updated = db.update(SQLITE_TABLE_CARDS, cv, "$SQLITE_COLUMN_ID = ?", arrayOf(card.id.toString()))

            if (updated == 0) {
                throw DataProcessingException("failed to update card ${card.id}")
            }

            // delete all the old translations from the card
            card.translations.forEach {
                db.delete(
                        SQLITE_TABLE_CARDS_REVERSE,
                        "$SQLITE_COLUMN_CARD_ID = ? AND $SQLITE_COLUMN_TERM_ID = ?",
                        arrayOf(card.id.toString(), it.id.toString())
                )
            }

            // add new translations to the card
            val reverseCV = CreateContentValues.generateCardsReverseContentValues(card.id, translations)
            reverseCV.forEach {
                val result = db.insertOrUpdate(SQLITE_TABLE_CARDS_REVERSE, it)
                if (result < 0) {
                    throw DataProcessingException("failed to update card ${card.id}: translation not in the database")
                }
            }

            db.setTransactionSuccessful()

            return Card(card.id, deckId, card.domain, original, translations)
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
                    SQLITE_TABLE_CARDS,
                    "$SQLITE_COLUMN_ID = ?",
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
            |SELECT
            |   ${allColumnsCards(CARDS)},
            |   ${allColumnsTerms(TERMS)},
            |   ${allColumnsLanguages(LANGUAGES)}
            |
            |   FROM $SQLITE_TABLE_CARDS AS $CARDS
            |
            |       LEFT JOIN $SQLITE_TABLE_TERMS AS $TERMS
            |           ON ${SQLITE_COLUMN_FRONT_ID.from(CARDS)} = ${SQLITE_COLUMN_ID.from(TERMS)}
            |
            |       LEFT JOIN $SQLITE_TABLE_LANGUAGES AS $LANGUAGES
            |           ON ${SQLITE_COLUMN_LANGUAGE_ID.from(TERMS)} = ${SQLITE_COLUMN_ID.from(LANGUAGES)}
            |
            |   WHERE
            |       ${SQLITE_COLUMN_DOMAIN_ID.from(CARDS)} = ?
        """.trimMargin(), arrayOf(domain.id.toString()))

        val cards = mutableListOf<Card>()

        cursor.use {
            while (it.moveToNext()) {
                val cardId = it.getId(CARDS)
                cards.add(Card(
                        cardId,
                        it.getDeckId(CARDS),
                        domain,
                        it.getTerm(CreateContentValues, TERMS, LANGUAGES),
                        reverse[cardId]!!
                ))
            }
            return cards
        }
    }

    override fun allDecks(domain: Domain): List<Deck> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $SQLITE_TABLE_DECKS
            |   WHERE $SQLITE_COLUMN_DOMAIN_ID = ?
        """.trimMargin(), arrayOf(domain.id.toString()))

        val list = mutableListOf<Deck>()
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getId()
                list.add(Deck(id, domain, it.getName()))
            }
        }

        return list
    }

    override fun deckById(id: Long, domain: Domain): Deck? {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $SQLITE_TABLE_DECKS WHERE $SQLITE_COLUMN_ID = ?",
                arrayOf(id.toString()))

        cursor.use { it ->
            if (it.count == 0) return null
            if (it.count > 1) throw IllegalStateException("more that one value for deck id $id")

            it.moveToFirst()
            return Deck(id, domain, it.getName())
        }
    }

    override fun addDeck(domain: Domain, name: String): Deck {
        val db = helper.writableDatabase
        val cv = CreateContentValues.createDeckContentValues(domain.id, name)

        try {
            val id = db.insert(SQLITE_TABLE_DECKS, null, cv)
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
        val cv = CreateContentValues.createDeckContentValues(deck.domain.id, name)

        try {
            val updated = db.update(SQLITE_TABLE_DECKS, cv, "$SQLITE_COLUMN_ID = ?", arrayOf(deck.id.toString()))

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
            val deleted = db.delete(SQLITE_TABLE_DECKS, "$SQLITE_COLUMN_ID = ?", arrayOf(deck.id.toString()))
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

        val CARDS = "Cards"
        val TERMS = "Terms"
        val LANGUAGES = "Languages"

        val cursor = db.rawQuery("""
            |SELECT
            |   ${allColumnsCards(CARDS)},
            |   ${allColumnsTerms(TERMS)},
            |   ${allColumnsLanguages(LANGUAGES)}
            |
            |   FROM $SQLITE_TABLE_CARDS AS $CARDS
            |
            |       LEFT JOIN $SQLITE_TABLE_TERMS AS $TERMS
            |           ON ${SQLITE_COLUMN_FRONT_ID.from(CARDS)} = ${SQLITE_COLUMN_ID.from(TERMS)}
            |
            |       LEFT JOIN $SQLITE_TABLE_LANGUAGES AS $LANGUAGES
            |           ON ${SQLITE_COLUMN_LANGUAGE_ID.from(TERMS)} = ${SQLITE_COLUMN_ID.from(LANGUAGES)}
            |
            |   WHERE $SQLITE_COLUMN_DECK_ID = ?
            |
            |""".trimMargin(), arrayOf(deck.id.toString()))

        val list = mutableListOf<Card>()
        cursor.use {
            while (it.moveToNext()) {
                val cardId = it.getId(CARDS)
                list.add(Card(
                        cardId,
                        deck.id,
                        deck.domain,
                        it.getTerm(CreateContentValues, TERMS, LANGUAGES),
                        reverse.getValue(cardId)
                ))
            }

            return list
        }
    }

    private fun translationsByCardId(id: Long): List<Term> {
        val db = helper.readableDatabase

        val REVERSE = "Reverse"
        val TERMS = "Terms"
        val LANGUAGES = "Languages"

        val cursor = db.rawQuery("""
                |SELECT
                |   ${allColumnsReverse(REVERSE)},
                |   ${allColumnsTerms(TERMS)},
                |   ${allColumnsLanguages(LANGUAGES)}
                |
                |   FROM $SQLITE_TABLE_CARDS_REVERSE AS $REVERSE
                |
                |       LEFT JOIN $SQLITE_TABLE_TERMS AS $TERMS
                |           ON ${SQLITE_COLUMN_TERM_ID.from(REVERSE)} = ${SQLITE_COLUMN_ID.from(TERMS)}
                |
                |       LEFT JOIN $SQLITE_TABLE_LANGUAGES AS $LANGUAGES
                |           ON ${SQLITE_COLUMN_LANGUAGE_ID.from(TERMS)} = ${SQLITE_COLUMN_ID.from(LANGUAGES)}
                |
                |   WHERE ${SQLITE_COLUMN_CARD_ID.from(REVERSE)} = ?
                |
            """.trimMargin(), arrayOf(id.toString()))

        val translations = mutableListOf<Term>()
        cursor.use {
            while (it.moveToNext()) {
                translations.add(it.getTerm(CreateContentValues, TERMS, LANGUAGES))
            }
            return translations
        }
    }

    private fun allCardsReverse(db: SQLiteDatabase): Map<Long, List<Term>> {
        val REVERSE = "Reverse"
        val TERMS = "Terms"
        val LANGUAGES = "Languages"

        val cursor = db.rawQuery("""
                |SELECT
                |   ${allColumnsReverse(REVERSE)},
                |   ${allColumnsTerms(TERMS)},
                |   ${allColumnsLanguages(LANGUAGES)}
                |
                |   FROM $SQLITE_TABLE_CARDS_REVERSE AS $REVERSE
                |       LEFT JOIN $SQLITE_TABLE_TERMS AS $TERMS
                |       ON ${SQLITE_COLUMN_TERM_ID.from(REVERSE)} = ${SQLITE_COLUMN_ID.from(TERMS)}
                |
                |       LEFT JOIN $SQLITE_TABLE_LANGUAGES AS $LANGUAGES
                |       ON ${SQLITE_COLUMN_LANGUAGE_ID.from(TERMS)} = ${SQLITE_COLUMN_ID.from(LANGUAGES)}
            """.trimMargin(), null)

        val reverse = mutableMapOf<Long, MutableList<Term>>()
        cursor.use {
            while (it.moveToNext()) {
                reverse
                        .getOrPut(it.getCardId(REVERSE), { mutableListOf() })
                        .add(it.getTerm(CreateContentValues, TERMS, LANGUAGES))
            }
            return reverse
        }
    }

    override fun getCardLearningProgress(card: Card): LearningProgress {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $SQLITE_TABLE_CARD_STATES
            |   WHERE $SQLITE_COLUMN_CARD_ID = ?
        """.trimMargin(), arrayOf(card.id.toString()))

        cursor.use {
            return if (cursor.moveToNext()) {
                extractLearningProgress(cursor)
            } else {
                LearningProgress(emptyMap())
            }
        }
    }

    override fun updateCardLearningProgress(card: Card, learningProgress: LearningProgress) {
        val cvList = CreateContentValues.createAllLearningProgressContentValues(card.id, learningProgress)

        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            cvList.forEach {
                val result = db.insertWithOnConflict(
                        SQLITE_TABLE_CARD_STATES, null, it, SQLiteDatabase.CONFLICT_REPLACE)
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

    override fun pendingCards(deck: Deck, date: DateTime): List<Pair<Card, LearningProgress>> {
        val db = helper.readableDatabase

        val reverse = allCardsReverse(db)

        val CARDS = "Cards"
        val STATES = "States"
        val TERMS = "Terms"
        val LANGUAGES = "Languages"

        val cursor = db.rawQuery("""
            |SELECT
            |   ${allColumnsCards(CARDS)},
            |   ${allColumnsSRStates(STATES)},
            |   ${allColumnsTerms(TERMS)},
            |   ${allColumnsLanguages(LANGUAGES)}
            |
            |   FROM $SQLITE_TABLE_CARDS AS $CARDS
            |
            |       LEFT JOIN $SQLITE_TABLE_TERMS AS $TERMS
            |           ON ${SQLITE_COLUMN_FRONT_ID.from(CARDS)} = ${SQLITE_COLUMN_ID.from(TERMS)}
            |
            |       LEFT JOIN $SQLITE_TABLE_LANGUAGES AS $LANGUAGES
            |           ON ${SQLITE_COLUMN_LANGUAGE_ID.from(TERMS)} = ${SQLITE_COLUMN_ID.from(LANGUAGES)}
            |
            |       LEFT JOIN $SQLITE_TABLE_CARD_STATES AS $STATES
            |           ON ${SQLITE_COLUMN_ID.from(CARDS)} = ${SQLITE_COLUMN_CARD_ID.from(STATES)}
            |
            |   WHERE
            |       ${SQLITE_COLUMN_DECK_ID.from(CARDS)} = ?
            |           AND
            |       ${onlyPending(STATES)}
        """.trimMargin(),
                arrayOf(deck.id.toString(), date.timespamp.toString()))

        cursor.use {
            val tasks = mutableListOf<Pair<Card, LearningProgress>>()
            while (cursor.moveToNext()) {
                val cardId = cursor.getId(CARDS)
                val card = Card(
                        cardId,
                        deck.id,
                        deck.domain,
                        it.getTerm(CreateContentValues, TERMS, LANGUAGES),
                        reverse.getValue(cardId)
                )
                val state = extractLearningProgress(cursor, STATES)
                tasks.add(Pair(card, state))
            }
            return tasks
        }
    }

    override fun getStatesForCardsWithOriginals(originalIds: List<Long>): Map<Long, LearningProgress> {
        val db = helper.readableDatabase

        val CARDS = "Cards"
        val STATES = "States"
        val TERMS = "Terms"

        val cursor = db.rawQuery("""
            |SELECT
            |   ${allColumnsCards(CARDS)},
            |   ${allColumnsSRStates(STATES)},
            |   ${allColumnsTerms(TERMS)}
            |
            |   FROM $SQLITE_TABLE_CARDS AS $CARDS
            |
            |       LEFT JOIN $SQLITE_TABLE_TERMS AS $TERMS
            |           ON ${SQLITE_COLUMN_FRONT_ID.from(CARDS)} = ${SQLITE_COLUMN_ID.from(TERMS)}
            |
            |       LEFT JOIN $SQLITE_TABLE_CARD_STATES AS $STATES
            |           ON ${SQLITE_COLUMN_ID.from(CARDS)} = ${SQLITE_COLUMN_CARD_ID.from(STATES)}
            |
            |   WHERE
            |       ${SQLITE_COLUMN_ID.from(TERMS)} IN (${originalIds.joinToString()})
        """.trimMargin(), arrayOf())

        val map = mutableMapOf<Long, LearningProgress>()
        cursor.use {
            while (cursor.moveToNext()) {
                val termId = cursor.getId(TERMS)
                val state = extractLearningProgress(cursor, STATES)
                map[termId] = state
            }
            return map
        }
    }

    private fun onlyPending(statesTableAlias: String) =
            "(${SQLITE_COLUMN_DUE_DATE.from(statesTableAlias)} IS NULL OR ${SQLITE_COLUMN_DUE_DATE.from(statesTableAlias)} <= ?)"


    override fun invalidateCache() {
        // nothing to do here
    }

    private fun extractLearningProgress(cursor: Cursor, alias: String? = null): LearningProgress {
        if (cursor.hasSavedSRState(alias)) {
            val state = ExerciseState(cursor.getDateDue(alias), cursor.getPeriod(alias))
            // TODO: decouple
            return LearningProgress(mapOf(ExerciseId.FLASHCARDS to state))
        } else {
            return LearningProgress(emptyMap())
        }
    }
}