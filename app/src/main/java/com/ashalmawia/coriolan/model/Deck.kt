package com.ashalmawia.coriolan.model

import java.io.Serializable

data class Deck(
        val id: DeckId,
        val domain: Domain,
        val name: String
)

data class DeckId(val value: Long) : Serializable {

    fun asString() = value.toString()
}