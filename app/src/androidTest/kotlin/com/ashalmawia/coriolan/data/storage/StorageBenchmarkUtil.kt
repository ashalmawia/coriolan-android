package com.ashalmawia.coriolan.data.storage

import android.util.Log
import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.backup.CardInfo
import com.ashalmawia.coriolan.data.backup.ExerciseStateInfo
import com.ashalmawia.coriolan.data.backup.TermInfo
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.model.Extras

const val BENCHMARK_TAG = "StorageBenchmark"

object StorageBenchmarkUtil {

    /**
     * returns: Long - average benchmark time in millis
     */
    fun benchmark(
            count: Int,
            averagingAttempts: Int,
            createRepo: () -> Pair<BackupableRepository, Repository>,
            operation: (Repository) -> Unit,
            prepare: (Repository) -> Unit = { }
    ): Long {
        val results = mutableListOf<Long>()
        val (backup, repo) = createRepo()
        for (i in 1 .. averagingAttempts) {
            val result = singleBenchmark(count, backup, repo, prepare, operation)
            Log.d(BENCHMARK_TAG, "-- attempt $i, result: $result ms")
            results.add(result)
        }
        return results.average().toLong()
    }

    private fun singleBenchmark(
            count: Int,
            backup: BackupableRepository,
            repo: Repository,
            prepare: (Repository) -> Unit,
            operation: (Repository) -> Unit
    ): Long {
        fillRepository(count, backup)
        prepare(repo)

        val startTime = System.nanoTime()
        operation(repo)
        val endTime = System.nanoTime()

        return (endTime - startTime) / 1000
    }

    private fun fillRepository(count: Int, repository: BackupableRepository) {
        repository.overrideRepositoryData {
            val languages = TestData.languages
            it.writeLanguages(languages)

            val domains = TestData.domains
            it.writeDomains(domains)

            val decks = TestData.decks
            it.writeDecks(decks)

            val terms = mutableListOf<TermInfo>()
            fun id() = terms.size.toLong()
            for (i in 1 .. count / 3 + 3) {
                terms.add(TermInfo(
                        id(), "term with id: ${id()}", 1L, Extras("transcription with id: ${id()}")
                ))
                terms.add(TermInfo(
                        id(), "term with id: ${id()}", 2L, Extras.empty()
                ))
                terms.add(TermInfo(
                        id(), "term with id: ${id()}", 3L, Extras.empty()
                ))
            }
            it.writeTerms(terms)

            val termsByLang = terms.groupBy { it.languageId }

            val cards = mutableListOf<CardInfo>()
            for (i in 1 .. count) {
                val domainId = (i % 2 + 1).toLong()
                val deckId = if (domainId == 1L) {
                    if (i % 3 == 0) 1L else 2L
                } else 3L
                val originalLangId = if (domainId == 1L) 1L else 3L
                val translationId = 2L
                val termsOriginal = termsByLang[originalLangId]!!
                val termsTranslation = termsByLang[translationId]!!
                cards.add(CardInfo(
                        id = i.toLong(),
                        deckId = deckId,
                        domainId = domainId,
                        originalId = termsOriginal[i / 3].id,
                        translationIds = (0 until 3).map { index -> termsTranslation[i / 3 + index].id }
                ))
            }
            it.writeCards(cards)

            val states = mutableListOf<ExerciseStateInfo>()
            for (i in 0 until count) {
                states.add(ExerciseStateInfo(
                        (i + 1).toLong(), ExerciseId.FLASHCARDS, mockToday(), 4
                ))
                states.add(ExerciseStateInfo(
                        (i + 1).toLong(), ExerciseId.TEST, mockToday().minus(5), -1
                ))
            }
            it.writeCardStates(states)
        }
    }
}