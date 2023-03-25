package com.ashalmawia.coriolan.data.journal.sqlite

import android.database.Cursor
import com.ashalmawia.coriolan.util.getInt

fun Cursor.getCardsFirstSeen(alias: String? = null): Int { return getInt(SQLITE_COLUMN_CARDS_FIRST_SEEN, alias) }
fun Cursor.getCardsReviewed(alias: String? = null): Int { return getInt(SQLITE_COLUMN_CARDS_REVIEWED, alias) }
fun Cursor.getCardsRelearned(alias: String? = null): Int { return getInt(SQLITE_COLUMN_CARDS_RELEARNED, alias) }