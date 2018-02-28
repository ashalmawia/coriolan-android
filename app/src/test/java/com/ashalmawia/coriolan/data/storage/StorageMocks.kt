package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.LanguagesRegistry
import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.model.*

fun addMockCard(storage: Repository, deckId: Long = 1L, original: String = "spring", translations: List<String> = listOf("весна", "источник")): Card {
    return storage.addCard(
            deckId,
            storage.addExpression(original, ExpressionType.WORD, LanguagesRegistry.original()),
            translations.map { storage.addExpression(it, ExpressionType.WORD, LanguagesRegistry.translations()) })
}

fun addMockCard(storage: Repository, cardData: CardData): Card {
    val original = storage.addExpression(cardData.original, cardData.contentType, cardData.originalLang)
    return storage.addCard(
            cardData.deckId,
            original,
            cardData.translations.map { storage.addExpression(it, cardData.contentType, cardData.translationsLang) }
    )
}

fun addMockExpressionOriginal(storage: Repository, value: String = "some value", type: ExpressionType = ExpressionType.WORD): Expression {
    return storage.addExpression(value, type, langOriginal())
}

fun addMockExpressionTranslation(storage: Repository, value: String = "some value", type: ExpressionType = ExpressionType.WORD): Expression {
    return storage.addExpression(value, type, langTranslations())
}