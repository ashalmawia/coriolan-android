package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.assignment.Counts
import com.ashalmawia.coriolan.learning.assignment.PendingCounter
import com.ashalmawia.coriolan.learning.scheduler.*
import com.ashalmawia.coriolan.model.*
import com.ashalmawia.coriolan.util.timespamp
import com.ashalmawia.errors.Errors
import org.joda.time.DateTime

private val TAG = SqliteStorage::class.java.simpleName

class SqliteStorage(private val context: Context, exercises: List<Exercise>) : Repository {

    private val helper = MySqliteOpenHelper(context, exercises)

    override fun addLanguage(value: String): Language {
        val db = helper.writableDatabase

        val cv = createLanguageContentValues(value)
        val id = db.insertOrThrow(SQLITE_TABLE_LANGUAGES, null, cv)

        return Language(id, value)
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
        val id = helper.writableDatabase.insert(SQLITE_TABLE_EXPRESSIONS,
                null,
                createExpressionContentValues(value, type, language))
        return Expression(id, value, type, language)
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

    override fun addCard(deckId: Long, original: Expression, translations: List<Expression>): Card {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            val cardId = db.insert(
                    SQLITE_TABLE_CARDS,
                    null,
                    toContentValues(deckId, original))

            // write the card-to-expression relation (many-to-many)
            val cardsReversCV = generateCardsReverseContentValues(cardId, translations)
            for (cv in cardsReversCV) {
                db.insert(SQLITE_TABLE_CARDS_REVERSE, null, cv)
            }

            val card = Card(cardId, deckId, original, translations, emptyState())

            db.setTransactionSuccessful()

            return card
        } finally {
            db.endTransaction()
        }
    }

    override fun cardById(id: Long): Card? {
        // please make sure to cover it with tests in case of adding a real implementation
        throw UnsupportedOperationException("this method is currently only used in testing")
    }

    override fun allDecks(): List<Deck> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $SQLITE_TABLE_DECKS", null)

        val list = mutableListOf<Deck>()
        while (cursor.moveToNext()) {
            val id = cursor.getId()
            list.add(Deck(id, cursor.getName()))
        }

        cursor.close()
        return list
    }

    override fun deckById(id: Long): Deck? {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $SQLITE_TABLE_DECKS WHERE $SQLITE_COLUMN_ID = ?",
                arrayOf(id.toString()))

        cursor.use { it ->
            if (it.count == 0) return null
            if (it.count > 1) throw IllegalStateException("more that one value for deck id $id")

            it.moveToFirst()
            return Deck(id, it.getName())
        }
    }

    override fun addDeck(name: String): Deck {
        val cv = createDeckContentValues(name)

        val db = helper.writableDatabase
        val id = db.insert(SQLITE_TABLE_DECKS, null, cv)

        return Deck(id, name)
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
                    storage().expressionById(cursor.getFrontId())!!,
                    translationsByCardId(cardId),
                    // todo: read state here
                    State(today(), PERIOD_NEVER_SCHEDULED)
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

    override fun updateCardState(card: Card, state: State, exercise: Exercise): Card {
        val table = sqliteTableExerciseState(exercise)
        val cv = createStateContentValues(card.id, state)
        helper.writableDatabase.insertWithOnConflict(table, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        card.state = state
        return card
    }

    override fun cardsDueDate(exercise: Exercise, deck: Deck, date: DateTime): List<Card> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |FROM
            |   $SQLITE_TABLE_CARDS AS Cards
            |   LEFT JOIN ${sqliteTableExerciseState(exercise)} AS States
            |       ON Cards.$SQLITE_COLUMN_ID = States.$SQLITE_COLUMN_CARD_ID
            |WHERE
            |   Cards.$SQLITE_COLUMN_DECK_ID = ?
            |   AND
            |   (States.$SQLITE_COLUMN_DUE IS NULL OR States.$SQLITE_COLUMN_DUE <= ?)
        """.trimMargin(),
                arrayOf(deck.id.toString(), date.timespamp.toString()))

        val cards = mutableListOf<Card>()
        while (cursor.moveToNext()) {
            val state = extractState(cursor)
            cards.add(Card(
                    cursor.getId(),
                    deck.id,
                    expressionById(cursor.getFrontId())!!,
                    translationsByCardId(cursor.getId()),
                    state
            ))
        }
        cursor.close()

        return cards
    }

    override fun cardsDueDateCount(exercise: Exercise, deck: Deck, date: DateTime): Counts {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |FROM
            |   $SQLITE_TABLE_CARDS AS Cards
            |   LEFT JOIN ${sqliteTableExerciseState(exercise)} AS States
            |       ON Cards.$SQLITE_COLUMN_ID = States.$SQLITE_COLUMN_CARD_ID
            |WHERE
            |   Cards.$SQLITE_COLUMN_DECK_ID = ?
            |   AND
            |   (States.$SQLITE_COLUMN_DUE IS NULL OR States.$SQLITE_COLUMN_DUE <= ?)
        """.trimMargin(),
                arrayOf(deck.id.toString(), date.timespamp.toString()))

        val states = mutableMapOf<Status, Int>()
        while (cursor.moveToNext()) {
            val state = extractState(cursor)
            states[state.status] = states[state.status]?.plus(1) ?: 1
        }

        cursor.close()

        return PendingCounter.createFrom(states)
    }

    private fun extractState(cursor: Cursor) =
            if (cursor.hasSavedState()) State(cursor.getDateDue(), cursor.getPeriod()) else emptyState()

    fun storage() = Repository.get(context)
}