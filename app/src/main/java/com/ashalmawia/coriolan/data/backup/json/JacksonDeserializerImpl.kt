package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.*
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class JacksonDeserializerImpl : JacksonDeserializer {

    private val objectMapper = jacksonObjectMapper()

    override fun readLanguage(json: JsonParser): LanguageInfo = readLanguageFromJson(json)

    override fun readDomain(json: JsonParser): DomainInfo = readDomainFromJson(json)

    override fun readTerm(json: JsonParser): TermInfo = readTermFromJson(json, objectMapper)

    override fun readTermExtra(json: JsonParser): TermExtraInfo = readTermExtraFromJson(json)

    override fun readCard(json: JsonParser): CardInfo = readCardFromJson(json)

    override fun readCardStateSR(json: JsonParser): ExerciseStateInfo = readSRStateFromJson(json)

    override fun readDeck(json: JsonParser): DeckInfo = readDeckFromJson(json)
}