package com.ashalmawia.coriolan.data.backup

import com.ashalmawia.coriolan.data.backup.json.JsonBackupTestData
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

abstract class BackupableRepositoryTest {

    protected abstract fun createRepository(): BackupableRepository

    private lateinit var repo: BackupableRepository

    private val languages = JsonBackupTestData.languages
    private val domains = JsonBackupTestData.domains
    private val terms = JsonBackupTestData.terms
    private val decks = JsonBackupTestData.decks
    private val cards = JsonBackupTestData.cards
    private val cardStates = JsonBackupTestData.cardStates

    @Before
    fun before() {
        repo = createRepository()
    }

    @Test
    fun test__languages__empty() {
        testEmpty(repo::writeLanguages, repo::allLanguages)
    }

    @Test
    fun test__languages__nonEmpty() {
        // given
        testNonEmpty(languages, repo::writeLanguages, repo::allLanguages)
    }

    @Test
    fun test__domains__empty() {
        testEmpty(repo::writeDomains, repo::allDomains)
    }

    @Test
    fun test__domains__nonEmpty() {
        // given
        repo.writeLanguages(languages)

        // then
        testNonEmpty(domains, repo::writeDomains, repo::allDomains)
    }

    @Test
    fun test__terms__empty() {
        testEmpty(repo::writeTerms, repo::allTerms)
    }

    @Test
    fun test__terms__nonEmpty() {
        // given
        repo.writeLanguages(languages)
        repo.writeDomains(domains)

        // then
        testNonEmpty(terms, repo::writeTerms, repo::allTerms)
    }

    @Test
    fun test__decks__empty() {
        testEmpty(repo::writeDecks, repo::allDecks)
    }

    @Test
    fun test__decks__nonEmpty() {
        // given
        repo.writeLanguages(languages)
        repo.writeDomains(domains)

        // then
        testNonEmpty(decks, repo::writeDecks, repo::allDecks)
    }

    @Test
    fun test__cards__empty() {
        testEmpty(repo::writeCards, repo::allCards)
    }

    @Test
    fun test__cards__nonEmpty() {
        // given
        repo.writeLanguages(languages)
        repo.writeDomains(domains)
        repo.writeTerms(terms)
        repo.writeDecks(decks)

        // then
        testNonEmpty(cards, repo::writeCards, repo::allCards)
    }

    @Test
    fun test__cardStates__empty() {
        // then
        testEmpty({ states -> repo.writeCardStates(states) }, { offset, limit -> repo.allCardStates(offset, limit)})
    }

    @Test
    fun test__cardStates__nonEmpty() {
        // given
        repo.writeLanguages(languages)
        repo.writeDomains(domains)
        repo.writeTerms(terms)
        repo.writeDecks(decks)
        repo.writeCards(cards)

        // then
        testNonEmpty(
                cardStates.sortedBy { it.cardId },
                { states -> repo.writeCardStates(states) },
                { offset, limit -> repo.allCardStates(offset, limit).sortedBy { it.cardId } }
        )
    }

    @Test
    fun test__overrideRepositoryData__emptyData() {
        // given
        repo.writeLanguages(languages)
        repo.writeDomains(domains)
        repo.writeTerms(terms)
        repo.writeDecks(decks)
        repo.writeCards(cards)
        repo.writeCardStates(cardStates)

        // when
        repo.overrideRepositoryData { }

        // then
        assertTrue(repo.allLanguages(0, 500).isEmpty())
        assertTrue(repo.allDomains(0, 500).isEmpty())
        assertTrue(repo.allTerms(0, 500).isEmpty())
        assertTrue(repo.allCards(0, 500).isEmpty())
        assertTrue(repo.allDecks(0, 500).isEmpty())
        assertTrue(repo.allCardStates(0, 500).isEmpty())
    }

    @Test
    fun test__overrideRepositoryData__nonEmptyData() {
        // given
        repo.writeLanguages(languages)
        repo.writeDomains(domains)
        repo.writeTerms(terms)
        repo.writeDecks(decks)
        repo.writeCards(cards)
        repo.writeCardStates(cardStates)

        // when
        repo.overrideRepositoryData { repo ->
            repo.writeLanguages(languages)
            repo.writeDomains(domains)
            repo.writeTerms(terms)
            repo.writeDecks(decks)
            repo.writeCards(cards)
            repo.writeCardStates(cardStates)
        }

        // then
        assertEquals(languages, repo.allLanguages(0, 500))
        assertEquals(domains, repo.allDomains(0, 500))
        assertEquals(terms, repo.allTerms(0, 500))
        assertEquals(cards, repo.allCards(0, 500))
        assertEquals(decks, repo.allDecks(0, 500))
        assertEquals(cardStates.sortedBy { it.cardId }, repo.allCardStates(0, 500).sortedBy { it.cardId })
    }

    @Test
    fun test__hasAtLeastOneCard() {
        // clean repo
        assertFalse(repo.hasAtLeastOneCard())

        // given
        repo.writeLanguages(languages)
        repo.writeDomains(domains)
        repo.writeTerms(terms)
        repo.writeDecks(decks)

        // then
        assertFalse(repo.hasAtLeastOneCard())

        // given
        repo.writeCards(cards.subList(0, 1))

        // then
        assertTrue(repo.hasAtLeastOneCard())

        // given
        repo.overrideRepositoryData { }

        // then
        assertFalse(repo.hasAtLeastOneCard())

        // given
        repo.writeLanguages(languages)
        repo.writeDomains(domains)
        repo.writeTerms(terms)
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