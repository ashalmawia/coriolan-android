package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.*
import com.fasterxml.jackson.core.JsonParser

class JacksonDeserializerImpl : JacksonDeserializer {

    override fun readLanguage(json: JsonParser): LanguageInfo = readLanguageFromJson(json)

    override fun readDomain(json: JsonParser): DomainInfo = readDomainFromJson(json)

    override fun readExpression(json: JsonParser): ExpressionInfo = readExpressionFromJson(json)

    override fun readExpressionExtra(json: JsonParser): ExpressionExtraInfo = readExpressionExtraFromJson(json)

    override fun readCard(json: JsonParser): CardInfo = readCardFromJson(json)

    override fun readCardStateSR(json: JsonParser): CardStateInfo = readSRStateFromJson(json)

    override fun readDeck(json: JsonParser): DeckInfo = readDeckFromJson(json)
}