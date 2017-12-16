package com.ashalmawia.coriolan.model

class Deck(
        val name: String,
        private val cards: List<Card>
) {

    fun cards(): List<Card> {
        return ArrayList(cards)
    }
}