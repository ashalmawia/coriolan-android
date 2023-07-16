package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.*
import com.ashalmawia.coriolan.data.logbook.LogbookEntryInfo
import com.fasterxml.jackson.core.JsonGenerator

class JacksonSerializerImpl : JacksonSerializer {

    override fun writeLanguage(language: LanguageInfo, json: JsonGenerator) = writeLanguageToJson(language, json)

    override fun writeDomain(domain: DomainInfo, json: JsonGenerator) = writeDomainToJson(domain, json)

    override fun writeTerm(term: TermInfo, json: JsonGenerator)
            = writeTermToJson(term, json)

    override fun writeCard(card: CardInfo, json: JsonGenerator) = writeCardToJson(card, json)

    override fun writeCardState(state: LearningProgressInfo, json: JsonGenerator) = writeExerciseStateToJson(state, json)

    override fun writeDeck(deck: DeckInfo, json: JsonGenerator) = writeDeckToJson(deck, json)

    override fun writeLogbookEntry(entry: LogbookEntryInfo, json: JsonGenerator) = writeLogbookEntryToJson(entry, json)
}