package com.ashalmawia.coriolan.data.storage.stub

import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.data.storage.Storage
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Expression
import com.ashalmawia.coriolan.model.ExpressionType

class StubStorage : Storage {

    private var index = 0

    private val decks = listOf(
            Deck(1, "Default", cardsByDeckId(1))
    )

    override fun addCard(data: CardData): Card {
        return Card.create(
                index++,
                Expression("ru", data.original, ExpressionType.WORD),
                Expression("en", data.translation, ExpressionType.WORD))
    }

    override fun cardsByDeckId(id: Int): List<Card> {
        return ArrayList()
    }

    override fun allDecks(): List<Deck> {
        return ArrayList(decks)
    }

    override fun deckById(id: Int): Deck? {
        return decks.find { it.id == id }
    }

    //    private fun createStubDeck(): ArrayList<Card> {
//        val list = ArrayList<Card>()
//
//        list.add(Card.create(1, toExpression("ru", "красный"), toExpression("en", "red")))
//        list.add(Card.create(2, toExpression("ru", "зеленый"), toExpression("en", "green")))
//        list.add(Card.create(3, toExpression("ru", "синий"), toExpression("en", "blue")))
//        list.add(Card.create(4, toExpression("ru", "желтый"), toExpression("en", "yellow")))
//        list.add(Card.create(5, toExpression("ru", "белый"), toExpression("en", "white")))
//        list.add(Card.create(6, toExpression("ru", "коричневый"), toExpression("en", "brown")))
//        list.add(Card.create(7, toExpression("ru", "розовый"), toExpression("en", "pink")))
//        list.add(Card.create(8, toExpression("ru", "золотой"), toExpression("en", "gold")))
//        list.add(Card.create(9, toExpression("ru", "черный"), toExpression("en", "black")))
//        list.add(Card.create(10, toExpression("ru", "оранжевый"), toExpression("en", "orange")))
//
//        return list
//    }
//
//    private fun toExpression(lang: String, value: String): Expression {
//        return Expression(lang, value, ExpressionType.WORD)
//    }
}