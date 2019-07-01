package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.model.*

fun addMockCard(
        storage: Repository,
        deckId: Long = 1L,
        original: String = "spring",
        translations: List<String> = listOf("весна", "источник"),
        domain: Domain = mockDomain()
): Card {
    return storage.addCard(
            domain,
            deckId,
            storage.addExpression(original, domain.langOriginal()),
            translations.map { storage.addExpression(it, domain.langTranslations()) })
}

fun addMockCard(storage: Repository, cardData: CardData, domain: Domain = mockDomain(), type: CardType = CardType.FORWARD): Card {
    val original = storage.justAddExpression(cardData.original, domain.langOriginal(type))
    return storage.addCard(
            domain,
            cardData.deckId,
            original,
            cardData.translations.map { storage.justAddExpression(it, domain.langTranslations(type)) }
    )
}

fun addMockExpressionOriginal(storage: Repository, value: String = "some value", domain: Domain): Expression {
    return storage.justAddExpression(value, domain.langOriginal())
}

fun addMockExpressionTranslation(storage: Repository, value: String = "some value", domain: Domain): Expression {
    return storage.justAddExpression(value, domain.langTranslations())
}