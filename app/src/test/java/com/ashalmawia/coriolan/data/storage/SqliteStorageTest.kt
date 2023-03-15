package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.storage.sqlite.SqliteStorage
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.MockExercisesRegistry
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Test pure SQLite storage, without any wrappers.
 */

@RunWith(RobolectricTestRunner::class)
class SqliteStorageTest : StorageTest() {

    override fun createStorage(exercises: List<Exercise<*, *>>): Repository {
        val registry = MockExercisesRegistry(exercises)
        val helper = provideHelper(registry)
        return SqliteStorage(helper, mockEmptyStateProvider)
    }
}