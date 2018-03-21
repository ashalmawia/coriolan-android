package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.scheduler.CardWithState
import com.ashalmawia.coriolan.learning.scheduler.State

class Mutations(private val mutations: List<Mutation>) : Mutation {

    override fun <S : State> apply(cards: List<CardWithState<S>>): List<CardWithState<S>> {
        var list = cards
        mutations.forEach { list = it.apply(list) }
        return list
    }
}