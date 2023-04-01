package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.TermExtraInfo
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken

private const val FIELD_ID = "id"
private const val FIELD_TERM_ID = "expression_id"
private const val FIELD_TYPE = "type"
private const val FIELD_VALUE = "value"

fun readTermExtraFromJson(json: JsonParser): TermExtraInfo {
    var id: Long? = null
    var termId: Long? = null
    var type: Int? = null
    var value: String? = null

    while (json.nextToken() != JsonToken.END_OBJECT) {
        when (json.currentName) {
            FIELD_ID -> {
                json.nextToken()
                id = json.longValue
            }
            FIELD_TERM_ID -> {
                json.nextToken()
                termId = json.longValue
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

    if (id == null || termId == null || type == null || value == null) {
        throw JsonDeserializationException("failed to read term, " +
                "id $id, termId $termId, type $type value[$value]")
    }

    return TermExtraInfo(id, termId, type, value)
}