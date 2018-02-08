package com.ashalmawia.coriolan.model

import com.ashalmawia.coriolan.data.LanguagesRegistry
import com.ashalmawia.coriolan.learning.scheduler.State

data class Card(
        val id: Long,
        val deckId: Long,
        val original: Expression,
        val translations: List<Expression>,
        var state: State
) {
    val type: CardType
        get() {
            return if (original.language == LanguagesRegistry.original()) CardType.FORWARD else CardType.REVERSE
        }
}

enum class CardType(val value: Int) {
    UNKNOWN(-1), FORWARD(0), REVERSE(1)
}

fun toCardType(value: Int): CardType {
    return CardType.values().find { it.value == value } ?: CardType.UNKNOWN
}