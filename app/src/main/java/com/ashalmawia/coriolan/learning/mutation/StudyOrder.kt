package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.util.new
import com.ashalmawia.coriolan.util.review

enum class StudyOrder {
    ORDER_ADDED,
    RANDOM,
    NEWEST_FIRST
}

class OrderAdded: NewCardsOrderMutation() {
    override fun apply(cards: List<CardWithState>): List<CardWithState> {
        return cards
    }
}

class NewestFirst: NewCardsOrderMutation() {
    override fun apply(cards: List<CardWithState>): List<CardWithState> {
        return cards.new().reversed().plus(cards.review())
    }
}

class Random : NewCardsOrderMutation() {
    override fun apply(cards: List<CardWithState>): List<CardWithState> {
        return cards.shuffled()
    }
}