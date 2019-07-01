package com.ashalmawia.coriolan.data.importer

import android.content.Context
import android.support.annotation.StringRes
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

interface DataImporter {

    var flow: DataImportFlow?

    @StringRes
    fun label() : Int

    fun launch(context: Context)
}

@JsonClass(generateAdapter = true)
data class JsonCardData(
        @Json(name = "original")
        val original: String,

        @Json(name = "transcription")
        val transcription: String?,

        @Json(name = "translations")
        val translations: List<String>
)