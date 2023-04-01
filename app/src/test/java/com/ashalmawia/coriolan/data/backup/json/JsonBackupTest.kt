package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.Backup
import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.backup.CardInfo
import com.ashalmawia.coriolan.data.backup.CardStateInfo
import com.ashalmawia.coriolan.data.backup.DeckInfo
import com.ashalmawia.coriolan.data.backup.DomainInfo
import com.ashalmawia.coriolan.data.backup.LanguageInfo
import com.ashalmawia.coriolan.data.backup.TermInfo
import com.ashalmawia.coriolan.model.Extras
import junit.framework.Assert.assertEquals
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
                CardStateInfo(1, DateTime(1680400800000), 1),
                CardStateInfo(3, DateTime(1680314400000), -1),
                CardStateInfo(5, DateTime(1680400800000), 1),
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
    {"languages":[{"id":1,"value":"Английский"},{"id":2,"value":"Русский"}],"domains":[{"id":1,"name":"","orig_lang_id":1,"trans_lang_id":2}],"expressions":[{"id":1,"value":"get","lang_id":1},{"id":2,"value":"получать","lang_id":2},{"id":3,"value":"зарабатывать","lang_id":2},{"id":4,"value":"добиваться","lang_id":2},{"id":5,"value":"earn","lang_id":1},{"id":6,"value":"climb","lang_id":1},{"id":7,"value":"карабкаться","lang_id":2}],"expression_extras":[{"id":1,"expression_id":1,"type":1,"value":"get"},{"id":2,"expression_id":5,"type":1,"value":"/ɜːn ɜːrn/"}],"decks":[{"id":1,"domain_id":1,"name":"Default"}],"cards":[{"id":1,"deck_id":1,"domain_id":1,"original_id":1,"translations":[2,3,4]},{"id":2,"deck_id":1,"domain_id":1,"original_id":2,"translations":[1]},{"id":3,"deck_id":1,"domain_id":1,"original_id":3,"translations":[1,5]},{"id":4,"deck_id":1,"domain_id":1,"original_id":4,"translations":[1]},{"id":5,"deck_id":1,"domain_id":1,"original_id":5,"translations":[3]},{"id":6,"deck_id":1,"domain_id":1,"original_id":6,"translations":[7]},{"id":7,"deck_id":1,"domain_id":1,"original_id":7,"translations":[6]}],"sr_state":{"simple":[{"id":1,"due":1680400800000,"period":1},{"id":3,"due":1680314400000,"period":-1},{"id":5,"due":1680400800000,"period":1}]}}
""".trimIndent()