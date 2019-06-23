package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.*
import com.fasterxml.jackson.core.JsonParser

interface JacksonDeserializer {

    fun readLanguage(json: JsonParser): LanguageInfo

    fun readDomain(json: JsonParser): DomainInfo

    fun readExpression(json: JsonParser): ExpressionInfo

    fun readExpressionExtra(json: JsonParser): ExpressionExtraInfo

    fun readCard(json: JsonParser): CardInfo

    fun readCardStateSR(json: JsonParser): SRStateInfo

    fun readDeck(json: JsonParser): DeckInfo

    companion object {
        fun instance(): JacksonDeserializer = JacksonDeserializerImpl()
    }
}