package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.State

class Mutations<S : State>(private val mutations: List<Mutation<S>>) {

    fun apply(cards: List<CardWithState<S>>): List<CardWithState<S>> {
        var list = cards
        mutations.forEach { list = it.apply(list) }
        return list
    }
}