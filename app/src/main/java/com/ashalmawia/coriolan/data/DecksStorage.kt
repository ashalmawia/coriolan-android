package com.ashalmawia.coriolan.data

import com.ashalmawia.coriolan.model.Deck

object DecksStorage {

    val default = Deck(1, "Default", CardsStorage.cardsByDeckId(1))

    fun allDecks() :List<Deck> {
        val list = ArrayList<Deck>()

        list.add(default)

        return list
    }
}