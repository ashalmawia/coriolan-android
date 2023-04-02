package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.storage.StorageBenchmarkUtil.benchmark
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteBackupHelper
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteStorage
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.Extras
import com.ashalmawia.coriolan.model.Language
import com.ashalmawia.coriolan.model.Term
import com.ashalmawia.coriolan.model.mockLearningProgressInProgress
import org.joda.time.DateTime
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.SQLiteMode
import java.io.File
import kotlin.time.DurationUnit
import kotlin.time.toDuration

private const val AVERAGING_ATTEMPTS = 10

@RunWith(RobolectricTestRunner::class)
@SQLiteMode(SQLiteMode.Mode.LEGACY)
class SqliteStorageBenchmarkTest {

    private var count = 0
    private val results = mutableMapOf<String, Long>()

    @Test
    fun benchmark_100() {
        runBenchmark(100)
    }

    @Test
    fun benchmark_1000() {
        runBenchmark(1_000)
    }

    @Test
    fun benchmark_10000() {
        runBenchmark(10_000)
    }

    private fun runBenchmark(count: Int) {
        results.clear()
        this.count = count

        `add language`()
        `add domain`()
        `add term without extras`()
        `add term with extras`()
        `add deck`()
        `add card`()

        `update term without extras`()
        `update term with extras`()
        `update deck`()
        `update card - move`()
        `update card - add translation`()
        `update card learning progress`()

        `delete term`()
        `delete deck`()
        `delete card`()

        `query language by id`()
        `query language by name`()
        `query domain by id`()
        `query all domains`()
        `query term by name`()
        `query term by values`()
        `query term is used`()
        `query deck by id`()
        `query all decks`()
        `query card by id`()
        `query card by values`()
        `query all cards`()
        `query cards of deck`()
        `query deck pending cards`()
        `query deck pending counts`()
        `query card learning progress`()
        `query progress for cards with originals`()

        val output = formatResult()
        printResultsAsFile(output)
    }

    /************************** ADD **************************/

    private fun `add language`() {
        benchmark("add language") {
            it.addLanguage("new language")
        }
    }

    private fun `add domain`() {
        var language1: Language? = null
        var language2: Language? = null
        benchmark("add domain", prepare = {
            language1 = it.languageById(8L)
            language2 = it.languageById(2L)
        }) {
            it.createDomain("", language1!!, language2!!)
        }
    }

    private fun `add term without extras`() {
        var language: Language? = null
        benchmark("add term without extras", prepare = {
            language = it.languageById(1L)
        }) {
            it.addTerm("new term", language!!, null)
        }
    }

    private fun `add term with extras`() {
        var language: Language? = null
        benchmark("add term with extras", prepare = {
            language = it.languageById(1L)
        }) {
            it.addTerm("new term", language!!, Extras("transcription"))
        }
    }

    private fun `add deck`() {
        var domain: Domain? = null
        benchmark("add deck", prepare = {
            domain = it.domainById(1L)
        }) {
            it.addDeck(domain!!, "new deck")
        }
    }

    private fun `add card`() {
        var domain: Domain? = null
        var original: Term? = null
        var translations: List<Term>? = null

        benchmark("add card", prepare = {
            domain = it.domainById(1L)
            original = it.addTerm("some new term", domain!!.langOriginal(), Extras("transcription"))
            translations = (1 .. 3).map { i ->
                it.addTerm("some new term $i", domain!!.langTranslations(), null)
            }
        }) {
            it.addCard(domain!!, 1L, original!!, translations!!)
        }
    }


    /************************** UPDATE **************************/

    private fun `update term without extras`() {
        var term: Term? = null
        benchmark("update term without extras", prepare = {
            term = it.termById(1L)
        }) {
            it.updateTerm(term!!, null)
        }
    }

    private fun `update term with extras`() {
        var term: Term? = null
        benchmark("update term with extras", prepare = {
            term = it.termById(1L)
        }) {
            it.updateTerm(term!!, Extras("new transcription"))
        }
    }

    private fun `update deck`() {
        var domain: Domain?
        var deck: Deck? = null
        benchmark("update deck", prepare = {
            domain = it.domainById(1L)
            deck = it.deckById(8L, domain!!)
        }) {
            it.updateDeck(deck!!,"new deck name")
        }
    }

    private fun `update card - move`() {
        var domain: Domain?
        var card: Card? = null
        benchmark("update card - move", prepare = {
            domain = it.domainById(1L)
            card = it.cardById(count.toLong(), domain!!)
        }) {
            it.updateCard(card!!, 5L, card!!.original, card!!.translations)
        }
    }

    private fun `update card - add translation`() {
        var domain: Domain?
        var card: Card? = null
        var translation: Term? = null
        benchmark("update card - add translation", prepare = {
            domain = it.domainById(1L)
            card = it.cardById(count.toLong(), domain!!)
            translation = it.addTerm("new term", domain!!.langTranslations(), null)
        }) {
            it.updateCard(card!!, card!!.deckId, card!!.original, card!!.translations.plus(translation!!))
        }
    }

    private fun `update card learning progress`() {
        var domain: Domain?
        var card: Card? = null
        benchmark("update card learning progress", prepare = {
            domain = it.domainById(1L)
            card = it.cardById(count.toLong(), domain!!)
        }) {
            it.updateCardLearningProgress(card!!, mockLearningProgressInProgress())
        }
    }

    /************************** DELETE **************************/

    private fun `delete term`() {
        var term: Term? = null
        benchmark("delete term", prepare = {
            term = it.termById((count + 2).toLong())
        }) {
            it.deleteTerm(term!!)
        }
    }

    private fun `delete deck`() {
        var domain: Domain?
        var deck: Deck? = null
        benchmark("delete deck", prepare = {
            domain = it.domainById(1L)
            deck = it.deckById(8L, domain!!)
        }) {
            it.deleteDeck(deck!!)
        }
    }

    private fun `delete card`() {
        var domain: Domain?
        var card: Card? = null
        benchmark("delete card", prepare = {
            domain = it.domainById(1L)
            card = it.cardById(count.toLong(), domain!!)
        }) {
            it.deleteCard(card!!)
        }
    }


    /************************** QUERY **************************/

    private fun `query language by id`() {
        benchmark("query language by id") {
            it.languageById(3L)
        }
    }

    private fun `query language by name`() {
        benchmark("query language by name") {
            it.languageByName("Russian")
        }
    }

    private fun `query domain by id`() {
        benchmark("query domain by id") {
            it.domainById(5L)
        }
    }

    private fun `query all domains`() {
        benchmark("query all domains") {
            it.allDomains()
        }
    }

    private fun `query term by name`() {
        benchmark("query term by name") {
            it.termById(15L)
        }
    }

    private fun `query term by values`() {
        var language: Language? = null
        benchmark("query term by values", prepare = {
            language = it.languageById(1L)
        }) {
            it.termByValues("term with id: 33", language!!)
        }
    }

    private fun `query term is used`() {
        var term: Term? = null
        benchmark("query term is used", prepare = {
            term = it.termById(70L)
        }) {
            it.isUsed(term!!)
        }
    }

    private fun `query deck by id`() {
        var domain: Domain? = null
        benchmark("query deck by id", prepare = {
            domain = it.domainById(1L)
        }) {
            it.deckById(8L, domain!!)
        }
    }

    private fun `query all decks`() {
        var domain: Domain? = null
        benchmark("query all decks", prepare = {
            domain = it.domainById(1L)
        }) {
            it.allDecks(domain!!)
        }
    }

    private fun `query card by id`() {
        var domain: Domain? = null
        benchmark("query card by id", prepare = {
            domain = it.domainById(1L)
        }) {
            it.cardById(count.toLong(), domain!!)
        }
    }

    private fun `query card by values`() {
        var domain: Domain? = null
        var original: Term? = null
        benchmark("query card by values", prepare = {
            domain = it.domainById(2L)
            original = it.termById(count.toLong())
        }) {
            it.cardByValues(domain!!, original!!)
        }
    }

    private fun `query all cards`() {
        var domain: Domain? = null
        benchmark("query all cards", prepare = {
            domain = it.domainById(1L)
        }) {
            it.allCards(domain!!)
        }
    }

    private fun `query cards of deck`() {
        var domain: Domain?
        var deck: Deck? = null
        benchmark("query cards of deck", prepare = {
            domain = it.domainById(2L)
            deck = it.deckById(3L, domain!!)
        }) {
            it.cardsOfDeck(deck!!)
        }
    }

    private fun `query deck pending cards`() {
        var domain: Domain?
        var deck: Deck? = null
        benchmark("query deck pending cards", prepare = {
            domain = it.domainById(2L)
            deck = it.deckById(3L, domain!!)
        }) {
            it.pendingCards(deck!!, mockToday())
        }
    }

    private fun `query deck pending counts`() {
        var domain: Domain?
        var deck: Deck? = null
        benchmark("query deck pending counts", prepare = {
            domain = it.domainById(2L)
            deck = it.deckById(3L, domain!!)
        }) {
            it.deckPendingCounts(deck!!, CardType.FORWARD, mockToday())
        }
    }

    private fun `query card learning progress`() {
        var domain: Domain?
        var card: Card? = null
        benchmark("query card learning progress", prepare = {
            domain = it.domainById(1L)
            card = it.cardById(count.toLong(), domain!!)
        }) {
            it.getCardLearningProgress(card!!)
        }
    }

    private fun `query progress for cards with originals`() {
        benchmark("query progress for cards with originals") {
            it.getStatesForCardsWithOriginals(
                    (1..count).map { id -> id.toLong() }
            )
        }
    }

    private fun benchmark(description: String, prepare: (Repository) -> Unit = { }, operation: (Repository) -> Unit) {
        if (results.containsKey(description)) {
            throw IllegalStateException("duplicated descriptions are not allowed")
        }

        val result = benchmark(count, AVERAGING_ATTEMPTS, this::createRepo, operation, prepare)
        results[description] = result
    }

    private fun createRepo(): Pair<BackupableRepository, Repository> {
        val helper = provideHelper()
        return Pair(SqliteBackupHelper(helper), SqliteStorage(helper))
    }

    private fun formatResult(): String {
        val readableResults = results.mapValues {
            it.value.toDuration(DurationUnit.MILLISECONDS).toString()
        }
        val longestKeyLength = readableResults.maxOf { it.key.length }
        val longestValueLength = readableResults.maxOf { it.value.length }
        val leftAlignFormat = "| %-${longestKeyLength}s | %-${longestValueLength}s |%n"

        val sb = StringBuilder()
        sb.appendLine("BENCHMARK")
        sb.appendLine("    - sample size: $count cards")
        sb.appendLine("    - date: ${DateTime().toString("yyyy-MM-dd, HH:mm")}")
        sb.appendLine("    - ran with Robolectric")     // TODO: run on a real device

        sb.appendLine(String.format("+-----------------+------+%n"))
        sb.appendLine(String.format("| Task     | Time   |%n"))
        sb.appendLine(String.format("+-----------------+------+%n"))
        for ((description, duration) in readableResults) {
            sb.appendLine(String.format(leftAlignFormat, description, duration))
        }
        sb.appendLine(String.format("+-----------------+------+%n"))
        return sb.toString()
    }

    private fun printResultsAsFile(result: String) {
        val fileName = "benchmark_storage_${DateTime.now().toString("yyyy-MM-dd_HH:mm:ss")}"
        val file = File(fileName)
        file.createNewFile()
        file.writeText(result)
    }
}
