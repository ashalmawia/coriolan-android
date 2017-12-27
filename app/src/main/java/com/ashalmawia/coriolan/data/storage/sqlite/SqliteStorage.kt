package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.Context
import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.data.storage.Storage
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Expression
import com.ashalmawia.coriolan.model.ExpressionType

class SqliteStorage(context: Context) : Storage {

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
            val translation = addExpression(data.translation, data.type)
            val cardId = db.insert(
                    SQLITE_TABLE_CARDS,
                    null,
                    toContentValues(data.deckId, original, translation))

            // todo: tmp https://trello.com/c/EJBtdetZ
//            db.insert(SQLITE_TABLE_CARDS_REVERSE,
//                    null,
//                    generateCardsReverseContentValues(cardId, translation)[0])

            val card = Card.create(cardId, original, translation)

            db.setTransactionSuccessful()

            return card
        } finally {
            db.endTransaction()
        }
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
        val cursor = db.rawQuery("SELECT * FROM $SQLITE_TABLE_CARDS WHERE $SQLITE_COLUMN_DECK_ID = ?",
                arrayOf(id.toString()))

        val list = mutableListOf<Card>()
        while (cursor.moveToNext()) {
            list.add(Card.create(
                    cursor.getId(),
                    expressionById(cursor.getFrontId())!!,
                    expressionById(cursor.getReverseId())!!
            ))
        }

        cursor.close()
        return list
    }
}