package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.storage.provideHelper
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteBackupHelper
import com.ashalmawia.coriolan.learning.MockExercisesRegistry
import com.ashalmawia.coriolan.learning.StateType
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JsonBackupSqliteRepositoryTest : JsonBackupTest() {

    override fun createEmptyRepo(exercises: MockExercisesRegistry): BackupableRepository
            = SqliteBackupHelper(exercises, provideHelper(exercises))

    override fun createNonEmptyRepo(exercises: MockExercisesRegistry): BackupableRepository {
        val repo = SqliteBackupHelper(exercises, provideHelper(exercises))

        repo.writeLanguages(JsonBackupTestData.languages)
        repo.writeDomains(JsonBackupTestData.domains)
        repo.writeExpressions(JsonBackupTestData.exressions)
        repo.writeExpressionExtras(JsonBackupTestData.expressionExtras)
        repo.writeDecks(JsonBackupTestData.decks)
        repo.writeCards(JsonBackupTestData.cards)
        exercises.allExercises().filter { it.stateType == StateType.SR_STATE }
                .forEach { repo.writeSRStates(it.stableId, JsonBackupTestData.srstates) }

        return repo
    }
}