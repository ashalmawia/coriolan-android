package com.ashalmawia.coriolan.ui.learning

import com.ashalmawia.coriolan.model.CardType

enum class CardTypeFilter {
    FORWARD, REVERSE, BOTH;

    fun toCardType(): CardType {
        return when (this) {
            FORWARD -> CardType.FORWARD
            REVERSE -> CardType.REVERSE
            BOTH -> throw IllegalStateException("BOTH does not convert to a card type")
        }
    }
}