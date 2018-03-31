package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.SRStateInfo
import com.ashalmawia.coriolan.util.timespamp
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import org.joda.time.DateTime

private const val FIELD_CARD_ID = "id"
private const val FIELD_DUE = "due"
private const val FIELD_PERIOD = "period"

fun readSRStateFromJson(json: JsonParser): SRStateInfo {
    var cardId: Long? = null
    var due: Long? = null
    var period: Int? = null

    while (json.nextToken() != JsonToken.END_OBJECT) {
        when (json.currentName) {
            FIELD_CARD_ID -> {
                json.nextToken()
                cardId = json.longValue
            }
            FIELD_DUE -> {
                json.nextToken()
                due = json.longValue
            }
            FIELD_PERIOD -> {
                json.nextToken()
                period = json.intValue
            }
        }
    }

    if (cardId == null || due == null || period == null) {
        throw JsonDeserializationException("failed to read SR state, cardId $cardId, due $due, period $period")
    }

    return SRStateInfo(cardId, DateTime(due), period)
}

fun writeSRStateToJson(state: SRStateInfo, json: JsonGenerator) {
    json.writeStartObject()

    json.writeNumberField(FIELD_CARD_ID, state.cardId)
    json.writeNumberField(FIELD_DUE, state.due.timespamp)
    json.writeNumberField(FIELD_PERIOD, state.period)

    json.writeEndObject()
}