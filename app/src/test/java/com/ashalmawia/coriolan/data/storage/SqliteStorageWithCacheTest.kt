package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.storage.sqlite.SqliteStorage
import com.ashalmawia.coriolan.learning.ExerciseDescriptor
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Test SQLite storage wrapped with in-memory cache.
 */

@RunWith(RobolectricTestRunner::class)
class SqliteStorageWithCacheTest : StorageTest() {

    override fun createStorage(exercises: List<ExerciseDescriptor<*, *>>): Repository {
        val helper = provideHelper(exercises)
        return SqliteStorage(RuntimeEnvironment.application, exercises, helper)
    }
}