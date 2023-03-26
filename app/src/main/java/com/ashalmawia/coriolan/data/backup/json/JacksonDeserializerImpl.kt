package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.*
import com.fasterxml.jackson.core.JsonParser

class JacksonDeserializerImpl : JacksonDeserializer {

    override fun readLanguage(json: JsonParser): LanguageInfo = readLanguageFromJson(json)

    override fun readDomain(json: JsonParser): DomainInfo = readDomainFromJson(json)

    override fun readTerm(json: JsonParser): TermInfo = readTermFromJson(json)

    override fun readTermExtra(json: JsonParser): TermExtraInfo = readTermExtraFromJson(json)

    override fun readCard(json: JsonParser): CardInfo = readCardFromJson(json)

    override fun readCardStateSR(json: JsonParser): CardStateInfo = readSRStateFromJson(json)

    override fun readDeck(json: JsonParser): DeckInfo = readDeckFromJson(json)
}