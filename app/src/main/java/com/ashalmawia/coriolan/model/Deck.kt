package com.ashalmawia.coriolan.model

import com.ashalmawia.coriolan.data.DecksStorage

class Deck(
        val id: Int,
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