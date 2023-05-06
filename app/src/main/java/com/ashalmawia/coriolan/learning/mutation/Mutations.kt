package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithProgress

class Mutations(private val mutations: List<Mutation>) {

    fun apply(cards: List<CardWithProgress>): List<CardWithProgress> {
        var list = cards
        mutations.forEach { list = it.apply(list) }
        return list
    }
}