package com.ashalmawia.coriolan.data.journal

import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import org.joda.time.DateTime

interface Journal {

    fun cardsStudiedOnDate(date: DateTime): Counts
    fun cardsStudiedOnDate(date: DateTime, exercise: ExerciseId): Counts

    fun incrementCardStudied(date: DateTime, targetStatus: Status, exercise: ExerciseId)
    fun decrementCardStudied(date: DateTime, targetStatus: Status, exercise: ExerciseId)
}