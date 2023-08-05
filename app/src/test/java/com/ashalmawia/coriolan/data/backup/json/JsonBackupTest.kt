package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.Backup
import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.backup.LearningProgressInfo
import com.ashalmawia.coriolan.data.backup.LanguageInfo
import com.ashalmawia.coriolan.data.backup.TermInfo
import com.ashalmawia.coriolan.data.logbook.BackupableLogbook
import com.ashalmawia.coriolan.model.CardType
import org.junit.Assert.*
import org.joda.time.DateTime
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@RunWith(JUnit4::class)
abstract class JsonBackupTest {

    private val backup: Backup = JsonBackup()
    
    protected abstract fun createEmptyRepo(): BackupableRepository
    protected abstract fun createNonEmptyRepo(): BackupableRepository

    protected abstract fun createEmptyLogbook(): BackupableLogbook
    protected abstract fun createNonEmptyLogbook(): BackupableLogbook

    @Test
    fun test__emptyRepository() {
        // given
        val repo = createEmptyRepo()
        val logbook = createEmptyLogbook()

        // then
        test(repo, logbook, backup)
    }

    @Test
    fun test__singleExercise() {
        // given
        val repo = createNonEmptyRepo()
        val logbook = createNonEmptyLogbook()

        // then
        test(repo, logbook, backup)
    }

    @Test
    fun test__multipleExercises() {
        // given
        val repo = createNonEmptyRepo()
        val logbook = createNonEmptyLogbook()

        // then
        test(repo, logbook, backup)
    }

    @Test
    fun test__multipleExercisesAndSomeWithoutState() {
        // given
        val repo = createNonEmptyRepo()
        val logbook = createNonEmptyLogbook()

        // then
        test(repo, logbook, backup)
    }

    @Test
    fun test__applyEmptyToANonEmpty() {
        // given
        val repo = createEmptyRepo()
        val outRepo = createNonEmptyRepo()

        val logbook = createEmptyLogbook()
        val outLogbook = createNonEmptyLogbook()

        // then
        test(repo, logbook, backup, outRepo, outLogbook)
    }

    @Test
    fun test__applyNonEmptyToANonEmpty() {
        // given
        val repo = createNonEmptyRepo()
        val outRepo = createNonEmptyRepo()

        val logbook = createNonEmptyLogbook()
        val outLogbook = createNonEmptyLogbook()

        // then
        test(repo, logbook, backup, outRepo, outLogbook)
    }

    @Test
    fun test__smallPage() {
        // given
        val backup = JsonBackup(2)
        val repo = createNonEmptyRepo()
        val logbook = createNonEmptyLogbook()

        // then
        test(repo, logbook, backup)
    }

    @Test
    fun test__mediumPage() {
        // given
        val backup = JsonBackup(5)
        val repo = createNonEmptyRepo()
        val logbook = createNonEmptyLogbook()

        // then
        test(repo, logbook, backup)
    }

    @Test
    fun test__bigPage() {
        // given
        val backup = JsonBackup(50)
        val repo = createNonEmptyRepo()
        val logbook = createNonEmptyLogbook()

        // then
        test(repo, logbook, backup)
    }

    private fun test(
            repo: BackupableRepository,
            logbook: BackupableLogbook,
            backup: Backup,
            outRepo: BackupableRepository = createEmptyRepo(),
            outLogbook: BackupableLogbook = createEmptyLogbook()
    ) {
        // when
        val output = ByteArrayOutputStream()

        // when
        backup.create(repo, logbook, output)

        println(output.toString())

        // given
        val input = ByteArrayInputStream(output.toByteArray())

        // when
        backup.restoreFrom(input, outRepo, outLogbook)

        // then
        assertRepoEquals(repo, outRepo)
        assertLogbookEquals(logbook, outLogbook)
    }

    private fun assertRepoEquals(expected: BackupableRepository, actual: BackupableRepository) {
        assertEquals(expected.allLanguages(0, 500), actual.allLanguages(0, 500))
        assertEquals(expected.allDomains(0, 500), actual.allDomains(0, 500))
        assertEquals(expected.allTerms(0, 500), actual.allTerms(0, 500))
        assertEquals(expected.allCards(0, 500), actual.allCards(0, 500))
        assertEquals(expected.allDecks(0, 500), actual.allDecks(0, 500))
        assertEquals(expected.allExerciseStates(0, 500), actual.allExerciseStates(0, 500))
    }

    private fun assertLogbookEquals(expected: BackupableLogbook, actual: BackupableLogbook) {
        assertEquals(expected.exportAllData(0, 500), actual.exportAllData(0, 500))
    }

    @Test
    fun testLegacyBackup() {
        // given
        val repo = createNonEmptyRepo()
        val languages = listOf(
                LanguageInfo(1, "Английский"),
                LanguageInfo(2, "Русский"))
        val domains = listOf(
                domainInfo(1, "", 1, 2)
        )
        val terms = listOf(
                TermInfo(1, "get", 1, "get"),
                TermInfo(2, "получать", 2, null),
                TermInfo(3, "зарабатывать", 2, null),
                TermInfo(4, "добиваться", 2, null),
                TermInfo(5, "earn", 1, "/ɜːn ɜːrn/"),
                TermInfo(6, "climb", 1, null),
                TermInfo(7, "карабкаться", 2, null),
        )
        val cards = listOf(
                cardInfo(1, 1, 1, 1, listOf(2, 3, 4), CardType.FORWARD),
                cardInfo(2, 1, 1, 2, listOf(1), CardType.REVERSE),
                cardInfo(3, 1, 1, 3, listOf(1, 5), CardType.REVERSE),
                cardInfo(4, 1, 1, 4, listOf(1), CardType.REVERSE),
                cardInfo(5, 1, 1, 5, listOf(3), CardType.FORWARD),
                cardInfo(6, 1, 1, 6, listOf(7), CardType.FORWARD),
                cardInfo(7, 1, 1, 7, listOf(6), CardType.REVERSE),
        )
        val decks = listOf(
                deckInfo(1, 1, "Default")
        )
        val states = listOf(
                learningProgressInfo(1, DateTime(1680400800000), 1),
                learningProgressInfo(3, DateTime(1680314400000), -1),
                learningProgressInfo(5, DateTime(1680400800000), 1),
        )

        val outLogbook = createNonEmptyLogbook()

        // when
        backup.restoreFrom(
                legacyBackup.byteInputStream(),
                repo,
                outLogbook
        )

        // then
        assertEquals(languages, repo.allLanguages(0, 500))
        assertEquals(domains, repo.allDomains(0, 500))
        assertEquals(decks, repo.allDecks(0, 500))
        assertEquals(terms, repo.allTerms(0, 500))
        assertEquals(cards, repo.allCards(0, 500))
        assertEquals(states, repo.allExerciseStates(0, 500))

        assertTrue(outLogbook.exportAllData(0, 500).isEmpty())
    }
}

private val legacyBackup = """
{
  "cards": [
    {
      "deck_id": 1, 
      "domain_id": 1, 
      "id": 1, 
      "original_id": 1, 
      "translations": [
        2, 
        3, 
        4
      ]
    }, 
    {
      "deck_id": 1, 
      "domain_id": 1, 
      "id": 2, 
      "original_id": 2, 
      "translations": [
        1
      ]
    }, 
    {
      "deck_id": 1, 
      "domain_id": 1, 
      "id": 3, 
      "original_id": 3, 
      "translations": [
        1, 
        5
      ]
    }, 
    {
      "deck_id": 1, 
      "domain_id": 1, 
      "id": 4, 
      "original_id": 4, 
      "translations": [
        1
      ]
    }, 
    {
      "deck_id": 1, 
      "domain_id": 1, 
      "id": 5, 
      "original_id": 5, 
      "translations": [
        3
      ]
    }, 
    {
      "deck_id": 1, 
      "domain_id": 1, 
      "id": 6, 
      "original_id": 6, 
      "translations": [
        7
      ]
    }, 
    {
      "deck_id": 1, 
      "domain_id": 1, 
      "id": 7, 
      "original_id": 7, 
      "translations": [
        6
      ]
    }
  ], 
  "decks": [
    {
      "domain_id": 1, 
      "id": 1, 
      "name": "Default"
    }
  ], 
  "domains": [
    {
      "id": 1, 
      "name": "", 
      "orig_lang_id": 1, 
      "trans_lang_id": 2
    }
  ], 
  "expression_extras": [
    {
      "expression_id": 1, 
      "id": 1, 
      "type": 1, 
      "value": "get"
    }, 
    {
      "expression_id": 5, 
      "id": 2, 
      "type": 1, 
      "value": "/ɜːn ɜːrn/"
    }
  ], 
  "expressions": [
    {
      "id": 1, 
      "lang_id": 1, 
      "value": "get"
    }, 
    {
      "id": 2, 
      "lang_id": 2, 
      "value": "получать"
    }, 
    {
      "id": 3, 
      "lang_id": 2, 
      "value": "зарабатывать"
    }, 
    {
      "id": 4, 
      "lang_id": 2, 
      "value": "добиваться"
    }, 
    {
      "id": 5, 
      "lang_id": 1, 
      "value": "earn"
    }, 
    {
      "id": 6, 
      "lang_id": 1, 
      "value": "climb"
    }, 
    {
      "id": 7, 
      "lang_id": 2, 
      "value": "карабкаться"
    }
  ], 
  "languages": [
    {
      "id": 1, 
      "value": "Английский"
    }, 
    {
      "id": 2, 
      "value": "Русский"
    }
  ], 
  "sr_state": {
    "simple": [
      {
        "due": 1680400800000, 
        "id": 1, 
        "period": 1
      }, 
      {
        "due": 1680314400000, 
        "id": 3, 
        "period": -1
      }, 
      {
        "due": 1680400800000, 
        "id": 5, 
        "period": 1
      }
    ]
  }
}

""".trimIndent()