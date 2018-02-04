package com.ashalmawia.coriolan.model

import com.ashalmawia.coriolan.learning.scheduler.State

data class Card(
        val id: Long,
        val deckId: Long,
        val original: Expression,
        val translations: List<Expression>,
        var state: State
)