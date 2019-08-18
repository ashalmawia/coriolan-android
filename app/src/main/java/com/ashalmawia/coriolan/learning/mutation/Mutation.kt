package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.State

interface Mutation<S : State> {

    fun apply(cards: List<CardWithState<S>>): List<CardWithState<S>>
}