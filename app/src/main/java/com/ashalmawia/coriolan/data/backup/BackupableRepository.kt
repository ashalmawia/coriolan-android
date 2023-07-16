package com.ashalmawia.coriolan.data.backup

import com.ashalmawia.coriolan.model.CardType
import org.joda.time.DateTime

interface BackupableRepository : BackupableEntity {

    fun allLanguages(offset: Int, limit: Int): List<LanguageInfo>

    fun allDomains(offset: Int, limit: Int): List<DomainInfo>

    fun allTerms(offset: Int, limit: Int): List<TermInfo>

    fun allCards(offset: Int, limit: Int): List<CardInfo>

    fun allDecks(offset: Int, limit: Int): List<DeckInfo>

    fun allExerciseStates(offset: Int, limit: Int): List<LearningProgressInfo>

    fun writeLanguages(languages: List<LanguageInfo>)

    fun writeDomains(domains: List<DomainInfo>)

    fun writeTerms(terms: List<TermInfo>)

    fun writeCards(cards: List<CardInfo>)

    fun writeDecks(decks: List<DeckInfo>)

    fun writeExerciseStates(states: List<LearningProgressInfo>)

    fun hasAtLeastOneCard(): Boolean
}

data class LanguageInfo(
        val id: Long,
        val value: String
)

data class DomainInfo(
        val id: Long,
        val name: String,
        val origLangId: Long,
        val transLangId: Long)

data class TermInfo(
        val id: Long,
        val value: String,
        val languageId: Long,
        val transcription: String?)

data class TermExtraInfo(
        val id: Long,
        val termId: Long,
        val type: Int,
        val value: String)

data class CardInfo(
        val id: Long,
        val deckId: Long,
        val domainId: Long,
        val originalId: Long,
        val translationIds: List<Long>,
        var cardType: CardType?)

data class DeckInfo(
        val id: Long,
        val domainId: Long,
        val name: String)

data class LearningProgressInfo(
        val cardId: Long,
        val due: DateTime,
        val interval: Int)