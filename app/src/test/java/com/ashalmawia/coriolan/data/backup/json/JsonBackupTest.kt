package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.Backup
import com.ashalmawia.coriolan.data.backup.BackupableRepository
import junit.framework.Assert.assertEquals
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
        assertEquals(expected.allExpressions(0, 500), actual.allExpressions(0, 500))
        assertEquals(expected.allCards(0, 500), actual.allCards(0, 500))
        assertEquals(expected.allDecks(0, 500), actual.allDecks(0, 500))
        assertEquals(expected.allCardStates(0, 500), actual.allCardStates(0, 500))
    }
}