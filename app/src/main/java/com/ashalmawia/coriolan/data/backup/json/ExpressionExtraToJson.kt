package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.ExpressionExtraInfo
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken

private const val FIELD_ID = "id"
private const val FIELD_EXPRESSION_ID = "expression_id"
private const val FIELD_TYPE = "type"
private const val FIELD_VALUE = "value"

fun readExpressionExtraFromJson(json: JsonParser): ExpressionExtraInfo {
    var id: Long? = null
    var expressionId: Long? = null
    var type: Int? = null
    var value: String? = null

    while (json.nextToken() != JsonToken.END_OBJECT) {
        when (json.currentName) {
            FIELD_ID -> {
                json.nextToken()
                id = json.longValue
            }
            FIELD_EXPRESSION_ID -> {
                json.nextToken()
                expressionId = json.longValue
            }
            FIELD_TYPE -> {
                json.nextToken()
                type = json.intValue
            }
            FIELD_VALUE -> {
                json.nextToken()
                value = json.text
            }
        }
    }

    if (id == null || expressionId == null || type == null || value == null) {
        throw JsonDeserializationException("failed to read expression, " +
                "id $id, expressionId $expressionId, type $type value[$value]")
    }

    return ExpressionExtraInfo(id, expressionId, type, value)
}

fun writeExpressionExtraToJson(extra: ExpressionExtraInfo, json: JsonGenerator) {
    json.writeStartObject()

    json.writeNumberField(FIELD_ID, extra.id)
    json.writeNumberField(FIELD_EXPRESSION_ID, extra.expressionId)
    json.writeNumberField(FIELD_TYPE, extra.type)
    json.writeStringField(FIELD_VALUE, extra.value)

    json.writeEndObject()
}