package com.ashalmawia.coriolan.model

data class Deck(
        val id: Long,
        val domain: Domain,
        val name: String,
        val type: CardType = CardType.UNKNOWN
)