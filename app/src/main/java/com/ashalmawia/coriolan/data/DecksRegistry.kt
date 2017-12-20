package com.ashalmawia.coriolan.data

import android.content.Context
import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.data.storage.Storage
import com.ashalmawia.coriolan.model.Deck

object DecksRegistry {

    private const val DEFAULT_DECK_ID = 1

    private lateinit var def: Deck

    fun default(context: Context): Deck {
        if (!DecksRegistry::def.isInitialized) {
            def = Storage.get(context).deckById(DEFAULT_DECK_ID)!!
        }
        return def
    }

    fun allDecks() :List<Deck> {
        val list = ArrayList<Deck>()

        list.add(def)

        return list
    }

    fun addCardsToDeck(context: Context, deck: Deck, data: List<CardData>) {
        val cards = data.map { Storage.get(context).addCard(it) }
        deck.add(cards)
    }
}