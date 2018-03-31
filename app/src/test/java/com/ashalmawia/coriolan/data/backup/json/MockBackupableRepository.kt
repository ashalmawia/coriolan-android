package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.*
import com.ashalmawia.coriolan.learning.exercise.MockExerciseDescriptor
import com.ashalmawia.coriolan.learning.scheduler.StateType
import com.ashalmawia.coriolan.learning.scheduler.today
import com.ashalmawia.coriolan.model.ExpressionType
import junit.framework.Assert.assertEquals
import java.util.*
import kotlin.math.min

class MockBackupableRepository(
        langauges: List<LanguageInfo>,
        domains: List<DomainInfo>,
        expressions: List<ExpressionInfo>,
        cards: List<CardInfo>,
        decks: List<DeckInfo>,
        srstates: Map<String, List<SRStateInfo>>
) : BackupableRepository {

    val languages = langauges.toMutableList()
    val domains = domains.toMutableList()
    val expressions = expressions.toMutableList()
    val cards = cards.toMutableList()
    val decks = decks.toMutableList()
    val srstates = srstates.mapValues { it.value.toMutableList() }.toMutableMap()

    fun exercises() = srstates.keys.map { MockExerciseDescriptor(it, StateType.SR_STATE) }

    override fun allLanguages(offset: Int, limit: Int): List<LanguageInfo>
            = languages.subList(min(offset, languages.size), min(offset + limit, languages.size))

    override fun allDomains(offset: Int, limit: Int): List<DomainInfo>
            = domains.subList(min(offset, domains.size), min(offset + limit, domains.size))

    override fun allExpressions(offset: Int, limit: Int): List<ExpressionInfo>
            = expressions.subList(min(offset, expressions.size), min(offset + limit, expressions.size))

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

    override fun writeCards(cards: List<CardInfo>) {
        this.cards.addAll(cards)
    }

    override fun writeDecks(decks: List<DeckInfo>) {
        this.decks.addAll(decks)
    }

    override fun writeSRStates(exerciseId: String, states: List<SRStateInfo>) {
        this.srstates.getOrPut(exerciseId, { mutableListOf() }).addAll(states)
    }

    companion object {
        fun empty(): MockBackupableRepository {
            return MockBackupableRepository(
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyMap()
            )
        }

        fun random(srStateExercisesCount: Int): MockBackupableRepository {
            val languages = listOf(
                    LanguageInfo(1L, "English"),
                    LanguageInfo(2L, "Russian"),
                    LanguageInfo(3L, "French")
            )
            val domains = listOf(
                    DomainInfo(1L, "English", 1L, 2L),
                    DomainInfo(2L, "French", 3L, 2L)
            )

            val exressions = listOf(
                    // English
                    ExpressionInfo(1L, "shrimp", ExpressionType.WORD, 1L),
                    ExpressionInfo(2L, "rocket", ExpressionType.WORD, 1L),
                    ExpressionInfo(3L, "spring", ExpressionType.WORD, 1L),
                    ExpressionInfo(4L, "summer", ExpressionType.WORD, 1L),
                    ExpressionInfo(5L, "victory", ExpressionType.WORD, 1L),
                    ExpressionInfo(6L, "march", ExpressionType.WORD, 1L),

                    // Russian
                    ExpressionInfo(7L, "креветка", ExpressionType.WORD, 2L),
                    ExpressionInfo(8L, "ракета", ExpressionType.WORD, 2L),
                    ExpressionInfo(9L, "источник", ExpressionType.WORD, 2L),
                    ExpressionInfo(10L, "весна", ExpressionType.WORD, 2L),
                    ExpressionInfo(11L, "пружина", ExpressionType.WORD, 2L),
                    ExpressionInfo(12L, "лето", ExpressionType.WORD, 2L),
                    ExpressionInfo(13L, "победа", ExpressionType.WORD, 2L),
                    ExpressionInfo(14L, "март", ExpressionType.WORD, 2L),
                    ExpressionInfo(15L, "марш", ExpressionType.WORD, 2L),

                    // French
                    ExpressionInfo(16L, "ameloirer", ExpressionType.WORD, 3L),
                    ExpressionInfo(17L, "chercher", ExpressionType.WORD, 3L),
                    ExpressionInfo(18L, "voisin", ExpressionType.WORD, 3L),

                    // Russian
                    ExpressionInfo(19L, "улучшать", ExpressionType.WORD, 2L),
                    ExpressionInfo(20L, "искать", ExpressionType.WORD, 2L),
                    ExpressionInfo(21L, "сосед", ExpressionType.WORD, 2L)
            )

            val cards = listOf(
                    CardInfo(1L, 1L, 1L, 1L, listOf(7L)),
                    CardInfo(2L, 1L, 1L, 2L, listOf(8L)),
                    CardInfo(3L, 2L, 1L, 3L, listOf(9L, 10L, 11L)),
                    CardInfo(4L, 1L, 1L, 4L, listOf(12L)),
                    CardInfo(5L, 2L, 1L, 5L, listOf(13L)),
                    CardInfo(6L, 1L, 1L, 6L, listOf(14L, 15L)),

                    CardInfo(7L, 1L, 1L, 7L, listOf(1L)),
                    CardInfo(8L, 1L, 1L, 8L, listOf(2L)),
                    CardInfo(9L, 2L, 1L, 9L, listOf(3L)),
                    CardInfo(10L, 2L, 1L, 10L, listOf(3L)),
                    CardInfo(11L, 2L, 1L, 11L, listOf(3L)),
                    CardInfo(12L, 1L, 1L, 12L, listOf(4L)),
                    CardInfo(13L, 2L, 1L, 13L, listOf(5L)),
                    CardInfo(14L, 1L, 1L, 14L, listOf(6L)),
                    CardInfo(15L, 1L, 1L, 15L, listOf(6L)),

                    CardInfo(16L, 3L, 2L, 16L, listOf(19L)),
                    CardInfo(17L, 3L, 2L, 17L, listOf(20L)),
                    CardInfo(18L, 3L, 2L, 18L, listOf(21L)),

                    CardInfo(19L, 3L, 2L, 19L, listOf(16L)),
                    CardInfo(20L, 3L, 2L, 20L, listOf(17L)),
                    CardInfo(21L, 3L, 2L, 21L, listOf(18L))
            )

            val decks = listOf(
                    DeckInfo(1L, 1L, "Basic English"),
                    DeckInfo(2L, 1L, "Advanced"),
                    DeckInfo(3L, 2L, "Default")
            )

            val srstates = mutableMapOf<String, List<SRStateInfo>>()
            val random = Random()
            (0 until srStateExercisesCount).forEach {
                srstates["exercise_$it"] = listOf(
                        SRStateInfo(random.nextLong() % cards.size, today().minusDays(random.nextInt(500)), random.nextInt(500)),
                        SRStateInfo(random.nextLong() % cards.size, today().minusDays(random.nextInt(500)), random.nextInt(500)),
                        SRStateInfo(random.nextLong() % cards.size, today().plusDays(random.nextInt(500)), random.nextInt(500)),
                        SRStateInfo(random.nextLong() % cards.size, today().minusDays(random.nextInt(500)), random.nextInt(500)),
                        SRStateInfo(random.nextLong() % cards.size, today().minusDays(random.nextInt(500)), random.nextInt(500)),
                        SRStateInfo(random.nextLong() % cards.size, today().minusDays(random.nextInt(500)), random.nextInt(500)),
                        SRStateInfo(random.nextLong() % cards.size, today().plusDays(random.nextInt(500)), random.nextInt(500)),
                        SRStateInfo(random.nextLong() % cards.size, today(), random.nextInt(500)),
                        SRStateInfo(random.nextLong() % cards.size, today().minusDays(random.nextInt(500)), random.nextInt(500)),
                        SRStateInfo(random.nextLong() % cards.size, today(), random.nextInt(500))
                )
            }

            return MockBackupableRepository(
                    languages,
                    domains,
                    exressions,
                    cards,
                    decks,
                    srstates
            )
        }

        fun assertEquals(expected: MockBackupableRepository, actual: MockBackupableRepository) {
            assertEquals(expected.languages, actual.languages)
            assertEquals(expected.domains, actual.domains)
            assertEquals(expected.expressions, actual.expressions)
            assertEquals(expected.cards, actual.cards)
            assertEquals(expected.decks, actual.decks)
            assertEquals(expected.srstates, actual.srstates)
        }
    }
}