package com.ashalmawia.coriolan.data.backup

import com.ashalmawia.coriolan.learning.*
import com.ashalmawia.coriolan.learning.exercise.MockExercise
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

abstract class BackupableRepositoryTest {

    protected abstract fun createRepository(exercisesRegistry: ExercisesRegistry): BackupableRepository

    private val exercise = MockExercise("some_exercise", StateType.SR_STATE)
    private val exercises = MockExercisesRegistry(listOf(exercise))

    private val today = mockToday()

    private lateinit var repo: BackupableRepository

    private val languages = listOf(
            LanguageInfo(1L, "English"),
            LanguageInfo(2L, "Russian"),
            LanguageInfo(3L, "French"),
            LanguageInfo(4L, "Greek"),
            LanguageInfo(5L, "Chineese"),
            LanguageInfo(6L, "Polish"),
            LanguageInfo(7L, "Finnish")
    )
    private val domains = listOf(
            DomainInfo(1L, "English", 1L, 2L),
            DomainInfo(2L, "French", 3L, 2L),
            DomainInfo(3L, "Greek", 4L, 2L),
            DomainInfo(4L, "Chineese", 5L, 2L),
            DomainInfo(5L, "Polish", 6L, 2L),
            DomainInfo(6L, "Finnish", 7L, 2L)
    )
    private val expressions = listOf(
            ExpressionInfo(1L, "shrimp", 1L),
            ExpressionInfo(2L, "rocket", 1L),
            ExpressionInfo(3L, "spring", 1L),
            ExpressionInfo(4L, "summer", 1L),
            ExpressionInfo(5L, "victory", 1L),
            ExpressionInfo(6L, "march", 1L),
            ExpressionInfo(7L, "креветка", 2L),
            ExpressionInfo(8L, "ракета", 2L),
            ExpressionInfo(9L, "источник", 2L),
            ExpressionInfo(10L, "весна", 2L),
            ExpressionInfo(11L, "пружина", 2L),
            ExpressionInfo(12L, "лето", 2L),
            ExpressionInfo(13L, "победа", 2L),
            ExpressionInfo(14L, "март", 2L),
            ExpressionInfo(15L, "марш", 2L)
    )
    private val decks = listOf(
            DeckInfo(1L, 1L, "Basic English"),
            DeckInfo(2L, 1L, "Advanced"),
            DeckInfo(3L, 2L, "Default"),
            DeckInfo(4L, 1L, "Some deck"),
            DeckInfo(5L, 1L, "Advanced deck"),
            DeckInfo(6L, 2L, "Another deck"),
            DeckInfo(7L, 1L, "Topic - Travelling"),
            DeckInfo(8L, 1L, "Topic - Music"),
            DeckInfo(9L, 2L, "Topic - Sports")
    )
    private val cards = listOf(
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
            CardInfo(12L, 1L, 1L, 12L, listOf(4L))
    )

    private val srstates = listOf(
            SRStateInfo(5L, today.minusDays(10), 44),
            SRStateInfo(3L, today.minusDays(5), 52),
            SRStateInfo(12L, today.plusDays(11), 22),
            SRStateInfo(11L, today.minusDays(88), 12),
            SRStateInfo(9L, today.plusDays(23), 50),
            SRStateInfo(8L, today.minusDays(1), 1),
            SRStateInfo(6L, today, 0)
    )

    @Before
    fun before() {
        repo = createRepository(exercises)
    }

    @Test
    fun `test__languages__empty`() {
        testEmpty(repo::writeLanguages, repo::allLanguages)
    }

    @Test
    fun `test__languages__nonEmpty`() {
        // given
        testNonEmpty(languages, repo::writeLanguages, repo::allLanguages)
    }

    @Test
    fun `test__domains__empty`() {
        testEmpty(repo::writeDomains, repo::allDomains)
    }

    @Test
    fun `test__domains__nonEmpty`() {
        // given
        repo.writeLanguages(languages)

        // then
        testNonEmpty(domains, repo::writeDomains, repo::allDomains)
    }

    @Test
    fun `test__expressions__empty`() {
        testEmpty(repo::writeExpressions, repo::allExpressions)
    }

    @Test
    fun `test__expressions__nonEmpty`() {
        // given
        repo.writeLanguages(languages)
        repo.writeDomains(domains)

        // then
        testNonEmpty(expressions, repo::writeExpressions, repo::allExpressions)
    }

    @Test
    fun `test__decks__empty`() {
        testEmpty(repo::writeDecks, repo::allDecks)
    }

    @Test
    fun `test__decks__nonEmpty`() {
        // given
        repo.writeLanguages(languages)
        repo.writeDomains(domains)

        // then
        testNonEmpty(decks, repo::writeDecks, repo::allDecks)
    }

    @Test
    fun `test__cards__empty`() {
        testEmpty(repo::writeCards, repo::allCards)
    }

    @Test
    fun `test__cards__nonEmpty`() {
        // given
        repo.writeLanguages(languages)
        repo.writeDomains(domains)
        repo.writeExpressions(expressions)
        repo.writeDecks(decks)

        // then
        testNonEmpty(cards, repo::writeCards, repo::allCards)
    }

    @Test
    fun `test__srstates__empty`() {
        // given
        val exerciseId = exercise.stableId

        // then
        testEmpty({ states -> repo.writeSRStates(exerciseId, states) }, { offset, limit -> repo.allSRStates(exerciseId, offset, limit)})
    }

    @Test
    fun `test__srstates__nonEmpty`() {
        // given
        repo.writeLanguages(languages)
        repo.writeDomains(domains)
        repo.writeExpressions(expressions)
        repo.writeDecks(decks)
        repo.writeCards(cards)

        val exerciseId = exercise.stableId

        // then
        testNonEmpty(
                srstates.sortedBy { it.cardId },
                { states -> repo.writeSRStates(exerciseId, states) },
                { offset, limit -> repo.allSRStates(exerciseId, offset, limit).sortedBy { it.cardId } }
        )
    }

    @Test
    fun `test__clear`() {
        // given
        val exerciseId = exercise.stableId

        repo.writeLanguages(languages)
        repo.writeDomains(domains)
        repo.writeExpressions(expressions)
        repo.writeDecks(decks)
        repo.writeCards(cards)
        repo.writeSRStates(exerciseId, srstates)

        // when
        repo.clearAll()

        // then
        assertTrue(repo.allLanguages(0, 500).isEmpty())
        assertTrue(repo.allDomains(0, 500).isEmpty())
        assertTrue(repo.allExpressions(0, 500).isEmpty())
        assertTrue(repo.allCards(0, 500).isEmpty())
        assertTrue(repo.allDecks(0, 500).isEmpty())
        assertTrue(repo.allSRStates(exerciseId, 0, 500).isEmpty())
    }

    @Test
    fun `test__hasAtLeastOneCard`() {
        // clean repo
        assertFalse(repo.hasAtLeastOneCard())

        // given
        repo.writeLanguages(languages)
        repo.writeDomains(domains)
        repo.writeExpressions(expressions)
        repo.writeDecks(decks)

        // then
        assertFalse(repo.hasAtLeastOneCard())

        // given
        repo.writeCards(cards.subList(0, 1))

        // then
        assertTrue(repo.hasAtLeastOneCard())

        // given
        repo.clearAll()

        // then
        assertFalse(repo.hasAtLeastOneCard())

        // given
        repo.writeLanguages(languages)
        repo.writeDomains(domains)
        repo.writeExpressions(expressions)
        repo.writeDecks(decks)
        repo.writeCards(cards)

        // then
        assertTrue(repo.hasAtLeastOneCard())
    }

    private fun <T> testEmpty(writer: (List<T>) -> Unit, reader: (Int, Int) -> List<T>) {
        // given
        val entities = listOf<T>()

        // when
        writer(entities)
        val read = reader(0, 10)

        // then
        assertEquals(entities, read)
    }

    /**
     * Size of the list must be no less than 5.
     */
    private fun <T> testNonEmpty(data: List<T>, writer: (List<T>) -> Unit, reader: (Int, Int) -> List<T>) {
        // when
        writer(data)

        // no pagination
        assertEquals(data, reader(0, data.size))

        // big page
        assertEquals(data, reader(0, data.size * 2 + 1))

        // small page
        assertEquals(data.subList(0, data.size / 2), reader(0, data.size / 2))

        // offset within the list
        assertEquals(data.subList(1, 1 + 3), reader(1, 3))

        // offset partly within the list
        assertEquals(data.subList(data.size / 2, data.size), reader(data.size / 2, data.size + 3))

        // offset outside of the list
        assertEquals(emptyList<T>(), reader(data.size, 5))
    }
}