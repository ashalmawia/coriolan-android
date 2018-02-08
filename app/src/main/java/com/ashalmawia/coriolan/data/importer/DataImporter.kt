package com.ashalmawia.coriolan.data.importer

import android.content.Context
import android.support.annotation.StringRes
import com.ashalmawia.coriolan.model.ExpressionType
import com.ashalmawia.coriolan.model.Language

interface DataImporter {

    @StringRes
    fun label() : Int

    fun launch(context: Context)

    fun ongoing(): DataImportFlow {
        return DataImportFlow.ongoing!!
    }
}

data class CardData(
        val original: String,
        val originalLang: Language,
        val translations: List<String>,
        val translationsLang: Language,
        val deckId: Long,
        val contentType: ExpressionType
) {

    companion object {

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
    }
}