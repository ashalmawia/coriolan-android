package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.backup.logbook.createNonEmptyLogbookWithMockData
import com.ashalmawia.coriolan.data.logbook.BackupableLogbook
import com.ashalmawia.coriolan.data.logbook.sqlite.SqliteLogbook
import com.ashalmawia.coriolan.data.storage.provideLogbookHelper
import com.ashalmawia.coriolan.data.storage.provideRepositoryHelper
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteBackupHelper
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.SQLiteMode

@RunWith(RobolectricTestRunner::class)
@SQLiteMode(SQLiteMode.Mode.LEGACY)
class JsonBackupSqliteRepositoryTest : JsonBackupTest() {

    override fun createEmptyRepo(): BackupableRepository
            = SqliteBackupHelper(provideRepositoryHelper())

    override fun createNonEmptyRepo(): BackupableRepository {
        val repo = SqliteBackupHelper(provideRepositoryHelper())

        repo.writeLanguages(JsonBackupTestData.languages)
        repo.writeDomains(JsonBackupTestData.domains)
        repo.writeTerms(JsonBackupTestData.terms)
        repo.writeDecks(JsonBackupTestData.decks)
        repo.writeCards(JsonBackupTestData.cards)
        repo.writeExerciseStates(JsonBackupTestData.cardStates)

        return repo
    }

    override fun createEmptyLogbook(): BackupableLogbook {
        return SqliteLogbook(provideLogbookHelper())
    }

    override fun createNonEmptyLogbook(): BackupableLogbook = createNonEmptyLogbookWithMockData()
}