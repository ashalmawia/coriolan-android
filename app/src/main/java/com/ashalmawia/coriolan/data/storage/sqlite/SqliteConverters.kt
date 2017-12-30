package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.ContentValues
import com.ashalmawia.coriolan.model.Expression
import com.ashalmawia.coriolan.model.ExpressionType

// ********** EXPRESSION ********************

fun createExpressionContentValues(value: String, type: ExpressionType): ContentValues {
    val cv = ContentValues()
    cv.put(SQLITE_COLUMN_VALUE, value)
    cv.put(SQLITE_COLUMN_TYPE, type.value)
    return cv
}

// ********** CARD ********************

fun toContentValues(deckId: Long, original: Expression): ContentValues {
    val cv = ContentValues()
    cv.put(SQLITE_COLUMN_FRONT_ID, original.id)
    cv.put(SQLITE_COLUMN_DECK_ID, deckId)
    return cv
}

fun generateCardsReverseContentValues(cardId: Long, translations: List<Expression>): List<ContentValues> {
    return translations.map { toCardsReverseContentValues(cardId, it) }
}

private fun toCardsReverseContentValues(cardId: Long, expression: Expression): ContentValues {
    val cv = ContentValues()
    cv.put(SQLITE_COLUMN_CARD_ID, cardId)
    cv.put(SQLITE_COLUMN_EXPRESSION_ID, expression.id)
    return cv
}

// ********** DECK ********************

fun createDeckContentValues(name: String): ContentValues {
    val cv = ContentValues()
    cv.put(SQLITE_COLUMN_NAME, name)
    return cv
}