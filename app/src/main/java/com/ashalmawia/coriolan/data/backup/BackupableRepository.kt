package com.ashalmawia.coriolan.data.backup

import org.joda.time.DateTime

interface BackupableRepository {

    fun beginTransaction()

    fun commitTransaction()

    fun rollbackTransaction()

    fun allLanguages(offset: Int, limit: Int): List<LanguageInfo>

    fun allDomains(offset: Int, limit: Int): List<DomainInfo>

    fun allExpressions(offset: Int, limit: Int): List<ExpressionInfo>

    fun allExpressionExtras(offset: Int, limit: Int): List<ExpressionExtraInfo>

    fun allCards(offset: Int, limit: Int): List<CardInfo>

    fun allDecks(offset: Int, limit: Int): List<DeckInfo>

    fun allCardStates(offset: Int, limit: Int): List<CardStateInfo>

    fun clearAll()

    fun writeLanguages(languages: List<LanguageInfo>)

    fun writeDomains(domains: List<DomainInfo>)

    fun writeExpressions(expressions: List<ExpressionInfo>)

    fun writeExpressionExtras(extras: List<ExpressionExtraInfo>)

    fun writeCards(cards: List<CardInfo>)

    fun writeDecks(decks: List<DeckInfo>)

    fun writeCardStates(states: List<CardStateInfo>)

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

data class ExpressionInfo(
        val id: Long,
        val value: String,
        val languageId: Long)

data class ExpressionExtraInfo(
        val id: Long,
        val expressionId: Long,
        val type: Int,
        val value: String)

data class CardInfo(
        val id: Long,
        val deckId: Long,
        val domainId: Long,
        val originalId: Long,
        val translationIds: List<Long>)

data class DeckInfo(
        val id: Long,
        val domainId: Long,
        val name: String)

data class CardStateInfo(
        val cardId: Long,
        val due: DateTime,
        val period: Int)