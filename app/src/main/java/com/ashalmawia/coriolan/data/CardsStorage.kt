package com.ashalmawia.coriolan.data

import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Expression
import com.ashalmawia.coriolan.model.ExpressionType

object CardsStorage {

    val stub : List<Card>

    init {
        stub = createStubDeck()
    }

    fun cardsByDeckName(name: String): List<Card> {
        return ArrayList<Card>(stub)
    }

    fun cardById(id: Int): Card {
        return stub[id-1]
    }

    private fun createStubDeck(): ArrayList<Card> {
        val list = ArrayList<Card>()

        list.add(Card.create(1, toExpression("ru", "красный"), toExpression("en", "red")))
        list.add(Card.create(2, toExpression("ru", "зеленый"), toExpression("en", "green")))
        list.add(Card.create(3, toExpression("ru", "синий"), toExpression("en", "blue")))
//        list.add(Card.create(4, toExpression("ru", "желтый"), toExpression("en", "yellow")))
//        list.add(Card.create(5, toExpression("ru", "белый"), toExpression("en", "white")))
//        list.add(Card.create(6, toExpression("ru", "коричневый"), toExpression("en", "brown")))
//        list.add(Card.create(7, toExpression("ru", "розовый"), toExpression("en", "pink")))
//        list.add(Card.create(8, toExpression("ru", "золотой"), toExpression("en", "gold")))
//        list.add(Card.create(9, toExpression("ru", "черный"), toExpression("en", "black")))
//        list.add(Card.create(10, toExpression("ru", "оранжевый"), toExpression("en", "orange")))

        return list
    }

    private fun toExpression(lang: String, value: String): Expression {
        return Expression(lang, value, ExpressionType.WORD)
    }

}