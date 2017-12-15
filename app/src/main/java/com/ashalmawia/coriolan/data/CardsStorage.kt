package com.ashalmawia.coriolan.data

import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Expression
import com.ashalmawia.coriolan.model.ExpressionType

object CardsStorage {

    fun cardsByDeckName(name: String) :List<Card> {
        return createStubDeck()
    }

    private fun createStubDeck(): ArrayList<Card> {
        val list = ArrayList<Card>()

        list.add(Card.create(toExpression("ru", "красный"), toExpression("en", "red")))
        list.add(Card.create(toExpression("ru", "зеленый"), toExpression("en", "green")))
        list.add(Card.create(toExpression("ru", "синий"), toExpression("en", "blue")))
        list.add(Card.create(toExpression("ru", "желтый"), toExpression("en", "yellow")))
        list.add(Card.create(toExpression("ru", "белый"), toExpression("en", "white")))
        list.add(Card.create(toExpression("ru", "коричневый"), toExpression("en", "brown")))
        list.add(Card.create(toExpression("ru", "розовый"), toExpression("en", "pink")))
        list.add(Card.create(toExpression("ru", "золотой"), toExpression("en", "gold")))
        list.add(Card.create(toExpression("ru", "черный"), toExpression("en", "black")))
        list.add(Card.create(toExpression("ru", "оранжевый"), toExpression("en", "orange")))

        return list
    }

    fun toExpression(lang: String, value: String) :Expression {
        return Expression(lang, value, ExpressionType.WORD)
    }

}