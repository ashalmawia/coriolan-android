package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.DeckInfo
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken

private const val FIELD_ID = "id"
private const val FIELD_DOMAIN_ID = "domain_id"
private const val FIELD_NAME = "name"

fun readDeckFromJson(json: JsonParser): DeckInfo {
    var id: Long? = null
    var domainId: Long? = null
    var name: String? = null

    while (json.nextToken() != JsonToken.END_OBJECT) {
        when (json.currentName) {
            FIELD_ID -> {
                json.nextToken()
                id = json.longValue
            }
            FIELD_DOMAIN_ID -> {
                json.nextToken()
                domainId = json.longValue
            }
            FIELD_NAME -> {
                json.nextToken()
                name = json.text
            }
        }
    }

    if (id == null || domainId == null || name == null) {
        throw JsonDeserializationException("failed to deserialize deck, id $id, domainId $domainId, name $name")
    }
    return DeckInfo(id, domainId, name)
}

fun writeDeckToJson(deck: DeckInfo, json: JsonGenerator) {
    json.writeStartObject()

    json.writeNumberField(FIELD_ID, deck.id)
    json.writeNumberField(FIELD_DOMAIN_ID, deck.domainId)
    json.writeStringField(FIELD_NAME, deck.name)

    json.writeEndObject()
}