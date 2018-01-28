package com.ashalmawia.coriolan.model

import com.ashalmawia.coriolan.data.importer.CardData

fun mockCardData(original: String, translations: List<String>, deckId: Long = 1L, type: ExpressionType = ExpressionType.WORD)
        = CardData(original, translations, deckId, type)

fun mockCardData(original: String, translation: String, deckId: Long = 1L, type: ExpressionType = ExpressionType.WORD)
        = mockCardData(original, listOf(translation), deckId, type)

fun mockExpression(value: String = "mock value", type: ExpressionType = ExpressionType.WORD)
        = Expression(99L, value, type)

private var cardId = 1L
fun mockCard(): Card {
    return Card.create(cardId++, mockExpression(), listOf(mockExpression(), mockExpression()))
}

private var deckId = 1L
fun mockDeck(name: String = "My deck") = Deck(deckId++, name, listOf())