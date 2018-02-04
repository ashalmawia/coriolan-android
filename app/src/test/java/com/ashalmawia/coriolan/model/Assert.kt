package com.ashalmawia.coriolan.model

import com.ashalmawia.coriolan.data.importer.CardData
import org.junit.Assert.*

fun assertExpressionCorrect(expression: Expression?, value: String, type: ExpressionType) {
    assertNotNull("expression is found", expression)
    assertEquals("expression has correct values", value, expression!!.value)
    assertEquals("expression has correct values", type, expression.type)
}

fun assertCardCorrect(card: Card?, data: CardData) {
    assertNotNull("card is created", card)
    assertExpressionCorrect(card!!.original, data.original, data.type)
    assertEquals("translations count is correct", data.translations.size, card.translations.size)
    for (i in 0 until card.translations.size) {
        assertExpressionCorrect(card.translations[i], data.translations[i], data.type)
    }
}

fun assertDeckCorrect(deck: Deck?, name: String) {
    assertNotNull("deck is created", deck)
    assertEquals("deck name is correct", name, deck!!.name)
}

fun assertDeckCardsCorrect(cards: List<Card>, data: List<CardData>) {
    assertEquals("cards count is correct", data.size, cards.size)
    for (i in 0 until cards.size) {
        assertCardCorrect(cards[i], data[i])
    }
}