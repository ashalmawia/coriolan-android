package com.ashalmawia.coriolan.data.importer.file

import com.ashalmawia.coriolan.data.importer.JsonCardData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImportData(
        @Json(name = "cards")
        val cards: List<JsonCardData>
)