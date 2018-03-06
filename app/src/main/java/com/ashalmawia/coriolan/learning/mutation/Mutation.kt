package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.model.Card

interface Mutation {

    fun apply(cards: List<Card>): List<Card>
}