package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.CardInfo
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken

private const val FIELD_ID = "id"
private const val FIELD_DECK_ID = "deck_id"
private const val FIELD_DOMAIN_ID = "domain_id"
private const val FIELD_ORIGINAL_ID = "original_id"
private const val FIELD_TRANSLATIONS = "translations"

fun readCardFromJson(json: JsonParser): CardInfo {
    var id: Long? = null
    var deckId: Long? = null
    var domainId: Long? = null
    var originalId: Long? = null
    var translations: List<Long>? = null

    while (json.nextToken() != JsonToken.END_OBJECT) {
        when (json.currentName) {
            FIELD_ID -> {
                json.nextToken()
                id = json.longValue
            }
            FIELD_DECK_ID -> {
                json.nextToken()
                deckId = json.longValue
            }
            FIELD_DOMAIN_ID -> {
                json.nextToken()
                domainId = json.longValue
            }
            FIELD_ORIGINAL_ID -> {
                json.nextToken()
                originalId = json.longValue
            }
            FIELD_TRANSLATIONS -> {
                json.nextToken()

                val list = mutableListOf<Long>()
                while (json.nextToken() != JsonToken.END_ARRAY) {
                    list.add(json.longValue)
                }

                translations = list
            }
        }
    }

    if (id == null || deckId == null || domainId == null || originalId == null || translations == null) {
        throw JsonDeserializationException("failed to deserialize card, id $id, deckId $deckId, " +
                "domainId $domainId, originalId $originalId, translations $translations")
    }
    return CardInfo(id, deckId, domainId, originalId, translations)
}

fun writeCardToJson(card: CardInfo, json: JsonGenerator) {
    json.writeStartObject()

    json.writeNumberField(FIELD_ID, card.id)
    json.writeNumberField(FIELD_DECK_ID, card.deckId)
    json.writeNumberField(FIELD_DOMAIN_ID, card.domainId)
    json.writeNumberField(FIELD_ORIGINAL_ID, card.originalId)

    json.writeFieldName(FIELD_TRANSLATIONS)
    json.writeArray(card.translationIds.toLongArray(), 0, card.translationIds.size)

    json.writeEndObject()
}