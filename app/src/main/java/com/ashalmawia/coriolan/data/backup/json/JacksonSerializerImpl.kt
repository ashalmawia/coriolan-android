package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.*
import com.fasterxml.jackson.core.JsonGenerator

class JacksonSerializerImpl : JacksonSerializer {

    override fun writeLanguage(language: LanguageInfo, json: JsonGenerator) = writeLanguageToJson(language, json)

    override fun writeDomain(domain: DomainInfo, json: JsonGenerator) = writeDomainToJson(domain, json)

    override fun writeExpression(expression: ExpressionInfo, json: JsonGenerator) = writeExpressionToJson(expression, json)

    override fun writeCard(card: CardInfo, json: JsonGenerator) = writeCardToJson(card, json)

    override fun writeCardStateSR(state: SRStateInfo, json: JsonGenerator) = writeSRStateToJson(state, json)

    override fun writeDeck(deck: DeckInfo, json: JsonGenerator) = writeDeckToJson(deck, json)
}