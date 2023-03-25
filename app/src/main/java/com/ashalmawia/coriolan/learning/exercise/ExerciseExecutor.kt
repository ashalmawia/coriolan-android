package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.ExpressionExtras

interface ExerciseExecutor : ExerciseRenderer.Listener {

    val canUndo: Boolean

    fun isPending(card: CardWithState): Boolean

    fun getCardWithState(card: Card): CardWithState

    fun renderCard(card: CardWithState, extras: List<ExpressionExtras>)

    fun undoCard(card: CardWithState, undoneState: State): CardWithState
}

interface ExerciseListener {

    fun onCardStudied(updated: CardWithState)
}