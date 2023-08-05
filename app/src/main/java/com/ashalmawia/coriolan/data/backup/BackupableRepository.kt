package com.ashalmawia.coriolan.data.backup

import com.ashalmawia.coriolan.model.CardId
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.DeckId
import com.ashalmawia.coriolan.model.DomainId
import com.ashalmawia.coriolan.model.TermId
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
        val id: DomainId,
        val name: String,
        val origLangId: Long,
        val transLangId: Long)

data class TermInfo(
        val id: TermId,
        val value: String,
        val languageId: Long,
        val transcription: String?)

data class TermExtraInfo(
        val id: Long,
        val termId: TermId,
        val type: Int,
        val value: String)

data class CardInfo(
        val id: CardId,
        val deckId: DeckId,
        val domainId: DomainId,
        val originalId: TermId,
        val translationIds: List<TermId>,
        var cardType: CardType?)

data class DeckInfo(
        val id: DeckId,
        val domainId: DomainId,
        val name: String)

data class LearningProgressInfo(
        val cardId: CardId,
        val due: DateTime,
        val interval: Int)