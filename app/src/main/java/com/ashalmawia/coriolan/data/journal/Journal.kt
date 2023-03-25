package com.ashalmawia.coriolan.data.journal

import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.learning.exercise.CardAction
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import org.joda.time.DateTime

interface Journal {

    fun cardsStudiedOnDate(date: DateTime): Counts
    fun cardsStudiedOnDate(date: DateTime, exercise: ExerciseId): Counts

    fun incrementCardActions(date: DateTime, exercise: ExerciseId, cardAction: CardAction)
    fun decrementCardActions(date: DateTime, exercise: ExerciseId, cardAction: CardAction)
}