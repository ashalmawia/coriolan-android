package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import com.ashalmawia.coriolan.data.storage.DataProcessingException
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.ExerciseDescriptor
import com.ashalmawia.coriolan.learning.scheduler.*
import com.ashalmawia.coriolan.learning.scheduler.sr.SRState
import com.ashalmawia.coriolan.learning.scheduler.sr.emptyState
import com.ashalmawia.coriolan.model.*
import com.ashalmawia.coriolan.util.timespamp
import com.ashalmawia.errors.Errors
import org.joda.time.DateTime

private val TAG = SqliteStorage::class.java.simpleName

class SqliteStorage(private val context: Context, exercises: List<ExerciseDescriptor<*, *>>) : Repository {

    private val helper = SqliteRepositoryOpenHelper(context, exercises)

    override fun addLanguage(value: String): Language {
        val db = helper.writableDatabase
        val cv = createLanguageContentValues(value)

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

        cursor.use { it ->
            return if (it.moveToNext()) {
                Language(it.getId(), it.getLangValue())
            } else {
                null
            }
        }
    }

    override fun addExpression(value: String, type: ExpressionType, language: Language): Expression {
        try {
            val id = helper.writableDatabase.insert(SQLITE_TABLE_EXPRESSIONS,
                    null,
                    createExpressionContentValues(value, type, language))

            if (id < 0) {
                throw DataProcessingException("failed to add expression [$value] of type [$type], lang $language: maybe missing lang")
            }

            return Expression(id, value, type, language)
        } catch (e: SQLiteConstraintException) {
            throw DataProcessingException("failed to add expression [$value] of type [$type], lang $language: constraint violation, e")
        }
    }

    override fun expressionById(id: Long): Expression? {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |FROM $SQLITE_TABLE_EXPRESSIONS AS E
            |   LEFT JOIN $SQLITE_TABLE_LANGUAGES AS L
            |       ON E.$SQLITE_COLUMN_LANGUAGE_ID = L.$SQLITE_COLUMN_ID
            |
            |WHERE E.$SQLITE_COLUMN_ID = ?
            |""".trimMargin(),
                arrayOf(id.toString()))

        if (cursor.count == 0) return null
        if (cursor.count > 1) throw IllegalStateException("more that one expression for id $id")

        cursor.moveToFirst()
        val expression = Expression(id, cursor.getValue(), cursor.getExpressionType(), cursor.getLanguage())
        cursor.close()

        return expression
    }

    override fun expressionByValues(value: String, type: ExpressionType, language: Language): Expression? {
        val db = helper.readableDatabase

        val EXPRESSIONS = "E"
        val LANGUAGES = "L"

        val cursor = db.rawQuery("""
            |SELECT
            |   ${allColumnsExpressions(EXPRESSIONS)},
            |   ${allColumnsLanguages(LANGUAGES)}
            |
            |FROM $SQLITE_TABLE_EXPRESSIONS AS $EXPRESSIONS
            |   LEFT JOIN $SQLITE_TABLE_LANGUAGES AS $LANGUAGES
            |       ON ${SQLITE_COLUMN_LANGUAGE_ID.from(EXPRESSIONS)} = ${SQLITE_COLUMN_ID.from(LANGUAGES)}
            |
            |WHERE ${SQLITE_COLUMN_VALUE.from(EXPRESSIONS)} = ?
            |   AND ${SQLITE_COLUMN_TYPE.from(EXPRESSIONS)} = ?
            |   AND ${SQLITE_COLUMN_LANGUAGE_ID.from(EXPRESSIONS)} = ?
            |
        """.trimMargin(), arrayOf(value, type.value.toString(), language.id.toString()))

        cursor.use { it ->
            if (it.count == 0) {
                return null
            }

            if (it.count > 1) {
                Errors.illegalState(TAG, "found ${it.count} values for the value[$value], " +
                        "type[$type], language[$language]")
                return null
            }

            it.moveToFirst()
            return Expression(
                    it.getId(EXPRESSIONS),
                    it.getValue(EXPRESSIONS),
                    it.getExpressionType(EXPRESSIONS),
                    it.getLanguage(LANGUAGES)
            )
        }
    }

    override fun isUsed(expression: Expression): Boolean {
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
            |   $REVERSE.$SQLITE_COLUMN_EXPRESSION_ID = ?
        """.trimMargin(), arrayOf(expression.id.toString(), expression.id.toString()))

        cursor.use {
            it.moveToFirst()
            val count = it.getInt(0)
            return count > 0
        }
    }

    override fun deleteExpression(expression: Expression) {
        val db = helper.writableDatabase
        try {
            val result = db.delete(SQLITE_TABLE_EXPRESSIONS, "$SQLITE_COLUMN_ID = ?", arrayOf(expression.id.toString()))
            if (result == 0) {
                throw DataProcessingException("failed to delete expression $expression: not in the database")
            }
        } catch (e: SQLiteConstraintException) {
            throw DataProcessingException("failed to delete expression $expression: constraint violation")
        }
    }

    override fun createDomain(name: String, langOriginal: Language, langTranslations: Language): Domain {
        val db = helper.writableDatabase
        val cv = createDomainContentValues(name, langOriginal, langTranslations)

        try {
            val id = db.insertOrThrow(SQLITE_TABLE_DOMAINS, null, cv)
            return Domain(id, name, langOriginal, langTranslations)
        } catch (e: SQLiteConstraintException) {
            throw DataProcessingException("failed to create domain $langOriginal -> $langTranslations, constraint violation", e)
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

    override fun addCard(domain: Domain, deckId: Long, original: Expression, translations: List<Expression>): Card {
        if (translations.isEmpty()) {
            throw DataProcessingException("failed to add card with original[$original]: translations were empty")
        }

        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            val cardId = db.insert(
                    SQLITE_TABLE_CARDS,
                    null,
                    createCardContentValues(domain.id, deckId, original))

            if (cardId < 0) {
                throw DataProcessingException("failed to insert card ($original -> $translations)")
            }

            // write the card-to-expression relation (many-to-many)
            val cardsReverseCV = generateCardsReverseContentValues(cardId, translations)
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

        val cursor = db.rawQuery("""
            |SELECT *
            |FROM $SQLITE_TABLE_CARDS
            |WHERE $SQLITE_COLUMN_ID = ?
        """.trimMargin(), arrayOf(id.toString()))

        cursor.use {
            return if (cursor.moveToNext()) {
                Card(
                        id,
                        cursor.getDeckId(),
                        domain,
                        storage().expressionById(cursor.getFrontId())!!,
                        translationsByCardId(id)
                )
            } else {
                null
            }
        }
    }

    override fun updateCard(card: Card, deckId: Long, original: Expression, translations: List<Expression>): Card? {
        if (translations.isEmpty()) {
            throw DataProcessingException("failed to update card with id[$card.id]: translations were empty")
        }

        val db = helper.writableDatabase
        db.beginTransaction()

        try {
            val cv = createCardContentValues(card.domain.id, deckId, original, card.id)
            val updated = db.update(SQLITE_TABLE_CARDS, cv, "$SQLITE_COLUMN_ID = ?", arrayOf(card.id.toString()))

            if (updated == 0) {
                throw DataProcessingException("failed to update card ${card.id}")
            }

            // delete all the old translations from the card
            card.translations.forEach {
                db.delete(
                        SQLITE_TABLE_CARDS_REVERSE,
                        "$SQLITE_COLUMN_CARD_ID = ? AND $SQLITE_COLUMN_EXPRESSION_ID = ?",
                        arrayOf(card.id.toString(), it.id.toString())
                )
            }

            // add new translations to the card
            val reverseCV = generateCardsReverseContentValues(card.id, translations)
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

        val cursor = db.rawQuery("""
            |SELECT *
            |FROM
            |   $SQLITE_TABLE_CARDS AS Cards
            |WHERE
            |   Cards.$SQLITE_COLUMN_DOMAIN_ID = ?
        """.trimMargin(), arrayOf(domain.id.toString()))

        val cards = mutableListOf<Card>()
        while (cursor.moveToNext()) {
            cards.add(Card(
                    cursor.getId(),
                    cursor.getDeckId(),
                    domain,
                    expressionById(cursor.getFrontId())!!,
                    translationsByCardId(cursor.getId())
            ))
        }
        cursor.close()

        return cards
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
        val cv = createDeckContentValues(domain.id, name)

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

    override fun cardsOfDeck(deck: Deck): List<Card> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT * FROM $SQLITE_TABLE_CARDS
            |WHERE $SQLITE_COLUMN_DECK_ID = ?
            |""".trimMargin(), arrayOf(deck.id.toString()))

        val list = mutableListOf<Card>()
        while (cursor.moveToNext()) {
            val cardId = cursor.getId()
            list.add(Card(
                    cardId,
                    deck.id,
                    deck.domain,
                    storage().expressionById(cursor.getFrontId())!!,
                    translationsByCardId(cardId)
            ))
        }

        cursor.close()
        return list
    }

    private fun translationsByCardId(id: Long): List<Expression> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
                |SELECT * FROM $SQLITE_TABLE_CARDS_REVERSE
                |WHERE $SQLITE_COLUMN_CARD_ID = ?
            """.trimMargin(),
                arrayOf(id.toString()))

        // TODO: this is disastrously inoptimal, but who cares? https://trello.com/c/fkgQn5KD
        val translations = mutableListOf<Expression>()
        while (cursor.moveToNext()) {
            translations.add(storage().expressionById(cursor.getExpressionId())!!)
        }

        cursor.close()
        return translations
    }

    override fun getSRCardState(card: Card, exerciseId: String): SRState {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM ${sqliteTableExerciseState(exerciseId)}
            |   WHERE $SQLITE_COLUMN_CARD_ID = ?
        """.trimMargin(), arrayOf(card.id.toString()))

        cursor.use {
            return if (cursor.moveToNext()) {
                extractSRState(cursor)
            } else {
                emptyState()
            }
        }
    }

    override fun updateSRCardState(card: Card, state: SRState, exerciseId: String) {
        val table = sqliteTableExerciseState(exerciseId)
        val cv = createSRStateContentValues(card.id, state)
        try {
            val result = helper.writableDatabase.
                    insertWithOnConflict(table, null, cv, SQLiteDatabase.CONFLICT_REPLACE)

            if (result < 0) {
                throw DataProcessingException("failed to updated card state for card ${card.id}, " +
                        "exercise $exerciseId: error occured")
            }
        } catch (e: Exception) {
            throw DataProcessingException("failed to updated card state for card $card.id, " +
                    "exercise $exerciseId: constraint violation", e)
        }
    }

    override fun cardsDueDate(exerciseId: String, deck: Deck, date: DateTime): List<CardWithState<SRState>> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |FROM
            |   $SQLITE_TABLE_CARDS AS Cards
            |   LEFT JOIN ${sqliteTableExerciseState(exerciseId)} AS States
            |       ON Cards.$SQLITE_COLUMN_ID = States.$SQLITE_COLUMN_CARD_ID
            |WHERE
            |   Cards.$SQLITE_COLUMN_DECK_ID = ?
            |   AND
            |   (States.$SQLITE_COLUMN_DUE IS NULL OR States.$SQLITE_COLUMN_DUE <= ?)
        """.trimMargin(),
                arrayOf(deck.id.toString(), date.timespamp.toString()))

        val cards = mutableListOf<CardWithState<SRState>>()
        while (cursor.moveToNext()) {
            val card = Card(
                    cursor.getId(),
                    deck.id,
                    deck.domain,
                    expressionById(cursor.getFrontId())!!,
                    translationsByCardId(cursor.getId())
            )
            val state = extractSRState(cursor)
            cards.add(CardWithState(card, state))
        }
        cursor.close()

        return cards
    }

    private fun extractSRState(cursor: Cursor) =
            if (cursor.hasSavedState()) SRState(cursor.getDateDue(), cursor.getPeriod()) else emptyState()

    fun storage() = Repository.get(context)
}