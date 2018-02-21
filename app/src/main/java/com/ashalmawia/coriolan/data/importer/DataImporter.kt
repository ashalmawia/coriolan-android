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
)