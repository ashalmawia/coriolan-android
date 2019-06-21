package com.ashalmawia.coriolan.data.backup

import com.ashalmawia.coriolan.data.storage.sqlite.SqliteBackupHelper
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteRepositoryOpenHelper
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.MockExercisesRegistry
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class SqliteBackupableRepositoryTest : BackupableRepositoryTest() {

    override fun createRepository(exercises: List<Exercise<*, *>>): BackupableRepository {
        val helper = SqliteRepositoryOpenHelper(RuntimeEnvironment.application, exercises)
        return SqliteBackupHelper(RuntimeEnvironment.application, MockExercisesRegistry(exercises), helper)
    }
}