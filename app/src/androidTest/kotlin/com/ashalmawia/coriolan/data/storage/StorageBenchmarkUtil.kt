package com.ashalmawia.coriolan.data.storage

import android.util.Log
import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.storage.TestData.cards
import com.ashalmawia.coriolan.data.storage.TestData.decks
import com.ashalmawia.coriolan.data.storage.TestData.domains
import com.ashalmawia.coriolan.data.storage.TestData.languages
import com.ashalmawia.coriolan.data.storage.TestData.states
import com.ashalmawia.coriolan.data.storage.TestData.terms

const val BENCHMARK_TAG = "StorageBenchmark"
const val RUNS = 5

object StorageBenchmarkUtil {

    fun prepare(count: Int) {
        TestData.generateData(count)
    }

    /**
     * returns: Long - average benchmark time in millis
     */
    fun benchmark(
            createRepo: () -> Pair<BackupableRepository, Repository>,
            operation: (Repository) -> Unit,
            prepare: (Repository) -> Unit = { }
    ): Long {
        val results = mutableListOf<Long>()
        val (backup, repo) = createRepo()
        for (i in 1 .. RUNS) {
            val result = singleBenchmark(backup, repo, prepare, operation)
            Log.d(BENCHMARK_TAG, "-- attempt $i, result: $result ms")
            results.add(result)
        }
        return results.average().toLong()
    }

    private fun singleBenchmark(
            backup: BackupableRepository,
            repo: Repository,
            prepare: (Repository) -> Unit,
            operation: (Repository) -> Unit
    ): Long {
        fillRepository(backup)
        prepare(repo)

        val startTime = System.nanoTime()
        operation(repo)
        val endTime = System.nanoTime()

        return (endTime - startTime) / 1_000_000
    }

    private fun fillRepository(repository: BackupableRepository) {
        repository.overrideRepositoryData {
            it.writeLanguages(languages)
            it.writeDomains(domains)
            it.writeDecks(decks)
            it.writeTerms(terms)
            it.writeCards(cards)
            it.writeCardStates(states)
        }
    }
}