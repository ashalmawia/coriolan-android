package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.TermInfo
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.ObjectMapper

private const val FIELD_ID = "id"
private const val FIELD_VALUE = "value"
private const val FIELD_LANGUAGE_ID = "lang_id"
private const val FIELD_TRANSCRIPTION = "transcription"
private const val FIELD_LEGACY_EXTRAS = "extras"

fun readTermFromJson(json: JsonParser, objectMapper: ObjectMapper): TermInfo {
    var id: Long? = null
    var value: String? = null
    var langId: Long? = null
    var transcription: String? = null
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
            FIELD_TRANSCRIPTION -> {
                json.nextToken()
                transcription = json.text
            }
            FIELD_LEGACY_EXTRAS -> {
                json.nextToken()
                extrasStr = json.text
            }
        }
    }

    if (id == null || value == null || langId == null) {
        throw JsonDeserializationException("failed to read term, id $id, value[$value], langId $langId")
    }

    val extras = extrasStr?.run { objectMapper.readValue(this, LegacyExtras::class.java) }
    if (extras != null) transcription = extras.transcription
    return TermInfo(id, value, langId, transcription)
}

fun writeTermToJson(term: TermInfo, json: JsonGenerator) {
    json.writeStartObject()

    json.writeNumberField(FIELD_ID, term.id)
    json.writeStringField(FIELD_VALUE, term.value)
    json.writeNumberField(FIELD_LANGUAGE_ID, term.languageId)
    if (term.transcription != null) {
        json.writeStringField(FIELD_TRANSCRIPTION, term.transcription)
    }

    json.writeEndObject()
}

private data class LegacyExtras(val transcription: String?)