package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.LanguageInfo
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken

private const val NAME_ID = "id"
private const val NAME_VALUE = "value"

fun readLanguageFromJson(json: JsonParser): LanguageInfo {
    var id: Long? = null
    var value: String? = null
    while (json.nextToken() != JsonToken.END_OBJECT) {
        when (json.currentName) {
            NAME_ID -> {
                json.nextToken()
                id = json.longValue
            }
            NAME_VALUE -> {
                json.nextToken()
                value = json.text
            }
        }
    }

    if (id == null || value == null) {
        throw JsonDeserializationException("failed to read language: id $id, value[$value]")
    }
    return LanguageInfo(id, value)
}

fun writeLanguageToJson(language: LanguageInfo, json: JsonGenerator) {
    json.writeStartObject()
    json.writeNumberField(NAME_ID, language.id)
    json.writeStringField(NAME_VALUE, language.value)
    json.writeEndObject()
}