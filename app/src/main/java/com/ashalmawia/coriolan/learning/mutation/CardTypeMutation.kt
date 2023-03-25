package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.model.CardType

class CardTypeMutation(private val cardType: CardType) : Mutation {

    override fun apply(tasks: List<Task>): List<Task> {
        return tasks.filter { it.card.type == cardType }
    }
}