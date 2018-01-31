package com.ashalmawia.coriolan.model

import com.ashalmawia.coriolan.data.importer.CardData
import org.junit.Assert.*
import java.util.*

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

fun assertSameDate(date: Date, other: Date) {
    val cal1 = Calendar.getInstance()
    cal1.time = date

    val cal2 = Calendar.getInstance()
    cal2.time = other

    assertEquals(cal1.get(Calendar.DAY_OF_MONTH), cal2.get(Calendar.DAY_OF_MONTH))
    assertEquals(cal1.get(Calendar.MONTH), cal2.get(Calendar.MONTH))
    assertEquals(cal1.get(Calendar.YEAR), cal2.get(Calendar.YEAR))
}