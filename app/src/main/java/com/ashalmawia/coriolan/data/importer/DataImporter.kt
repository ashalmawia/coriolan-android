package com.ashalmawia.coriolan.data.importer

import android.content.Context
import android.support.annotation.StringRes

interface DataImporter {

    var flow: DataImportFlow?

    @StringRes
    fun label() : Int

    fun launch(context: Context)
}

data class CardData(
        val original: String,
        val translations: List<String>,
        val deckId: Long
)