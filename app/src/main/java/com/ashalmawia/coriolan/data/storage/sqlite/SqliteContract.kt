package com.ashalmawia.coriolan.data.storage.sqlite

import com.ashalmawia.coriolan.learning.Exercise

const val SQLITE_TABLE_EXPRESSIONS = "Expressions"
const val SQLITE_TABLE_CARDS = "Cards"
const val SQLITE_TABLE_CARDS_REVERSE = "CardsReverse"
const val SQLITE_TABLE_DECKS = "Decks"

fun sqliteTableExerciseState(exercise: Exercise): String {
    return "State_${exercise.stableId}"
}

const val SQLITE_COLUMN_ID = "_id"
const val SQLITE_COLUMN_VALUE = "Value"
const val SQLITE_COLUMN_TYPE = "Type"
const val SQLITE_COLUMN_FRONT_ID = "FrontId"
const val SQLITE_COLUMN_DECK_ID = "DeckId"
const val SQLITE_COLUMN_CARD_ID = "CardId"
const val SQLITE_COLUMN_EXPRESSION_ID = "ExpressionId"
const val SQLITE_COLUMN_NAME = "Name"
const val SQLITE_COLUMN_DUE = "Due"
const val SQLITE_COLUMN_PERIOD = "Period"