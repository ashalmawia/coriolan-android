package com.ashalmawia.coriolan.data.importer

fun reversedTo(cardData: CardData): List<CardData> {
    return cardData.translations.map { createReversedForTranslation(cardData, it) }
}

private fun createReversedForTranslation(cardData: CardData, translation: String): CardData {
    return CardData(
            original = translation,
            originalLang = cardData.translationsLang,
            deckId = cardData.deckId,
            translations = listOf(cardData.original),
            translationsLang = cardData.originalLang,
            contentType = cardData.contentType
    )
}