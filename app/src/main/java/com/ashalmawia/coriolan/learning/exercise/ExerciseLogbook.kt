package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.model.Card

interface ExerciseLogbook {

    fun recordCardStudied(card: Card, oldState: State, newState: State)

    fun undoCardStudied(card: Card, state: State, stateThatWasUndone: State)
}