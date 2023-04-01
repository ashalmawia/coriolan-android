package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.TermInfo
import com.ashalmawia.coriolan.model.Extras
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.ObjectMapper

private const val FIELD_ID = "id"
private const val FIELD_VALUE = "value"
private const val FIELD_LANGUAGE_ID = "lang_id"
private const val FIELD_EXTRAS = "extras"

fun readTermFromJson(json: JsonParser, objectMapper: ObjectMapper): TermInfo {
    var id: Long? = null
    var value: String? = null
    var langId: Long? = null
    var extrasStr: String? = null

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
            FIELD_EXTRAS -> {
                json.nextToken()
                extrasStr = json.text
            }
        }
    }

    if (id == null || value == null || langId == null) {
        throw JsonDeserializationException("failed to read term, id $id, value[$value], langId $langId")
    }

    val extras = extrasStr?.run { objectMapper.readValue(this, Extras::class.java) } ?: Extras.empty()
    return TermInfo(id, value, langId, extras)
}

fun writeTermToJson(term: TermInfo, json: JsonGenerator, objectMapper: ObjectMapper) {
    json.writeStartObject()

    json.writeNumberField(FIELD_ID, term.id)
    json.writeStringField(FIELD_VALUE, term.value)
    json.writeNumberField(FIELD_LANGUAGE_ID, term.languageId)

    if (term.extras != null) {
        val extrasStr = objectMapper.writeValueAsString(term.extras)
        json.writeStringField(FIELD_EXTRAS, extrasStr)
    }

    json.writeEndObject()
}