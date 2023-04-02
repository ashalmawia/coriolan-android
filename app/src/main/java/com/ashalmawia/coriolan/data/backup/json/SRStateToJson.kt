package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.ExerciseStateInfo
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.util.timespamp
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import org.joda.time.DateTime

private const val FIELD_CARD_ID = "id"
private const val FIELD_EXERICSE_ID = "exercise"
private const val FIELD_DUE = "due"
private const val FIELD_PERIOD = "period"

fun readSRStateFromJson(json: JsonParser): ExerciseStateInfo {
    var cardId: Long? = null
    var exercise: String? = ExerciseId.FLASHCARDS.value    // for compatibility with legacy backups
    var due: Long? = null
    var period: Int? = null

    while (json.nextToken() != JsonToken.END_OBJECT) {
        when (json.currentName) {
            FIELD_CARD_ID -> {
                json.nextToken()
                cardId = json.longValue
            }
            FIELD_EXERICSE_ID -> {
                json.nextToken()
                exercise = json.text
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

    if (cardId == null || exercise == null || due == null || period == null) {
        throw JsonDeserializationException("failed to read SR state: " +
                "cardId $cardId, exercise $exercise, due $due, period $period")
    }

    return ExerciseStateInfo(cardId, ExerciseId.fromValue(exercise), DateTime(due), period)
}

fun writeSRStateToJson(state: ExerciseStateInfo, json: JsonGenerator) {
    json.writeStartObject()

    json.writeNumberField(FIELD_CARD_ID, state.cardId)
    json.writeStringField(FIELD_EXERICSE_ID, state.exerciseId.value)
    json.writeNumberField(FIELD_DUE, state.due.timespamp)
    json.writeNumberField(FIELD_PERIOD, state.period)

    json.writeEndObject()
}