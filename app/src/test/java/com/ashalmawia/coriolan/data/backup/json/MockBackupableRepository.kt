package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.*
import kotlin.math.min

class MockBackupableRepository(
        langauges: List<LanguageInfo>,
        domains: List<DomainInfo>,
        terms: List<TermInfo>,
        termExtras: List<TermExtraInfo>,
        cards: List<CardInfo>,
        decks: List<DeckInfo>,
        cardStates: List<CardStateInfo>
) : BackupableRepository {

    private val languages = langauges.toMutableList()
    private val domains = domains.toMutableList()
    private val terms = terms.toMutableList()
    private val termExtras = termExtras.toMutableList()
    private val cards = cards.toMutableList()
    private val decks = decks.toMutableList()
    private val cardStates = cardStates.toMutableList()

    override fun beginTransaction() {}
    override fun commitTransaction() {}
    override fun rollbackTransaction() {}

    override fun allLanguages(offset: Int, limit: Int): List<LanguageInfo>
            = languages.subList(min(offset, languages.size), min(offset + limit, languages.size))

    override fun allDomains(offset: Int, limit: Int): List<DomainInfo>
            = domains.subList(min(offset, domains.size), min(offset + limit, domains.size))

    override fun allTerms(offset: Int, limit: Int): List<TermInfo>
            = terms.subList(min(offset, terms.size), min(offset + limit, terms.size))

    override fun allTermExtras(offset: Int, limit: Int): List<TermExtraInfo>
            = termExtras.subList(min(offset, termExtras.size), min(offset + limit, termExtras.size))

    override fun allCards(offset: Int, limit: Int): List<CardInfo>
            = cards.subList(min(offset, cards.size), min(offset + limit, cards.size))

    override fun allDecks(offset: Int, limit: Int): List<DeckInfo>
            = decks.subList(min(offset, decks.size), min(offset + limit, decks.size))

    override fun allCardStates(offset: Int, limit: Int): List<CardStateInfo> {
        return cardStates.subList(min(offset, cardStates.size), min(offset + limit, cardStates.size))
    }

    override fun clearAll() {
        languages.clear()
        domains.clear()
        terms.clear()
        termExtras.clear()
        cards.clear()
        decks.clear()
        cardStates.clear()
    }

    override fun writeLanguages(languages: List<LanguageInfo>) {
        this.languages.addAll(languages)
    }

    override fun writeDomains(domains: List<DomainInfo>) {
        this.domains.addAll(domains)
    }

    override fun writeTerms(terms: List<TermInfo>) {
        this.terms.addAll(terms)
    }

    override fun writeTermExtras(extras: List<TermExtraInfo>) {
        this.termExtras.addAll(extras)
    }

    override fun writeCards(cards: List<CardInfo>) {
        this.cards.addAll(cards)
    }

    override fun writeDecks(decks: List<DeckInfo>) {
        this.decks.addAll(decks)
    }

    override fun writeCardStates(states: List<CardStateInfo>) {
        this.cardStates.addAll(states)
    }

    override fun hasAtLeastOneCard(): Boolean {
        return cards.isNotEmpty()
    }

    companion object {
        fun empty(): MockBackupableRepository {
            return MockBackupableRepository(
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList()
            )
        }

        fun random(): MockBackupableRepository {
            return MockBackupableRepository(
                    JsonBackupTestData.languages,
                    JsonBackupTestData.domains,
                    JsonBackupTestData.terms,
                    JsonBackupTestData.termExtras,
                    JsonBackupTestData.cards,
                    JsonBackupTestData.decks,
                    JsonBackupTestData.cardStates
            )
        }
    }
}