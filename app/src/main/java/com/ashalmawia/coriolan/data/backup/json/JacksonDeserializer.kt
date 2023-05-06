package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.*
import com.fasterxml.jackson.core.JsonParser

interface JacksonDeserializer {

    fun readLanguage(json: JsonParser): LanguageInfo

    fun readDomain(json: JsonParser): DomainInfo

    fun readTerm(json: JsonParser): TermInfo

    fun readTermExtra(json: JsonParser): TermExtraInfo

    fun readCard(json: JsonParser): CardInfo

    fun readExerciseState(json: JsonParser): LearningProgressInfo

    fun readDeck(json: JsonParser): DeckInfo

    companion object {
        fun instance(): JacksonDeserializer = JacksonDeserializerImpl()
    }
}