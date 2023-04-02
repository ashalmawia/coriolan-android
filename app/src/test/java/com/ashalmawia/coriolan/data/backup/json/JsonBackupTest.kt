package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.Backup
import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.backup.CardInfo
import com.ashalmawia.coriolan.data.backup.ExerciseStateInfo
import com.ashalmawia.coriolan.data.backup.DeckInfo
import com.ashalmawia.coriolan.data.backup.DomainInfo
import com.ashalmawia.coriolan.data.backup.LanguageInfo
import com.ashalmawia.coriolan.data.backup.TermInfo
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.model.Extras
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

    @Test
    fun test__emptyRepository() {
        // given
        val repo = createEmptyRepo()

        // then
        test(repo, backup)
    }

    @Test
    fun test__singleSRStateExercise() {
        // given
        val repo = createNonEmptyRepo()

        // then
        test(repo, backup)
    }

    @Test
    fun test__multipleSRStateExercises() {
        // given
        val repo = createNonEmptyRepo()

        // then
        test(repo, backup)
    }

    @Test
    fun test__multipleSRStateExercisesAndSomeWithoutState() {
        // given
        val repo = createNonEmptyRepo()

        // then
        test(repo, backup)
    }

    @Test
    fun test__applyEmptyToANonEmpty() {
        // given
        val repo = createEmptyRepo()
        val outRepo = createNonEmptyRepo()

        // then
        test(repo, backup, outRepo)
    }

    @Test
    fun test__applyNonEmptyToANonEmpty() {
        // given
        val repo = createNonEmptyRepo()
        val outRepo = createNonEmptyRepo()

        // then
        test(repo, backup, outRepo)
    }

    @Test
    fun test__smallPage() {
        // given
        val backup = JsonBackup(2)
        val repo = createNonEmptyRepo()

        // then
        test(repo, backup)
    }

    @Test
    fun test__mediumPage() {
        // given
        val backup = JsonBackup(5)
        val repo = createNonEmptyRepo()

        // then
        test(repo, backup)
    }

    @Test
    fun test__bigPage() {
        // given
        val backup = JsonBackup(50)
        val repo = createNonEmptyRepo()

        // then
        test(repo, backup)
    }

    private fun test(
            repo: BackupableRepository,
            backup: Backup,
            outRepo: BackupableRepository = createEmptyRepo()
    ) {
        // when
        val output = ByteArrayOutputStream()

        // when
        backup.create(repo, output)

        println(output.toString())

        // given
        val input = ByteArrayInputStream(output.toByteArray())

        // when
        backup.restoreFrom(input, outRepo)

        // then
        assertRepoEquals(repo, outRepo)
    }

    private fun assertRepoEquals(expected: BackupableRepository, actual: BackupableRepository) {
        assertEquals(expected.allLanguages(0, 500), actual.allLanguages(0, 500))
        assertEquals(expected.allDomains(0, 500), actual.allDomains(0, 500))
        assertEquals(expected.allTerms(0, 500), actual.allTerms(0, 500))
        assertEquals(expected.allCards(0, 500), actual.allCards(0, 500))
        assertEquals(expected.allDecks(0, 500), actual.allDecks(0, 500))
        assertEquals(expected.allCardStates(0, 500), actual.allCardStates(0, 500))
    }

    @Test
    fun testLegacyBackup() {
        // given
        val repo = createNonEmptyRepo()
        val languages = listOf(
                LanguageInfo(1, "Английский"),
                LanguageInfo(2, "Русский"))
        val domains = listOf(
                DomainInfo(1, "", 1, 2)
        )
        val terms = listOf(
                TermInfo(1, "get", 1, Extras("get")),
                TermInfo(2, "получать", 2, Extras.empty()),
                TermInfo(3, "зарабатывать", 2, Extras.empty()),
                TermInfo(4, "добиваться", 2, Extras.empty()),
                TermInfo(5, "earn", 1, Extras("/ɜːn ɜːrn/")),
                TermInfo(6, "climb", 1, Extras.empty()),
                TermInfo(7, "карабкаться", 2, Extras.empty()),
        )
        val cards = listOf(
                CardInfo(1, 1, 1, 1, listOf(2, 3, 4)),
                CardInfo(2, 1, 1, 2, listOf(1)),
                CardInfo(3, 1, 1, 3, listOf(1, 5)),
                CardInfo(4, 1, 1, 4, listOf(1)),
                CardInfo(5, 1, 1, 5, listOf(3)),
                CardInfo(6, 1, 1, 6, listOf(7)),
                CardInfo(7, 1, 1, 7, listOf(6)),
        )
        val decks = listOf(
                DeckInfo(1, 1, "Default")
        )
        val states = listOf(
                ExerciseStateInfo(1, ExerciseId.FLASHCARDS, DateTime(1680400800000), 1),
                ExerciseStateInfo(3, ExerciseId.FLASHCARDS, DateTime(1680314400000), -1),
                ExerciseStateInfo(5, ExerciseId.FLASHCARDS, DateTime(1680400800000), 1),
        )

        // when
        backup.restoreFrom(
                legacyBackup.byteInputStream(),
                repo
        )

        // then
        assertEquals(languages, repo.allLanguages(0, 500))
        assertEquals(domains, repo.allDomains(0, 500))
        assertEquals(decks, repo.allDecks(0, 500))
        assertEquals(terms, repo.allTerms(0, 500))
        assertEquals(cards, repo.allCards(0, 500))
        assertEquals(states, repo.allCardStates(0, 500))
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