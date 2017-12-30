package com.ashalmawia.coriolan.model

class Deck(
        val id: Long,
        val name: String,
        cards: List<Card>
) {

    private val cards = cards.toMutableList()

    fun cards(): List<Card> {
        return ArrayList(cards)
    }

    fun add(new: List<Card>) {
        cards.addAll(new)
    }
}