package com.ashalmawia.coriolan.data.backup

import com.ashalmawia.coriolan.data.backup.json.JsonBackup
import com.ashalmawia.coriolan.data.backup.json.JsonBackupTestData
import com.ashalmawia.coriolan.data.storage.provideHelper
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteBackupHelper
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.MockExercisesRegistry
import com.ashalmawia.coriolan.learning.StateType
import com.ashalmawia.coriolan.util.OpenForTesting
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

@RunWith(RobolectricTestRunner::class)
class BackupableRepositoryTransactionTest {

    val exercises = MockExercisesRegistry()
    val realRepo = SqliteBackupHelper(RuntimeEnvironment.application, exercises, provideHelper(exercises))

    val backup: Backup = JsonBackup()

    @Test
    fun testLanguages() {
        // given
        val repo = object : OpenBackupableRepostory(realRepo) {
            override fun writeLanguages(languages: List<LanguageInfo>) {
                super.writeLanguages(languages)
                throw Exception()
            }
        }

        // then
        testTransactionIsRolledBack(repo)
    }

    @Test
    fun testDomains() {
        // given
        val repo = object : OpenBackupableRepostory(realRepo) {
            override fun writeDomains(domains: List<DomainInfo>) {
                super.writeDomains(domains)
                throw Exception()
            }
        }

        // then
        testTransactionIsRolledBack(repo)
    }

    @Test
    fun testExpressions() {
        // given
        val repo = object : OpenBackupableRepostory(realRepo) {
            override fun writeExpressions(expressions: List<ExpressionInfo>) {
                super.writeExpressions(expressions)
                throw Exception()
            }
        }

        // then
        testTransactionIsRolledBack(repo)
    }

    @Test
    fun testDecks() {
        // given
        val repo = object : OpenBackupableRepostory(realRepo) {
            override fun writeDecks(decks: List<DeckInfo>) {
                super.writeDecks(decks)
                throw Exception()
            }
        }

        // then
        testTransactionIsRolledBack(repo)
    }

    @Test
    fun testCards() {
        // given
        val repo = object : OpenBackupableRepostory(realRepo) {
            override fun writeCards(cards: List<CardInfo>) {
                super.writeCards(cards)
                throw Exception()
            }
        }

        // then
        testTransactionIsRolledBack(repo)
    }

    @Test
    fun testSRStates() {
        // given
        val repo = object : OpenBackupableRepostory(realRepo) {
            override fun writeSRStates(exerciseId: String, states: List<SRStateInfo>) {
                super.writeSRStates(exerciseId, states)
                throw Exception()
            }
        }

        // then
        testTransactionIsRolledBack(repo)
    }

    private fun testTransactionIsRolledBack(repo: BackupableRepository) {

        // when
        try {
            backup.restoreFrom(provideBackupInputStream(), repo)
        } catch (e: Exception) { }

        // then
        assertEmpty(repo, exercises.allExercises())
    }

    private fun provideBackupInputStream(): InputStream = provideBackupInputStream(exercises)
}

private fun assertEmpty(repository: BackupableRepository, exercises: List<Exercise<*, *>>) {
    assertTrue(repository.allLanguages(0, 500).isEmpty())
    assertTrue(repository.allDomains(0, 500).isEmpty())
    assertTrue(repository.allDecks(0, 500).isEmpty())
    assertTrue(repository.allCards(0, 500).isEmpty())
    assertTrue(repository.allExpressions(0, 500).isEmpty())
    exercises.forEach { assertTrue(repository.allSRStates(it.stableId, 0, 500).isEmpty()) }
}

private fun provideBackupInputStream(exercises: MockExercisesRegistry): InputStream {
    val tempRepo = SqliteBackupHelper(RuntimeEnvironment.application, exercises, provideHelper(exercises))
    tempRepo.writeLanguages(JsonBackupTestData.languages)
    tempRepo.writeDomains(JsonBackupTestData.domains)
    tempRepo.writeExpressions(JsonBackupTestData.exressions)
    tempRepo.writeDecks(JsonBackupTestData.decks)
    tempRepo.writeCards(JsonBackupTestData.cards)
    exercises.allExercises().filter { it.stateType == StateType.SR_STATE }
            .forEach { tempRepo.writeSRStates(it.stableId, JsonBackupTestData.srstates) }

    val output = ByteArrayOutputStream()
    JsonBackup().create(tempRepo, exercises.allExercises(), output)

    return ByteArrayInputStream(output.toByteArray())
}

@OpenForTesting
class OpenBackupableRepostory(private val inner: BackupableRepository) : BackupableRepository {
    override fun beginTransaction() {
        inner.beginTransaction()
    }

    override fun commitTransaction() {
        inner.commitTransaction()
    }

    override fun rollbackTransaction() {
        inner.rollbackTransaction()
    }

    override fun allLanguages(offset: Int, limit: Int): List<LanguageInfo> = inner.allLanguages(offset, limit)

    override fun allDomains(offset: Int, limit: Int): List<DomainInfo> = inner.allDomains(offset, limit)

    override fun allExpressions(offset: Int, limit: Int): List<ExpressionInfo> = inner.allExpressions(offset, limit)

    override fun allCards(offset: Int, limit: Int): List<CardInfo> = inner.allCards(offset, limit)

    override fun allDecks(offset: Int, limit: Int): List<DeckInfo> = inner.allDecks(offset, limit)

    override fun allSRStates(exerciseId: String, offset: Int, limit: Int): List<SRStateInfo>
            = inner.allSRStates(exerciseId, offset, limit)

    override fun clearAll() = inner.clearAll()

    override fun writeLanguages(languages: List<LanguageInfo>) = inner.writeLanguages(languages)

    override fun writeDomains(domains: List<DomainInfo>) = inner.writeDomains(domains)

    override fun writeExpressions(expressions: List<ExpressionInfo>) = inner.writeExpressions(expressions)

    override fun writeCards(cards: List<CardInfo>) = inner.writeCards(cards)

    override fun writeDecks(decks: List<DeckInfo>) = inner.writeDecks(decks)

    override fun writeSRStates(exerciseId: String, states: List<SRStateInfo>) = inner.writeSRStates(exerciseId, states)

    override fun hasAtLeastOneCard(): Boolean = inner.hasAtLeastOneCard()
}