package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.model.Card

class Mutations(private val mutations: List<Mutation>) : Mutation {

    override fun apply(cards: List<Card>): List<Card> {
        var list = cards
        mutations.forEach { list = it.apply(list) }
        return list
    }
}