package com.ashalmawia.coriolan.data.storage

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ashalmawia.coriolan.data.storage.StorageBenchmarkUtil.benchmark
import com.ashalmawia.coriolan.learning.ExerciseData
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.SchedulingState
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.Language
import com.ashalmawia.coriolan.model.Term
import com.ashalmawia.coriolan.util.asCardId
import com.ashalmawia.coriolan.util.asDeckId
import com.ashalmawia.coriolan.util.asDomainId
import com.ashalmawia.coriolan.util.asLanguageId
import com.ashalmawia.coriolan.util.asTermId
import org.joda.time.DateTime
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@RunWith(AndroidJUnit4::class)
class SqliteStorageBenchmarkTest {

    private var count = 0
    private val results = mutableMapOf<String, Long>()

    @Test
    fun benchmark_100() {       // lightweight, mostly for checking setup and debugging
        runBenchmark(100)
    }

    @Test
    fun benchmark_1000() {      // small database, 1 language, not active use
        runBenchmark(1_000)
    }

    @Test
    fun benchmark_5000() {      // medium database, 1 language,  active use
        runBenchmark(5_000)
    }

    @Test
    fun benchmark_10000() {     // big database, several languages
        runBenchmark(10_000)
    }

    @Test
    fun benchmark_100000() {     // huge database, heavy load testing
        runBenchmark(100_000)
    }

    private fun runBenchmark(count: Int) {
        if (results.isNotEmpty()) throw IllegalStateException("results are not empty")

        this.count = count
        StorageBenchmarkUtil.prepare(count)

        add_language()
        add_domain()
        add_term()
        add_deck()
        add_card()

        update_term()
        update_deck()
        update_card_move()
        update_card_add_translation()
        update_card_learning_progress()

        delete_term()
        delete_deck()
        delete_card()

        query_language_by_id()
        query_language_by_name()
        query_domain_by_id()
        query_all_domains()
        query_term_by_name()
        query_term_by_values()
        query_term_is_used()
        query_deck_by_id()
        query_all_decks()
        query_all_decks_cards_count()
        query_card_by_id()
        query_card_by_values()
        query_all_cards()
        query_cards_of_deck()
        query_deck_pending_cards()
        query_deck_pending_counts()
        query_all_decks_with_pending_counts()
        query_deck_stats()
        query_card_learning_progress()
        query_progress_for_cards_with_originals()

        val output = formatResult()
        printResultsAsFile(output)
    }

    /************************** ADD **************************/

    private fun add_language() {
        benchmark("add language") {
            it.addLanguage("new language")
        }
    }

    private fun add_domain() {
        var language1: Language? = null
        var language2: Language? = null
        benchmark("add domain", prepare = {
            language1 = it.languageById(8L.asLanguageId())
            language2 = it.languageById(2L.asLanguageId())
        }) {
            it.createDomain("", language1!!, language2!!)
        }
    }

    private fun add_term() {
        var language: Language? = null
        benchmark("add term", prepare = {
            language = it.languageById(1L.asLanguageId())
        }) {
            it.addTerm("new term", language!!, "transcription")
        }
    }

    private fun add_deck() {
        var domain: Domain? = null
        benchmark("add deck", prepare = {
            domain = it.domainById(1L.asDomainId())
        }) {
            it.addDeck(domain!!, "new deck")
        }
    }

    private fun add_card() {
        var domain: Domain? = null
        var original: Term? = null
        var translations: List<Term>? = null

        benchmark("add card", prepare = {
            domain = it.domainById(1L.asDomainId())
            original = it.addTerm("some new term", domain!!.langOriginal(), "transcription")
            translations = (1 .. 3).map { i ->
                it.addTerm("some new term $i", domain!!.langTranslations(), null)
            }
        }) {
            it.addCard(domain!!, 1L.asDeckId(), original!!, translations!!)
        }
    }


    private fun update_term() {
        var term: Term? = null
        benchmark("update term", prepare = {
            term = it.termById(1L.asTermId())
        }) {
            it.updateTerm(term!!, "new transcription")
        }
    }

    private fun update_deck() {
        var deck: Deck? = null
        benchmark("update deck", prepare = {
            deck = it.deckById(8L.asDeckId())
        }) {
            it.updateDeck(deck!!,"new deck name")
        }
    }

    private fun update_card_move() {
        var domain: Domain?
        var card: Card? = null
        benchmark("update card - move", prepare = {
            domain = it.domainById(1L.asDomainId())
            card = it.cardById(count.toLong().asCardId(), domain!!)
        }) {
            it.updateCard(card!!, 5L.asDeckId(), card!!.original, card!!.translations)
        }
    }

    private fun update_card_add_translation() {
        var domain: Domain?
        var card: Card? = null
        var translation: Term? = null
        benchmark("update card - add translation", prepare = {
            domain = it.domainById(1L.asDomainId())
            card = it.cardById(count.toLong().asCardId(), domain!!)
            translation = it.addTerm("new term", domain!!.langTranslations(), null)
        }) {
            it.updateCard(card!!, card!!.deckId, card!!.original, card!!.translations.plus(translation!!))
        }
    }

    private fun update_card_learning_progress() {
        var domain: Domain?
        var card: Card? = null
        benchmark("update card learning progress", prepare = {
            domain = it.domainById(1L.asDomainId())
            card = it.cardById(count.toLong().asCardId(), domain!!)
        }) {
            it.updateCardLearningProgress(card!!, LearningProgress(SchedulingState(mockToday(), 5), ExerciseData()))
        }
    }

    /************************** DELETE **************************/

    private fun delete_term() {
        var term: Term? = null
        benchmark("delete term", prepare = {
            term = it.termById((count + 10).toLong().asTermId())
        }) {
            it.deleteTerm(term!!)
        }
    }

    private fun delete_deck() {
        var deck: Deck? = null
        benchmark("delete deck", prepare = {
            deck = it.deckById(8L.asDeckId())
        }) {
            it.deleteDeck(deck!!)
        }
    }

    private fun delete_card() {
        var domain: Domain?
        var card: Card? = null
        benchmark("delete card", prepare = {
            domain = it.domainById(1L.asDomainId())
            card = it.cardById(count.toLong().asCardId(), domain!!)
        }) {
            it.deleteCard(card!!)
        }
    }


    /************************** QUERY **************************/

    private fun query_language_by_id() {
        benchmark("query language by id") {
            it.languageById(3L.asLanguageId())
        }
    }

    private fun query_language_by_name() {
        benchmark("query language by name") {
            it.languageByName("Russian")
        }
    }

    private fun query_domain_by_id() {
        benchmark("query domain by id") {
            it.domainById(5L.asDomainId())
        }
    }

    private fun query_all_domains() {
        benchmark("query all domains") {
            it.allDomains()
        }
    }

    private fun query_term_by_name() {
        benchmark("query term by name") {
            it.termById(15L.asTermId())
        }
    }

    private fun query_term_by_values() {
        var language: Language? = null
        benchmark("query term by values", prepare = {
            language = it.languageById(1L.asLanguageId())
        }) {
            it.termByValues("term with id: 33", language!!)
        }
    }

    private fun query_term_is_used() {
        var term: Term? = null
        benchmark("query term is used", prepare = {
            term = it.termById(70L.asTermId())
        }) {
            it.isUsed(term!!)
        }
    }

    private fun query_deck_by_id() {
        benchmark("query deck by id") {
            it.deckById(8L.asDeckId())
        }
    }

    private fun query_all_decks() {
        var domain: Domain? = null
        benchmark("query all decks", prepare = {
            domain = it.domainById(1L.asDomainId())
        }) {
            it.allDecks(domain!!)
        }
    }

    private fun query_all_decks_cards_count() {
        var domain: Domain? = null
        benchmark("query all decks cards count", prepare = {
            domain = it.domainById(1L.asDomainId())
        }) {
            it.allDecksCardsCount(domain!!)
        }
    }

    private fun query_card_by_id() {
        var domain: Domain? = null
        benchmark("query card by id", prepare = {
            domain = it.domainById(1L.asDomainId())
        }) {
            it.cardById(count.toLong().asCardId(), domain!!)
        }
    }

    private fun query_card_by_values() {
        var domain: Domain? = null
        var original: Term? = null
        benchmark("query card by values", prepare = {
            domain = it.domainById(2L.asDomainId())
            original = it.termById(count.toLong().asTermId())
        }) {
            it.cardByValues(domain!!, original!!)
        }
    }

    private fun query_all_cards() {
        var domain: Domain? = null
        benchmark("query all cards", prepare = {
            domain = it.domainById(1L.asDomainId())
        }) {
            it.allCards(domain!!)
        }
    }

    private fun query_cards_of_deck() {
        var deck: Deck? = null
        benchmark("query cards of deck", prepare = {
            deck = it.deckById(3L.asDeckId())
        }) {
            it.cardsOfDeck(deck!!)
        }
    }

    private fun query_deck_pending_cards() {
        var deck: Deck? = null
        benchmark("query deck pending cards", prepare = {
            deck = it.deckById(3L.asDeckId())
        }) {
            it.pendingCards(deck!!, mockToday())
        }
    }

    private fun query_deck_pending_counts() {
        var deck: Deck? = null
        benchmark("query deck pending counts", prepare = {
            deck = it.deckById(3L.asDeckId())
        }) {
            it.deckPendingCounts(deck!!, CardType.FORWARD, mockToday())
        }
    }

    private fun query_all_decks_with_pending_counts() {
        var domain: Domain? = null
        benchmark("query all decks with pending counts", prepare = {
            domain = it.domainById(1L.asDomainId())
        }) {
            it.allDecksWithPendingCounts(domain!!, mockToday())
        }
    }

    private fun query_deck_stats() {
        var deck: Deck? = null
        benchmark("query deck stats", prepare = {
            deck = it.deckById(3L.asDeckId())
        }) {
            it.deckStats(deck!!)
        }
    }

    private fun query_card_learning_progress() {
        var domain: Domain?
        var card: Card? = null
        benchmark("query card learning progress", prepare = {
            domain = it.domainById(1L.asDomainId())
            card = it.cardById(count.toLong().asCardId(), domain!!)
        }) {
            it.getCardLearningProgress(card!!)
        }
    }

    private fun query_progress_for_cards_with_originals() {
        benchmark("query progress for cards with originals") {
            it.getProgressForCardsWithOriginals(
                    (1..count).map { id -> id.toLong().asTermId() }
            )
        }
    }

    private fun benchmark(description: String, prepare: (Repository) -> Unit = { }, operation: (Repository) -> Unit) {
        if (results.containsKey(description)) {
            throw IllegalStateException("duplicated descriptions are not allowed")
        }

        Log.d(BENCHMARK_TAG, "beginning benchmark [$description]")
        val result = benchmark(operation, prepare)
        Log.d(BENCHMARK_TAG, "ending benchmark [$description] with the result $result")
        results[description] = result
    }

    private fun formatResult(): String {
        val readableResults = results.toList()
                .sortedByDescending { (_, time) -> time }
                .map { (description, time) -> Pair(description,
                        time.toDuration(DurationUnit.MILLISECONDS).toString(DurationUnit.SECONDS, 3)
                )}
        val longestKeyLength = readableResults.maxOf { (key, _) -> key.length }
        val longestValueLength = readableResults.maxOf { (_, value) -> value.length }
        val rowFormat = "| %-${longestKeyLength}s | %${longestValueLength}s |"

        fun StringBuilder.appendTableDivider() {
            append("+")
            append("-".repeat(longestKeyLength + 2))
            append("+")
            append("-".repeat(longestValueLength + 2))
            appendLine("+")
        }
        fun StringBuilder.appendSection(section: List<Pair<String, String>>) {
            appendTableDivider()
            for ((description, duration) in section) {
                appendLine(String.format(rowFormat, description, duration))
            }
        }

        val queryResults = readableResults.filter { (description, _) -> description.startsWith("query") }
        val addResults = readableResults.filter { (description, _) -> description.startsWith("add") }
        val updateResults = readableResults.filter { (description, _) -> description.startsWith("update") }
        val deleteResults = readableResults.filter { (description, _) -> description.startsWith("delete") }
        if (queryResults.size + addResults.size + updateResults.size + deleteResults.size != readableResults.size) {
            throw IllegalStateException("use one of the existing description categories or create a new one")
        }

        return StringBuilder().apply {
            appendLine("   SQLITE DATABASE PERFORMANCE BENCHMARK")
            appendLine("    - sample size: $count cards")
            appendLine("    - database size: ${StorageBenchmarkUtil.databaseSizeHumanReadable()}")
            appendLine("    - averaged over: $RUNS runs")
            appendLine("    - date: ${DateTime().toString("yyyy-MM-dd, HH:mm")}")
            appendLine("    - ran with Google Pixel 3a, API 30, RAM 3.7GB")

            appendTableDivider()
            appendLine(String.format(rowFormat, "Task", "Time"))

            appendSection(queryResults)
            appendSection(addResults)
            appendSection(updateResults)
            appendSection(deleteResults)

            appendTableDivider()
        }.toString()
    }

    private fun printResultsAsFile(result: String) {
        val fileName = "benchmark_storage_${count}cards_${DateTime.now().toString("yyyy-MM-dd_HH:mm:ss")}"
        val context = ApplicationProvider.getApplicationContext<Context>()
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        file.createNewFile()
        file.writeText(result)
    }
}
