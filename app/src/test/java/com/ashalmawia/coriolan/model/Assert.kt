package com.ashalmawia.coriolan.model

import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Expression
import com.ashalmawia.coriolan.model.ExpressionType
import org.junit.Assert.*

fun assertExpressionCorrect(expression: Expression?, value: String, type: ExpressionType) {
    assertNotNull("expression is found", expression)
    assertEquals("expression has correct values", value, expression!!.value)
    assertEquals("expression has correct values", type, expression.type)
}

fun assertCardCorrect(card: Card?, data: CardData) {
    assertNotNull("card is created", card)
    assertExpressionCorrect(card!!.original, data.original, data.type)
//    assertEquals("translations count is correct", data.translation.size, card.translations.size)
    for (i in 0 until card.translations.size) {
//        assertExpressionCorrect(card.translations[i], data.translations[i], data.type)
        assertExpressionCorrect(card.translations[i], data.translation, data.type)
    }
}

fun assertDeckCorrect(deck: Deck?, name: String, cards: List<CardData>? = null) {
    assertNotNull("deck is created", deck)
    assertEquals("deck name is correct", name, deck!!.name)
    if (cards != null) {
        assertEquals("cards count is correct", cards.size, deck.cards().size)
        for (i in 0 until cards.size) {
            assertCardCorrect(deck.cards()[i], cards[i])
        }
    }
}