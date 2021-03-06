package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.*
import com.ashalmawia.coriolan.learning.ExercisesRegistry
import com.ashalmawia.coriolan.learning.MockExercisesRegistry
import com.ashalmawia.coriolan.learning.StateType
import kotlin.math.min

class MockBackupableRepository(
        langauges: List<LanguageInfo>,
        domains: List<DomainInfo>,
        expressions: List<ExpressionInfo>,
        expressionExtras: List<ExpressionExtraInfo>,
        cards: List<CardInfo>,
        decks: List<DeckInfo>,
        srstates: Map<String, List<SRStateInfo>>,
        private val exercises: ExercisesRegistry
) : BackupableRepository {

    private val languages = langauges.toMutableList()
    private val domains = domains.toMutableList()
    private val expressions = expressions.toMutableList()
    private val expressionExtras = expressionExtras.toMutableList()
    private val cards = cards.toMutableList()
    private val decks = decks.toMutableList()
    private val srstates = srstates.mapValues { it.value.toMutableList() }.toMutableMap()

    override fun beginTransaction() {}
    override fun commitTransaction() {}
    override fun rollbackTransaction() {}

    override fun allLanguages(offset: Int, limit: Int): List<LanguageInfo>
            = languages.subList(min(offset, languages.size), min(offset + limit, languages.size))

    override fun allDomains(offset: Int, limit: Int): List<DomainInfo>
            = domains.subList(min(offset, domains.size), min(offset + limit, domains.size))

    override fun allExpressions(offset: Int, limit: Int): List<ExpressionInfo>
            = expressions.subList(min(offset, expressions.size), min(offset + limit, expressions.size))

    override fun allExpressionExtras(offset: Int, limit: Int): List<ExpressionExtraInfo>
            = expressionExtras.subList(min(offset, expressionExtras.size), min(offset + limit, expressionExtras.size))

    override fun allCards(offset: Int, limit: Int): List<CardInfo>
            = cards.subList(min(offset, cards.size), min(offset + limit, cards.size))

    override fun allDecks(offset: Int, limit: Int): List<DeckInfo>
            = decks.subList(min(offset, decks.size), min(offset + limit, decks.size))

    override fun allSRStates(exerciseId: String, offset: Int, limit: Int): List<SRStateInfo> {
        if (!srstates.containsKey(exerciseId)) {
            return emptyList()
        }

        val list = srstates[exerciseId]!!
        return list.subList(min(offset, list.size), min(offset + limit, list.size))
    }

    override fun clearAll() {
        languages.clear()
        domains.clear()
        expressions.clear()
        expressionExtras.clear()
        cards.clear()
        decks.clear()
        srstates.clear()
    }

    override fun writeLanguages(languages: List<LanguageInfo>) {
        this.languages.addAll(languages)
    }

    override fun writeDomains(domains: List<DomainInfo>) {
        this.domains.addAll(domains)
    }

    override fun writeExpressions(expressions: List<ExpressionInfo>) {
        this.expressions.addAll(expressions)
    }

    override fun writeExpressionExtras(extras: List<ExpressionExtraInfo>) {
        this.expressionExtras.addAll(extras)
    }

    override fun writeCards(cards: List<CardInfo>) {
        this.cards.addAll(cards)
    }

    override fun writeDecks(decks: List<DeckInfo>) {
        this.decks.addAll(decks)
    }

    override fun writeSRStates(exerciseId: String, states: List<SRStateInfo>) {
        if (exercises.allExercises().find { it.stableId == exerciseId } != null) {
            this.srstates.getOrPut(exerciseId, { mutableListOf() }).addAll(states)
        }
    }

    override fun hasAtLeastOneCard(): Boolean {
        return cards.isNotEmpty()
    }

    companion object {
        fun empty(exercises: ExercisesRegistry): MockBackupableRepository {
            return MockBackupableRepository(
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyMap(),
                    exercises
            )
        }

        fun random(exercises: MockExercisesRegistry): MockBackupableRepository {
            return MockBackupableRepository(
                    JsonBackupTestData.languages,
                    JsonBackupTestData.domains,
                    JsonBackupTestData.exressions,
                    JsonBackupTestData.expressionExtras,
                    JsonBackupTestData.cards,
                    JsonBackupTestData.decks,
                    exercises.allExercises().filter { it.stateType == StateType.SR_STATE }
                            .map { it.stableId }.associate { id -> Pair(id, JsonBackupTestData.srstates) },
                    exercises
            )
        }
    }
}