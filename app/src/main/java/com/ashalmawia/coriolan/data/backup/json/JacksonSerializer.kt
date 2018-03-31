package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.*
import com.fasterxml.jackson.core.JsonGenerator

interface JacksonSerializer {

    fun writeLanguage(language: LanguageInfo, json: JsonGenerator)

    fun writeDomain(domain: DomainInfo, json: JsonGenerator)

    fun writeExpression(expression: ExpressionInfo, json: JsonGenerator)

    fun writeCard(card: CardInfo, json: JsonGenerator)

    fun writeCardStateSR(state: SRStateInfo, json: JsonGenerator)

    fun writeDeck(deck: DeckInfo, json: JsonGenerator)

    companion object {
        fun instance(): JacksonSerializer = JacksonSerializerImpl()
    }
}