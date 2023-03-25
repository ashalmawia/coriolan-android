package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.model.Card

interface ExerciseLogbook {

    fun recordCardAction(card: Card, oldState: State, newState: State)

    fun unrecordCardAction(card: Card, state: State, stateThatWasUndone: State)
}

enum class CardAction(val value: String) {
    NEW_CARD_FIRST_SEEN("opened"),
    CARD_REVIEWED("reviewed"),
    CARD_RELEARNED("relearned")
}