package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.scheduler.CardWithState
import com.ashalmawia.coriolan.learning.scheduler.State

interface Mutation {

    fun <S : State>  apply(cards: List<CardWithState<S>>): List<CardWithState<S>>
}