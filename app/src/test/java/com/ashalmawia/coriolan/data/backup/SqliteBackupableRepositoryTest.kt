package com.ashalmawia.coriolan.data.backup

import com.ashalmawia.coriolan.data.storage.sqlite.SqliteBackupHelper
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteRepositoryOpenHelper
import com.ashalmawia.coriolan.learning.exercise.ExercisesRegistry
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class SqliteBackupableRepositoryTest : BackupableRepositoryTest() {

    override fun createRepository(exercisesRegistry: ExercisesRegistry): BackupableRepository {
        val helper = SqliteRepositoryOpenHelper(RuntimeEnvironment.application, exercisesRegistry)
        return SqliteBackupHelper(exercisesRegistry, helper)
    }
}