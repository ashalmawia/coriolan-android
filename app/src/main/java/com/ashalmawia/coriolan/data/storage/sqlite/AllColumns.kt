package com.ashalmawia.coriolan.data.storage.sqlite
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_DECK_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_DOMAIN_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_FRONT_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_REVERSE_CARD_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_REVERSE_TERM_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_TYPE
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DECKS_DOMAIN_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DECKS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DECKS_NAME
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DOMAINS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DOMAINS_LANG_ORIGINAL
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DOMAINS_LANG_TRANSLATIONS
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DOMAINS_NAME
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.LANGUAGES_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.LANGUAGES_VALUE
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.STATES_CARD_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.STATES_DUE_DATE
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.STATES_EXERCISE
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.STATES_PERIOD
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.TERMS_EXTRAS
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.TERMS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.TERMS_LANGUAGE_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.TERMS_VALUE

private val languages = arrayOf(
        LANGUAGES_ID,
        LANGUAGES_VALUE
)

private val terms = arrayOf(
        TERMS_ID,
        TERMS_VALUE,
        TERMS_LANGUAGE_ID,
        TERMS_EXTRAS
)

private val domains = arrayOf(
        DOMAINS_ID,
        DOMAINS_NAME,
        DOMAINS_LANG_ORIGINAL,
        DOMAINS_LANG_TRANSLATIONS
)

private val decks = arrayOf(
        DECKS_ID,
        DECKS_NAME,
        DECKS_DOMAIN_ID
)

private val cards = arrayOf(
        CARDS_ID,
        CARDS_FRONT_ID,
        CARDS_DECK_ID,
        CARDS_DOMAIN_ID,
        CARDS_TYPE
)

private val translations = arrayOf(
        CARDS_REVERSE_CARD_ID,
        CARDS_REVERSE_TERM_ID
)

private val cardStates = arrayOf(
        STATES_CARD_ID,
        STATES_EXERCISE,
        STATES_DUE_DATE,
        STATES_PERIOD
)

fun String.from(alias: String?): String = if (alias != null) "${alias}_$this" else this

fun allColumnsLanguages(alias: String? = null): String = allColumns(languages, alias)
fun allColumnsDomains(alias: String? = null): String = allColumns(domains, alias)
fun allColumnsStates(alias: String? = null): String = allColumns(cardStates, alias)

private fun allColumns(columns: Array<String>, alias: String?): String {
    if (alias == null) {
        return allColumnsWithoutAlias(columns)
    } else {
        return withAlias(columns, alias)
    }
}

private fun allColumnsWithoutAlias(columns: Array<String>): String
        = columns.joinToString { it }

fun withAlias(columns: Array<String>, alias: String): String
        = columns.joinToString { """$alias.$it AS "${it.from(alias)}"""" }