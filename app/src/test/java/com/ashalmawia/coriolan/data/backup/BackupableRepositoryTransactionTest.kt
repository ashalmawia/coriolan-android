package com.ashalmawia.coriolan.data.backup

import com.ashalmawia.coriolan.data.backup.json.JsonBackup
import com.ashalmawia.coriolan.data.backup.json.JsonBackupTestData
import com.ashalmawia.coriolan.data.backup.logbook.createNonEmptyLogbookWithMockData
import com.ashalmawia.coriolan.data.logbook.BackupableLogbook
import com.ashalmawia.coriolan.data.logbook.LogbookEntryInfo
import com.ashalmawia.coriolan.data.logbook.sqlite.SqliteLogbook
import com.ashalmawia.coriolan.data.storage.provideLogbookHelper
import com.ashalmawia.coriolan.data.storage.provideRepositoryHelper
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteBackupHelper
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.SQLiteMode
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

@RunWith(RobolectricTestRunner::class)
@SQLiteMode(SQLiteMode.Mode.LEGACY)
class BackupableRepositoryTransactionTest {

    private val realRepo = SqliteBackupHelper(provideRepositoryHelper())
    private val realLogbook = SqliteLogbook(provideLogbookHelper())

    private val backup: Backup = JsonBackup()

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
        testTransactionIsRolledBack(repo, realLogbook)
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
        testTransactionIsRolledBack(repo, realLogbook)
    }

    @Test
    fun testTerms() {
        // given
        val repo = object : OpenBackupableRepostory(realRepo) {
            override fun writeTerms(terms: List<TermInfo>) {
                super.writeTerms(terms)
                throw Exception()
            }
        }

        // then
        testTransactionIsRolledBack(repo, realLogbook)
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
        testTransactionIsRolledBack(repo, realLogbook)
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
        testTransactionIsRolledBack(repo, realLogbook)
    }

    @Test
    fun testExerciseStates() {
        // given
        val repo = object : OpenBackupableRepostory(realRepo) {
            override fun writeExerciseStates(states: List<LearningProgressInfo>) {
                super.writeExerciseStates(states)
                throw Exception()
            }
        }

        // then
        testTransactionIsRolledBack(repo, realLogbook)
    }

    @Test
    fun testDropAllDataRepository() {
        // given
        val repo = object : OpenBackupableRepostory(realRepo) {
            override fun dropAllData() {
                super.dropAllData()
                throw Exception()
            }
        }

        // then
        testTransactionIsRolledBack(repo, realLogbook)
    }

    @Test
    fun testDropAllDataLogbook() {
        // given
        val logbook = object : OpenBackupableLogbook(realLogbook) {
            override fun dropAllData() {
                super.dropAllData()
                throw Exception()
            }
        }

        // then
        testTransactionIsRolledBack(realRepo, logbook)
    }

    @Test
    fun testLogbook() {
        // given
        val logbook = object : OpenBackupableLogbook(realLogbook) {
            override fun overrideAllData(data: List<LogbookEntryInfo>) {
                super.overrideAllData(data)
                throw Exception()
            }
        }

        // then
        testTransactionIsRolledBack(realRepo, logbook)
    }

    private fun testTransactionIsRolledBack(repo: BackupableRepository, logbook: BackupableLogbook) {

        // when
        try {
            backup.restoreFrom(provideBackupInputStream(), repo, logbook)
        } catch (ignored: Exception) { }

        // then
        assertEmpty(repo)
        assertEmpty(logbook)
    }
}

private fun assertEmpty(repository: BackupableRepository) {
    assertTrue(repository.allLanguages(0, 500).isEmpty())
    assertTrue(repository.allDomains(0, 500).isEmpty())
    assertTrue(repository.allDecks(0, 500).isEmpty())
    assertTrue(repository.allCards(0, 500).isEmpty())
    assertTrue(repository.allTerms(0, 500).isEmpty())
    assertTrue(repository.allExerciseStates( 0, 500).isEmpty())
}

private fun assertEmpty(logbook: BackupableLogbook) {
    assertTrue(logbook.exportAllData(0, 500).isEmpty())
}

private fun provideBackupInputStream(): InputStream {
    val tempRepo = SqliteBackupHelper(provideRepositoryHelper())
    tempRepo.writeLanguages(JsonBackupTestData.languages)
    tempRepo.writeDomains(JsonBackupTestData.domains)
    tempRepo.writeTerms(JsonBackupTestData.terms)
    tempRepo.writeDecks(JsonBackupTestData.decks)
    tempRepo.writeCards(JsonBackupTestData.cards)
    tempRepo.writeExerciseStates(JsonBackupTestData.cardStates)

    val tempLogbook = createNonEmptyLogbookWithMockData()

    val output = ByteArrayOutputStream()
    JsonBackup().create(tempRepo, tempLogbook, output)

    return ByteArrayInputStream(output.toByteArray())
}

open class OpenBackupableRepostory(private val inner: BackupableRepository) : BackupableRepository {

    override fun allLanguages(offset: Int, limit: Int): List<LanguageInfo> = inner.allLanguages(offset, limit)

    override fun allDomains(offset: Int, limit: Int): List<DomainInfo> = inner.allDomains(offset, limit)

    override fun allTerms(offset: Int, limit: Int): List<TermInfo> = inner.allTerms(offset, limit)

    override fun allCards(offset: Int, limit: Int): List<CardInfo> = inner.allCards(offset, limit)

    override fun allDecks(offset: Int, limit: Int): List<DeckInfo> = inner.allDecks(offset, limit)

    override fun allExerciseStates(offset: Int, limit: Int): List<LearningProgressInfo>
            = inner.allExerciseStates(offset, limit)

    override fun beginTransaction() = inner.beginTransaction()

    override fun endTransaction() = inner.endTransaction()

    override fun setTransactionSuccessful() = inner.setTransactionSuccessful()

    override fun dropAllData() = inner.dropAllData()

    override fun writeLanguages(languages: List<LanguageInfo>) = inner.writeLanguages(languages)

    override fun writeDomains(domains: List<DomainInfo>) = inner.writeDomains(domains)

    override fun writeTerms(terms: List<TermInfo>) = inner.writeTerms(terms)

    override fun writeCards(cards: List<CardInfo>) = inner.writeCards(cards)

    override fun writeDecks(decks: List<DeckInfo>) = inner.writeDecks(decks)

    override fun writeExerciseStates(states: List<LearningProgressInfo>) = inner.writeExerciseStates(states)

    override fun hasAtLeastOneCard(): Boolean = inner.hasAtLeastOneCard()
}

open class OpenBackupableLogbook(private val inner: BackupableLogbook) : BackupableLogbook {
    override fun overrideAllData(data: List<LogbookEntryInfo>) = inner.overrideAllData(data)

    override fun exportAllData(offset: Int, limit: Int) = inner.exportAllData(offset, limit)

    override fun beginTransaction() = inner.beginTransaction()

    override fun endTransaction() = inner.endTransaction()

    override fun setTransactionSuccessful() = inner.setTransactionSuccessful()

    override fun dropAllData() = inner.dropAllData()
}