package com.ashalmawia.coriolan.learning.exercise

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.model.ExpressionExtras

interface ExerciseRenderer<S : State, A> {

    fun prepareUi(context: Context, parentView: ViewGroup): View

    fun renderCard(card: CardWithState<S>, extras: List<ExpressionExtras>)

    interface Listener<A> {
        fun onAnswered(answer: A)
    }
}