package com.ashalmawia.coriolan.data.backup

import com.ashalmawia.coriolan.data.backup.json.JsonBackup
import com.ashalmawia.coriolan.data.backup.json.JsonBackupTestData
import com.ashalmawia.coriolan.data.storage.provideHelper
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteBackupHelper
import com.ashalmawia.coriolan.util.OpenForTesting
import junit.framework.Assert.assertTrue
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

    val realRepo = SqliteBackupHelper(provideHelper())

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
            override fun writeCardStates(states: List<CardStateInfo>) {
                super.writeCardStates(states)
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
        } catch (ignored: Exception) { }

        // then
        assertEmpty(repo)
    }
}

private fun assertEmpty(repository: BackupableRepository) {
    assertTrue(repository.allLanguages(0, 500).isEmpty())
    assertTrue(repository.allDomains(0, 500).isEmpty())
    assertTrue(repository.allDecks(0, 500).isEmpty())
    assertTrue(repository.allCards(0, 500).isEmpty())
    assertTrue(repository.allExpressions(0, 500).isEmpty())
    assertTrue(repository.allCardStates( 0, 500).isEmpty())
}

private fun provideBackupInputStream(): InputStream {
    val tempRepo = SqliteBackupHelper(provideHelper())
    tempRepo.writeLanguages(JsonBackupTestData.languages)
    tempRepo.writeDomains(JsonBackupTestData.domains)
    tempRepo.writeExpressions(JsonBackupTestData.exressions)
    tempRepo.writeDecks(JsonBackupTestData.decks)
    tempRepo.writeCards(JsonBackupTestData.cards)
    tempRepo.writeCardStates(JsonBackupTestData.cardStates)

    val output = ByteArrayOutputStream()
    JsonBackup().create(tempRepo, output)

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

    override fun allExpressionExtras(offset: Int, limit: Int) = inner.allExpressionExtras(offset, limit)

    override fun allCards(offset: Int, limit: Int): List<CardInfo> = inner.allCards(offset, limit)

    override fun allDecks(offset: Int, limit: Int): List<DeckInfo> = inner.allDecks(offset, limit)

    override fun allCardStates(offset: Int, limit: Int): List<CardStateInfo>
            = inner.allCardStates(offset, limit)

    override fun clearAll() = inner.clearAll()

    override fun writeLanguages(languages: List<LanguageInfo>) = inner.writeLanguages(languages)

    override fun writeDomains(domains: List<DomainInfo>) = inner.writeDomains(domains)

    override fun writeExpressions(expressions: List<ExpressionInfo>) = inner.writeExpressions(expressions)

    override fun writeExpressionExtras(extras: List<ExpressionExtraInfo>) = inner.writeExpressionExtras(extras)

    override fun writeCards(cards: List<CardInfo>) = inner.writeCards(cards)

    override fun writeDecks(decks: List<DeckInfo>) = inner.writeDecks(decks)

    override fun writeCardStates(states: List<CardStateInfo>) = inner.writeCardStates(states)

    override fun hasAtLeastOneCard(): Boolean = inner.hasAtLeastOneCard()
}