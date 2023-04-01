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
            storage.addTerm(original, domain.langOriginal(), null),
            translations.map { storage.addTerm(it, domain.langTranslations(), null) })
}

fun addMockCard(storage: Repository, cardData: CardData, domain: Domain = mockDomain(), type: CardType = CardType.FORWARD): Card {
    val original = storage.justAddTerm(cardData.original, domain.langOriginal(type))
    return storage.addCard(
            domain,
            cardData.deckId,
            original,
            cardData.translations.map { storage.justAddTerm(it, domain.langTranslations(type)) }
    )
}

fun addMockTermOriginal(storage: Repository, value: String = "some value", domain: Domain): Term {
    return storage.justAddTerm(value, domain.langOriginal())
}

fun addMockTermTranslation(storage: Repository, value: String = "some value", domain: Domain): Term {
    return storage.justAddTerm(value, domain.langTranslations())
}