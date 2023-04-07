package com.ashalmawia.coriolan.data.backup

import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Extras
import org.joda.time.DateTime

interface BackupableRepository {

    fun allLanguages(offset: Int, limit: Int): List<LanguageInfo>

    fun allDomains(offset: Int, limit: Int): List<DomainInfo>

    fun allTerms(offset: Int, limit: Int): List<TermInfo>

    fun allCards(offset: Int, limit: Int): List<CardInfo>

    fun allDecks(offset: Int, limit: Int): List<DeckInfo>

    fun allCardStates(offset: Int, limit: Int): List<ExerciseStateInfo>

    fun writeLanguages(languages: List<LanguageInfo>)

    fun writeDomains(domains: List<DomainInfo>)

    fun writeTerms(terms: List<TermInfo>)

    fun writeCards(cards: List<CardInfo>)

    fun writeDecks(decks: List<DeckInfo>)

    fun writeCardStates(states: List<ExerciseStateInfo>)

    fun overrideRepositoryData(override: (BackupableRepository) -> Unit)

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
        val extras: Extras)

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

data class ExerciseStateInfo(
        val cardId: Long,
        val exerciseId: ExerciseId,
        val due: DateTime,
        val interval: Int)