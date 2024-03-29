package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.model.*
import com.ashalmawia.coriolan.util.asDeckId

fun addMockCard(
        storage: Repository,
        deckId: DeckId = 1L.asDeckId(),
        original: String = "spring",
        translations: List<String> = listOf("весна", "источник"),
        domain: Domain = mockDomain(),
        type: CardType = CardType.FORWARD
): Card {
    return storage.addCard(
            domain,
            deckId,
            storage.addTerm(original, domain.langOriginal(type), null),
            translations.map { storage.addTerm(it, domain.langTranslations(type), null) })
}

fun addMockCard(storage: Repository, cardData: CardData, domain: Domain = mockDomain(), type: CardType = CardType.FORWARD): Card {
    val original = storage.justAddTerm(cardData.original, domain.langOriginal(type))
    return storage.addCard(
            domain,
            cardData.deck.id,
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