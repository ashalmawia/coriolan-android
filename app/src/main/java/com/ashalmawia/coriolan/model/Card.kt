package com.ashalmawia.coriolan.model

import com.ashalmawia.coriolan.learning.scheduler.State

data class Card constructor(
        val id: Long,
        val original: Expression,
        val translations: List<Expression>,
        var state: State
)