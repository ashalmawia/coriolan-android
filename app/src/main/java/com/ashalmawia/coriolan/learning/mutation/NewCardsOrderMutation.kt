package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithProgress
import com.ashalmawia.coriolan.learning.new
import com.ashalmawia.coriolan.learning.review

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

class OrderAdded: NewCardsOrderMutation() {
    override fun apply(cards: List<CardWithProgress>): List<CardWithProgress> {
        return cards
    }
}

class NewestFirst: NewCardsOrderMutation() {
    override fun apply(cards: List<CardWithProgress>): List<CardWithProgress> {
        return cards.new().reversed().plus(cards.review())
    }
}

class Random : NewCardsOrderMutation() {
    override fun apply(cards: List<CardWithProgress>): List<CardWithProgress> {
        return cards.shuffled()
    }
}