package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.model.*

fun addMockCard(storage: Repository, deckId: Long = 1L, original: String = "spring", translations: List<String> = listOf("весна", "источник"), domain: Domain = mockDomain()): Card {
    return storage.addCard(
            domain,
            deckId,
            storage.addExpression(original, ExpressionType.WORD, domain.langOriginal()),
            translations.map { storage.addExpression(it, ExpressionType.WORD, domain.langTranslations()) })
}

fun addMockCard(storage: Repository, cardData: CardData, domain: Domain = mockDomain()): Card {
    val original = storage.addExpression(cardData.original, cardData.contentType, domain.langOriginal())
    return storage.addCard(
            domain,
            cardData.deckId,
            original,
            cardData.translations.map { storage.addExpression(it, cardData.contentType, domain.langTranslations()) }
    )
}

fun addMockExpressionOriginal(storage: Repository, value: String = "some value", type: ExpressionType = ExpressionType.WORD, domain: Domain): Expression {
    return storage.addExpression(value, type, langOriginal())
}

fun addMockExpressionTranslation(storage: Repository, value: String = "some value", type: ExpressionType = ExpressionType.WORD, domain: Domain): Expression {
    return storage.addExpression(value, type, langTranslations())
}