package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.model.*

fun addMockCard(storage: Repository, deckId: Long = 1L, original: String = "spring", translations: List<String> = listOf("весна", "источник"), domain: Domain = mockDomain()): Card {
    return storage.addCard(
            domain,
            deckId,
            storage.addExpression(original, domain.langOriginal()),
            translations.map { storage.addExpression(it, domain.langTranslations()) })
}

fun addMockCard(storage: Repository, cardData: CardData, domain: Domain = mockDomain(), type: CardType = CardType.FORWARD): Card {
    val original = storage.addExpression(cardData.original, domain.langOriginal(type))
    return storage.addCard(
            domain,
            cardData.deckId,
            original,
            cardData.translations.map { storage.addExpression(it, domain.langTranslations(type)) }
    )
}

fun addMockExpressionOriginal(storage: Repository, value: String = "some value", domain: Domain): Expression {
    return storage.addExpression(value, domain.langOriginal())
}

fun addMockExpressionTranslation(storage: Repository, value: String = "some value", domain: Domain): Expression {
    return storage.addExpression(value, domain.langTranslations())
}