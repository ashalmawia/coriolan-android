package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithState

interface Mutation {

    fun apply(cards: List<CardWithState>): List<CardWithState>
}