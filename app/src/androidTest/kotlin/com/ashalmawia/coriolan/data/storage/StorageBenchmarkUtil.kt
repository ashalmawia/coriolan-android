package com.ashalmawia.coriolan.data.storage

import android.text.format.Formatter
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.ashalmawia.coriolan.context
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteBackupHelper
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteRepositoryOpenHelper
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteStorage
import java.io.File
import java.nio.file.Files

const val BENCHMARK_TAG = "StorageBenchmark"
const val RUNS = 5

private const val PREFILLED_DATABASE_NAME = "test_prefilled"

object StorageBenchmarkUtil {

    private val helper by lazy { provideHelper() }

    fun prepare(count: Int) {
        val helper = provideHelper()
        val backup = SqliteBackupHelper(helper)
        fillDatabase(count, backup)
        helper.close()
        savePrefilledDatabase()

        Log.d(BENCHMARK_TAG, "generated database for count $count, file size: ${databaseSizeHumanReadable()}")
    }

    /**
     * returns: Long - average benchmark time in millis
     */
    fun benchmark(
            operation: (Repository) -> Unit,
            prepare: (Repository) -> Unit = { }
    ): Long {
        val results = mutableListOf<Long>()
        val repo = createRepo()
        for (i in 1 .. RUNS) {
            val result = singleBenchmark(repo, prepare, operation)
            Log.d(BENCHMARK_TAG, "-- attempt $i, result: $result ms")
            results.add(result)
        }
        return results.average().toLong()
    }

    private fun singleBenchmark(
            repo: Repository,
            prepare: (Repository) -> Unit,
            operation: (Repository) -> Unit
    ): Long {
        resetDatabase()
        prepare(repo)

        val startTime = System.nanoTime()
        operation(repo)
        val endTime = System.nanoTime()

        return (endTime - startTime) / 1_000_000
    }
    private fun createRepo(): Repository = SqliteStorage(helper)

    private fun provideHelper(): SqliteRepositoryOpenHelper {
        return SqliteRepositoryOpenHelper(ApplicationProvider.getApplicationContext(), "test.db")
    }

    private fun savePrefilledDatabase() {
        val context = context()
        val dbFile = context.getDatabasePath(helper.databaseName)
        val copy = File(dbFile.parent, PREFILLED_DATABASE_NAME)
        dbFile.copyTo(copy, true)
    }

    private fun resetDatabase() {
        helper.close()
        val context = context()
        val dbFile = context.getDatabasePath(helper.databaseName)
        val copy = File(dbFile.parent, PREFILLED_DATABASE_NAME)
        copy.copyTo(dbFile, true)
    }

    private fun databaseSize(): Long {
        val context = context()
        val dbFile = context.getDatabasePath(helper.databaseName)
        val copy = File(dbFile.parent, PREFILLED_DATABASE_NAME)
        return Files.size(copy.toPath())
    }

    fun databaseSizeHumanReadable(): String {
        return Formatter.formatShortFileSize(context(), databaseSize())
    }
}
