package com.ashalmawia.coriolan.data.journal.sqlite

import android.database.Cursor
import com.ashalmawia.coriolan.util.getInt

fun Cursor.getCardsNew(alias: String? = null): Int { return getInt(SQLITE_COLUMN_CARDS_NEW, alias) }
fun Cursor.getCardsReview(alias: String? = null): Int { return getInt(SQLITE_COLUMN_CARDS_REVIEW, alias) }
fun Cursor.getCardsRelearn(alias: String? = null): Int { return getInt(SQLITE_COLUMN_CARDS_RELEARN, alias) }