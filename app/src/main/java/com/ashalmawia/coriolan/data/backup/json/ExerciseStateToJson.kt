package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.LearningProgressInfo
import com.ashalmawia.coriolan.util.asCardId
import com.ashalmawia.coriolan.util.timespamp
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import org.joda.time.DateTime

private const val FIELD_CARD_ID = "id"
private const val FIELD_DUE = "due"
private const val FIELD_INTERVAL = "interval"
private const val FIELD_INTERVAL_LEGACY = "period"

fun readExerciseStateFromJson(json: JsonParser): LearningProgressInfo {
    var cardId: Long? = null
    var due: Long? = null
    var interval: Int? = null

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
            FIELD_INTERVAL, FIELD_INTERVAL_LEGACY -> {
                json.nextToken()
                interval = json.intValue
            }
        }
    }

    if (cardId == null || due == null || interval == null) {
        throw JsonDeserializationException("failed to read exercise state: " +
                "cardId $cardId, due $due, interval $interval")
    }

    return LearningProgressInfo(cardId.asCardId(), DateTime(due), interval)
}

fun writeExerciseStateToJson(state: LearningProgressInfo, json: JsonGenerator) {
    json.writeStartObject()

    json.writeNumberField(FIELD_CARD_ID, state.cardId.value)
    json.writeNumberField(FIELD_DUE, state.due.timespamp)
    json.writeNumberField(FIELD_INTERVAL, state.interval)

    json.writeEndObject()
}