package com.ashalmawia.coriolan.data.importer.file

import com.ashalmawia.coriolan.data.importer.JsonCardData
import com.squareup.moshi.Moshi

class JsonCardDataParser {

    private val moshi = Moshi.Builder().build()
    private val adapter = moshi.adapter(ImportData::class.java)

    fun parse(text: String): List<JsonCardData> {
        if (text.isBlank()) {
            return emptyList()
        }

        try {
            val data = adapter.fromJson(text)
            return data?.cards ?: emptyList()
        } catch (e: Exception) {
            throw ParsingException(text, e)
        }
    }
}

class ParsingException(val line: String, e: java.lang.Exception) : Exception("failed to parse the line[$line]", e)