package com.ashalmawia.coriolan.data

import android.content.Context
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Storage
import com.ashalmawia.coriolan.model.Deck

object DecksRegistry {
    private lateinit var def: Deck

    fun preinitialize(context: Context) {
        val prefs = preferences(context)
        val defaultDeckId = prefs.getDefaultDeckId()

        val storage = storage(context)

        if (defaultDeckId != null) {
            def = storage.deckById(defaultDeckId)!!
        } else {
            def = addDefaultDeck(context, storage)
            prefs.setDefaultDeckId(def.id)
        }
    }

    fun default(): Deck {
        return def
    }

    fun allDecks(context: Context): List<Deck> {
        return storage(context).allDecks()
    }

    fun addCardsToDeck(context: Context, deck: Deck, data: List<CardData>) {
        val cards = data.map { storage(context).addCard(it) }
        deck.add(cards)
    }

    fun preferences(context: Context) = Preferences.get(context)

    fun storage(context: Context) = Storage.get(context)

    private fun addDefaultDeck(context: Context, storage: Storage): Deck {
        return storage.addDeck(context.getString(R.string.decks_default))
    }
}