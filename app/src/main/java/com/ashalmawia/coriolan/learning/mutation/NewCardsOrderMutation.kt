package com.ashalmawia.coriolan.learning.mutation

abstract class NewCardsOrderMutation : Mutation {
    companion object {
        fun from(order: StudyOrder) : NewCardsOrderMutation {
            return when (order) {
                StudyOrder.ORDER_ADDED -> OrderAdded()
                StudyOrder.RANDOM -> Random()
                StudyOrder.NEWEST_FIRST -> NewestFirst()
            }
        }
    }
}