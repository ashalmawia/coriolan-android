package com.ashalmawia.coriolan.data.importer

import android.content.Context
import android.support.annotation.StringRes
import com.ashalmawia.coriolan.model.ExpressionType

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
        val translations: List<String>,
        val deckId: Long,
        val type: ExpressionType
)