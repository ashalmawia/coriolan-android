package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.DomainInfo
import com.ashalmawia.coriolan.util.asDomainId
import com.ashalmawia.coriolan.util.asLanguageId
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken

private const val FIELD_ID = "id"
private const val FIELD_NAME = "name"
private const val FIELD_ORIGINAL_LANG_ID = "orig_lang_id"
private const val FIELD_TRANSLATION_LANG_ID = "trans_lang_id"

fun readDomainFromJson(json: JsonParser): DomainInfo {
    var id: Long? = null
    var name: String? = null
    var origLangId: Long? = null
    var transLangId: Long? = null

    while (json.nextToken() != JsonToken.END_OBJECT) {
        when (json.currentName) {
            FIELD_ID -> {
                json.nextToken()
                id = json.longValue
            }
            FIELD_NAME -> {
                json.nextToken()
                name = json.text
            }
            FIELD_ORIGINAL_LANG_ID -> {
                json.nextToken()
                origLangId = json.longValue
            }
            FIELD_TRANSLATION_LANG_ID -> {
                json.nextToken()
                transLangId = json.longValue
            }
        }
    }

    if (id == null || name == null || origLangId == null || transLangId == null) {
        throw JsonDeserializationException("failed to deserialize domain, id $id, name[$name], " +
                "origLangId $origLangId, transLangId $transLangId")
    }
    return DomainInfo(id.asDomainId(), name, origLangId.asLanguageId(), transLangId.asLanguageId())
}

fun writeDomainToJson(domain: DomainInfo, json: JsonGenerator) {
    json.writeStartObject()

    json.writeNumberField(FIELD_ID, domain.id.value)
    json.writeStringField(FIELD_NAME, domain.name)
    json.writeNumberField(FIELD_ORIGINAL_LANG_ID, domain.origLangId.value)
    json.writeNumberField(FIELD_TRANSLATION_LANG_ID, domain.transLangId.value)

    json.writeEndObject()
}