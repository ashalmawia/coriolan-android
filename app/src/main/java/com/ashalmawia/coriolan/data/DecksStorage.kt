package com.ashalmawia.coriolan.data

import com.ashalmawia.coriolan.model.Deck

object DecksStorage {

    fun allDecks() :List<Deck> {
        val list = ArrayList<Deck>()

        val name = "Default"
        list.add(Deck(name, CardsStorage.cardsByDeckName(name)))

        return list
    }
}