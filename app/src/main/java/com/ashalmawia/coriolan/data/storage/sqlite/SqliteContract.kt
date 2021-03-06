package com.ashalmawia.coriolan.data.storage.sqlite

const val SQLITE_TABLE_LANGUAGES = "Languages"
const val SQLITE_TABLE_EXPRESSIONS = "Expressions"
const val SQLITE_TABLE_EXPRESSION_EXTRAS = "ExprExtras"
const val SQLITE_TABLE_DOMAINS = "Domains"
const val SQLITE_TABLE_CARDS = "Cards"
const val SQLITE_TABLE_CARDS_REVERSE = "CardsReverse"
const val SQLITE_TABLE_DECKS = "Decks"

fun sqliteTableExerciseState(exerciseId: String): String {
    return "State_$exerciseId"
}

const val SQLITE_COLUMN_ID = "_id"
const val SQLITE_COLUMN_VALUE = "Value"
const val SQLITE_COLUMN_LANG_VALUE = "LangValue"
const val SQLITE_COLUMN_LANGUAGE_ID = "LangId"
const val SQLITE_COLUMN_FRONT_ID = "FrontId"
const val SQLITE_COLUMN_DECK_ID = "DeckId"
const val SQLITE_COLUMN_CARD_ID = "CardId"
const val SQLITE_COLUMN_EXPRESSION_ID = "ExpressionId"
const val SQLITE_COLUMN_NAME = "Name"
const val SQLITE_COLUMN_DUE = "Due"
const val SQLITE_COLUMN_PERIOD = "Period"
const val SQLITE_COLUMN_LANG_ORIGINAL = "LangOriginal"
const val SQLITE_COLUMN_LANG_TRANSLATIONS = "LangTranslations"
const val SQLITE_COLUMN_DOMAIN_ID = "DomainId"
const val SQLITE_COLUMN_TYPE = "Type"