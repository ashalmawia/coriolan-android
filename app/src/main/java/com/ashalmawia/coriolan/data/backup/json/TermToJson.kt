package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.TermInfo
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken

private const val FIELD_ID = "id"
private const val FIELD_VALUE = "value"
private const val FIELD_LANGUAGE_ID = "lang_id"

fun readTermFromJson(json: JsonParser): TermInfo {
    var id: Long? = null
    var value: String? = null
    var langId: Long? = null

    while (json.nextToken() != JsonToken.END_OBJECT) {
        when (json.currentName) {
            FIELD_ID -> {
                json.nextToken()
                id = json.longValue
            }
            FIELD_VALUE -> {
                json.nextToken()
                value = json.text
            }
            FIELD_LANGUAGE_ID -> {
                json.nextToken()
                langId = json.longValue
            }
        }
    }

    if (id == null || value == null || langId == null) {
        throw JsonDeserializationException("failed to read term, id $id, value[$value], langId $langId")
    }

    return TermInfo(id, value, langId)
}

fun writeTermToJson(term: TermInfo, json: JsonGenerator) {
    json.writeStartObject()

    json.writeNumberField(FIELD_ID, term.id)
    json.writeStringField(FIELD_VALUE, term.value)
    json.writeNumberField(FIELD_LANGUAGE_ID, term.languageId)

    json.writeEndObject()
}