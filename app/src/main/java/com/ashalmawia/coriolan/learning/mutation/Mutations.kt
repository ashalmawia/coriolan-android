package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.Task

class Mutations(private val mutations: List<Mutation>) {

    fun apply(cards: List<Task>): List<Task> {
        var list = cards
        mutations.forEach { list = it.apply(list) }
        return list
    }
}