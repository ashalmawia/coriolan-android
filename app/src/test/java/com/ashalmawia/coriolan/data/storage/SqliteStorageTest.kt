package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.storage.sqlite.SqliteStorage
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.SQLiteMode

/**
 * Test pure SQLite storage, without any wrappers.
 */

@RunWith(RobolectricTestRunner::class)
@SQLiteMode(SQLiteMode.Mode.LEGACY)
class SqliteStorageTest : StorageTest() {

    override fun createStorage(): Repository {
        val helper = provideRepositoryHelper()
        return SqliteStorage(helper)
    }
}