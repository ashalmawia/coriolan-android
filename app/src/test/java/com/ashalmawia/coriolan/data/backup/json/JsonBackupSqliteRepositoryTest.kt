package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.storage.provideHelper
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteBackupHelper
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.SQLiteMode

@RunWith(RobolectricTestRunner::class)
@SQLiteMode(SQLiteMode.Mode.LEGACY)
class JsonBackupSqliteRepositoryTest : JsonBackupTest() {

    override fun createEmptyRepo(): BackupableRepository
            = SqliteBackupHelper(provideHelper())

    override fun createNonEmptyRepo(): BackupableRepository {
        val repo = SqliteBackupHelper(provideHelper())

        repo.writeLanguages(JsonBackupTestData.languages)
        repo.writeDomains(JsonBackupTestData.domains)
        repo.writeTerms(JsonBackupTestData.terms)
        repo.writeDecks(JsonBackupTestData.decks)
        repo.writeCards(JsonBackupTestData.cards)
        repo.writeExerciseStates(JsonBackupTestData.cardStates)

        return repo
    }
}