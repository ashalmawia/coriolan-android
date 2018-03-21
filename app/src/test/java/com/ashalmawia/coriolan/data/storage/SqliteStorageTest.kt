package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.storage.sqlite.SqliteStorage
import com.ashalmawia.coriolan.learning.ExerciseDescriptor
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Test pure SQLite storage, without any wrappers.
 */

@RunWith(RobolectricTestRunner::class)
class SqliteStorageTest : StorageTest() {

    override fun createStorage(exercises: List<ExerciseDescriptor<*, *>>): Repository {
        val storage = spy(SqliteStorage(RuntimeEnvironment.application, exercises))
        whenever(storage.storage()).thenReturn(storage)
        return storage
    }
}