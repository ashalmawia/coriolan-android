package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithState

class Mutations(private val mutations: List<Mutation>) {

    fun apply(cards: List<CardWithState>): List<CardWithState> {
        var list = cards
        mutations.forEach { list = it.apply(list) }
        return list
    }
}