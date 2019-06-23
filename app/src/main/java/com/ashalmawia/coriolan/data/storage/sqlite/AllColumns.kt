package com.ashalmawia.coriolan.data.storage.sqlite

private val languages = arrayOf(
        SQLITE_COLUMN_ID,
        SQLITE_COLUMN_LANG_VALUE
)

private val expressions = arrayOf(
        SQLITE_COLUMN_ID,
        SQLITE_COLUMN_VALUE,
        SQLITE_COLUMN_LANGUAGE_ID
)

private val domains = arrayOf(
        SQLITE_COLUMN_ID,
        SQLITE_COLUMN_NAME,
        SQLITE_COLUMN_LANG_ORIGINAL,
        SQLITE_COLUMN_LANG_TRANSLATIONS
)

private val decks = arrayOf(
        SQLITE_COLUMN_ID,
        SQLITE_COLUMN_NAME,
        SQLITE_COLUMN_DOMAIN_ID
)

private val cards = arrayOf(
        SQLITE_COLUMN_ID,
        SQLITE_COLUMN_FRONT_ID,
        SQLITE_COLUMN_DECK_ID,
        SQLITE_COLUMN_DOMAIN_ID
)

private val translations = arrayOf(
        SQLITE_COLUMN_CARD_ID,
        SQLITE_COLUMN_EXPRESSION_ID
)

private val srStates = arrayOf(
        SQLITE_COLUMN_CARD_ID,
        SQLITE_COLUMN_DUE,
        SQLITE_COLUMN_PERIOD
)

fun String.from(alias: String?): String = if (alias != null) "${alias}_$this" else this

fun allColumnsLanguages(alias: String? = null): String = allColumns(languages, alias)
fun allColumnsExpressions(alias: String? = null): String = allColumns(expressions, alias)
fun allColumnsDomains(alias: String? = null): String = allColumns(domains, alias)
fun allColumnsDecks(alias: String? = null): String = allColumns(decks, alias)
fun allColumnsCards(alias: String? = null): String = allColumns(cards, alias)
fun allColumnsReverse(alias: String? = null): String = allColumns(translations, alias)
fun allColumnsSRStates(alias: String? = null): String = allColumns(srStates, alias)

private fun allColumns(columns: Array<String>, alias: String?): String {
    if (alias == null) {
        return allColumnsWithoutAlias(columns)
    } else {
        return allColumnsWithAlias(columns, alias)
    }
}

private fun allColumnsWithoutAlias(columns: Array<String>): String
        = columns.joinToString { it }

private fun allColumnsWithAlias(columns: Array<String>, alias: String): String
        = columns.joinToString { """$alias.$it AS "${it.from(alias)}"""" }