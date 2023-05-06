package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.CardWithProgress

interface Mutation {

    fun apply(cards: List<CardWithProgress>): List<CardWithProgress>
}