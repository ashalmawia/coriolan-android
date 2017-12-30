package com.ashalmawia.coriolan.model

import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.model.ExpressionType

fun mockCardData(original: String, translations: List<String>, deckId: Long = 1L, type: ExpressionType = ExpressionType.WORD)
        = CardData(original, translations, deckId, type)

fun mockCardData(original: String, translation: String, deckId: Long = 1L, type: ExpressionType = ExpressionType.WORD)
        = mockCardData(original, listOf(translation), deckId, type)