package com.ashalmawia.coriolan.model

import java.lang.IllegalArgumentException

data class Card(
        val id: Long,
        val deckId: Long,
        val domain: Domain,
        val type: CardType,
        val original: Term,
        val translations: List<Term>
)

enum class CardType(val value: String) {
    FORWARD("forward"), REVERSE("reverse");

    companion object {
        fun fromValue(value: String) = values().find { it.value == value }
                ?: throw IllegalArgumentException("unexpected card type: $value")
    }
}