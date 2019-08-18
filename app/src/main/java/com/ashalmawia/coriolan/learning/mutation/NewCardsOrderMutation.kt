package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.State

abstract class NewCardsOrderMutation<S : State> : Mutation<S> {
    companion object {
        fun <S : State> from(order: StudyOrder) : NewCardsOrderMutation<S> {
            return when (order) {
                StudyOrder.ORDER_ADDED -> OrderAdded()
                StudyOrder.RANDOM -> Random()
                StudyOrder.NEWEST_FIRST -> NewestFirst()
            }
        }
    }
}