package com.ashalmawia.coriolan.data.journal

import com.ashalmawia.coriolan.data.Counts
import org.joda.time.DateTime

interface Journal {

    fun cardsStudiedOnDate(date: DateTime): Counts

    fun recordNewCardStudied(date: DateTime)
    fun recordReviewStudied(date: DateTime)
    fun recordCardRelearned(date: DateTime)

    fun undoNewCardStudied(date: DateTime)
    fun undoReviewStudied(date: DateTime)
    fun undoCardRelearned(date: DateTime)
}