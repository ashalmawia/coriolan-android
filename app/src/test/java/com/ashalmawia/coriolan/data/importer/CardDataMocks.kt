package com.ashalmawia.coriolan.data.importer

import com.ashalmawia.coriolan.model.CardData

fun reversedTo(cardData: CardData): List<CardData> {
    return cardData.translations.map { createReversedForTranslation(cardData, it) }
}

private fun createReversedForTranslation(cardData: CardData, translation: String): CardData {
    return CardData(
            original = translation,
            transcription = null,
            deck = cardData.deck,
            translations = listOf(cardData.original)
    )
}