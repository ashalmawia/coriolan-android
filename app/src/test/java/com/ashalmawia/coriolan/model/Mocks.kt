package com.ashalmawia.coriolan.model

import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.learning.scheduler.State
import org.joda.time.DateTime

fun mockCardData(original: String, translations: List<String>, deckId: Long = 1L, type: ExpressionType = ExpressionType.WORD)
        = CardData(original, translations, deckId, type)

fun mockCardData(original: String = "shrimp", translation: String = "креветка", deckId: Long = 1L, type: ExpressionType = ExpressionType.WORD)
        = mockCardData(original, listOf(translation), deckId, type)

fun mockExpression(value: String = "mock value", type: ExpressionType = ExpressionType.WORD)
        = Expression(99L, value, type)

private var cardId = 1L
fun mockCard(state: State = mockState()): Card {
    return Card(cardId++, mockExpression(), listOf(mockExpression(), mockExpression()), state)
}

private var deckId = 1L
fun mockDeck(name: String = "My deck") = Deck(deckId++, name, listOf())

fun mockState(period: Int = 0): State {
    return State(DateTime.now(), period)
}