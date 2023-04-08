package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.*
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class JacksonSerializerImpl : JacksonSerializer {

    private val objectMapper = jacksonObjectMapper()

    override fun writeLanguage(language: LanguageInfo, json: JsonGenerator) = writeLanguageToJson(language, json)

    override fun writeDomain(domain: DomainInfo, json: JsonGenerator) = writeDomainToJson(domain, json)

    override fun writeTerm(term: TermInfo, json: JsonGenerator)
            = writeTermToJson(term, json)

    override fun writeCard(card: CardInfo, json: JsonGenerator) = writeCardToJson(card, json)

    override fun writeCardState(state: ExerciseStateInfo, json: JsonGenerator) = writeExerciseStateToJson(state, json)

    override fun writeDeck(deck: DeckInfo, json: JsonGenerator) = writeDeckToJson(deck, json)
}