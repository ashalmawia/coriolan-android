package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.util.new
import com.ashalmawia.coriolan.util.review

enum class StudyOrder {
    ORDER_ADDED,
    RANDOM,
    NEWEST_FIRST
}

class OrderAdded<S : State>: Mutation.NewCardsOrder<S>() {
    override fun apply(cards: List<CardWithState<S>>): List<CardWithState<S>> {
        return cards
    }
}

class NewestFirst<S : State>: Mutation.NewCardsOrder<S>() {
    override fun apply(cards: List<CardWithState<S>>): List<CardWithState<S>> {
        return cards.new().reversed().plus(cards.review())
    }
}

class Random<S : State> : Mutation.NewCardsOrder<S>() {
    override fun apply(cards: List<CardWithState<S>>): List<CardWithState<S>> {
        return cards.shuffled()
    }
}