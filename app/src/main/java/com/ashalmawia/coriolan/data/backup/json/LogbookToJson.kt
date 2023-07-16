package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.logbook.LogbookEntryInfo
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken

private const val FIELD_DATE = "date"
private const val FIELD_PAYLOAD = "payload"

fun readLogbookEntryFromJson(json: JsonParser): LogbookEntryInfo {
    var date: Long? = null
    var payload: String? = null

    while (json.nextToken() != JsonToken.END_OBJECT) {
        when (json.currentName) {
            FIELD_DATE -> {
                json.nextToken()
                date = json.longValue
            }
            FIELD_PAYLOAD -> {
                json.nextToken()
                payload = json.text
            }
        }
    }

    if (date == null || payload == null) {
        throw JsonDeserializationException("failed to deserialize logbook payload, date $date, payload $payload")
    }

    return LogbookEntryInfo(date, payload)
}

fun writeLogbookEntryToJson(entry: LogbookEntryInfo, json: JsonGenerator) {
    json.writeStartObject()

    json.writeNumberField(FIELD_DATE, entry.date)
    json.writeStringField(FIELD_PAYLOAD, entry.payload)

    json.writeEndObject()
}