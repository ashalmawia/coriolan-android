package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.storage.provideHelper
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteBackupHelper
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.scheduler.StateType
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class JsonBackupSqliteRepositoryTest : JsonBackupTest() {

    override fun createEmptyRepo(exercises: List<Exercise<*, *>>): BackupableRepository
            = SqliteBackupHelper(RuntimeEnvironment.application, exercises, provideHelper(exercises))

    override fun createNonEmptyRepo(exercises: List<Exercise<*, *>>): BackupableRepository {
        val repo = SqliteBackupHelper(RuntimeEnvironment.application, exercises, provideHelper(exercises))

        repo.writeLanguages(JsonBackupTestData.languages)
        repo.writeDomains(JsonBackupTestData.domains)
        repo.writeExpressions(JsonBackupTestData.exressions)
        repo.writeDecks(JsonBackupTestData.decks)
        repo.writeCards(JsonBackupTestData.cards)
        exercises.filter { it.stateType == StateType.SR_STATE }
                .forEach { repo.writeSRStates(it.stableId, JsonBackupTestData.srstates) }

        return repo
    }
}