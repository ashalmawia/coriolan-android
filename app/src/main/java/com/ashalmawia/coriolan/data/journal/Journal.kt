package com.ashalmawia.coriolan.data.journal

import android.content.Context
import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.data.journal.sqlite.SqliteJournal
import org.joda.time.DateTime

interface Journal {

    companion object {
        private lateinit var instance: Journal

        fun get(context: Context): Journal {
            if (!Journal.Companion::instance.isInitialized) {
                instance = SqliteJournal(context)
            }
            return instance
        }
    }

    fun cardsStudiedOnDate(date: DateTime): Counts

    fun recordNewCardStudied(date: DateTime)
    fun recordReviewStudied(date: DateTime)
    fun recordCardRelearned(date: DateTime)

    fun undoNewCardStudied(date: DateTime)
    fun undoReviewStudied(date: DateTime)
    fun undoCardRelearned(date: DateTime)
}