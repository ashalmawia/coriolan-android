package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.Context
import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Expression
import com.ashalmawia.coriolan.model.ExpressionType

class SqliteStorage(private val context: Context) : Repository {

    private val helper = MySqliteOpenHelper(context)

    override fun addExpression(value: String, type: ExpressionType): Expression {
        val id = helper.writableDatabase.insert(SQLITE_TABLE_EXPRESSIONS,
                null,
                createExpressionContentValues(value, type))
        return Expression(id, value, type)
    }

    override fun expressionById(id: Long): Expression? {
        val db = helper.readableDatabase
        val cursor = db.rawQuery("""SELECT * FROM $SQLITE_TABLE_EXPRESSIONS WHERE $SQLITE_COLUMN_ID = ?""",
                arrayOf(id.toString()))

        if (cursor.count == 0) return null
        if (cursor.count > 1) throw IllegalStateException("more that one expression for id $id")

        cursor.moveToFirst()
        val expression = Expression(id, cursor.getValue(), cursor.getExpressionType())
        cursor.close()

        return expression
    }

    override fun addCard(data: CardData): Card {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            val original = addExpression(data.original, data.type)
            val translations = data.translations.map { addExpression(it, data.type) }
            val cardId = db.insert(
                    SQLITE_TABLE_CARDS,
                    null,
                    toContentValues(data.deckId, original))

            // write the card-to-expression relation (many-to-many)
            val cardsReversCV = generateCardsReverseContentValues(cardId, translations)
            for (cv in cardsReversCV) {
                db.insert(SQLITE_TABLE_CARDS_REVERSE, null, cv)
            }

            val card = Card.create(cardId, original, translations)

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
            list.add(Deck(id, cursor.getName(), cardsByDeckId(id)))
        }

        cursor.close()
        return list
    }

    override fun deckById(id: Long): Deck? {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $SQLITE_TABLE_DECKS WHERE $SQLITE_COLUMN_ID = ?",
                arrayOf(id.toString()))

        if (cursor.count == 0) { return null }
        if (cursor.count > 1) throw IllegalStateException("more that one value for deck id $id")

        cursor.moveToFirst()
        val result = Deck(id, cursor.getName(), cardsByDeckId(id))
        cursor.close()
        return result
    }

    override fun addDeck(name: String): Deck {
        val cv = createDeckContentValues(name)

        val db = helper.writableDatabase
        val id = db.insert(SQLITE_TABLE_DECKS, null, cv)

        return Deck(id, name, arrayListOf())
    }

    private fun cardsByDeckId(id: Long): List<Card> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT * FROM $SQLITE_TABLE_CARDS
            |WHERE $SQLITE_COLUMN_DECK_ID = ?
            |""".trimMargin(), arrayOf(id.toString()))

        val list = mutableListOf<Card>()
        while (cursor.moveToNext()) {
            val cardId = cursor.getId()
            list.add(Card.create(
                    cardId,
                    storage().expressionById(cursor.getFrontId())!!,
                    translationsByCardId(cardId))
            )
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

    fun storage() = Repository.get(context)
}