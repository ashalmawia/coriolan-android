package com.ashalmawia.coriolan.data.storage.sqlite

private val languages = arrayOf(
        SQLITE_COLUMN_ID,
        SQLITE_COLUMN_LANG_VALUE
)

private val expressions = arrayOf(
        SQLITE_COLUMN_ID,
        SQLITE_COLUMN_VALUE,
        SQLITE_COLUMN_TYPE,
        SQLITE_COLUMN_LANGUAGE_ID
)
private val decks = arrayOf(
        SQLITE_COLUMN_ID,
        SQLITE_COLUMN_NAME
)

private val cards = arrayOf(
        SQLITE_COLUMN_ID,
        SQLITE_COLUMN_FRONT_ID,
        SQLITE_COLUMN_DECK_ID
)

private val translations = arrayOf(
        SQLITE_COLUMN_CARD_ID,
        SQLITE_COLUMN_EXPRESSION_ID
)

fun String.from(alias: String?): String = if (alias != null) "${alias}_$this" else this

fun allColumnsLanguages(alias: String? = null): String = allColumns(languages, alias)
fun allColumnsExpressions(alias: String? = null): String = allColumns(expressions, alias)
fun allColumnsDecks(alias: String? = null): String = allColumns(decks, alias)
fun allColumnsCards(alias: String? = null): String = allColumns(cards, alias)
fun allColumnsTranslations(alias: String? = null): String = allColumns(translations, alias)

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