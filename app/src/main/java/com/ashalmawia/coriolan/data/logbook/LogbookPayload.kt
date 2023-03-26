package com.ashalmawia.coriolan.data.logbook

import com.ashalmawia.coriolan.learning.exercise.CardAction
import com.ashalmawia.coriolan.learning.exercise.ExerciseId

data class LogbookPayload(
        val cardActions: MutableMap<ExerciseId, MutableMap<CardAction, Int>>
)