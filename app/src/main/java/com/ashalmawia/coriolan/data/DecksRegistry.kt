package com.ashalmawia.coriolan.data

import android.content.Context
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
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

    fun addCardsToDeck(context: Context, data: List<CardData>) {
        data.map { storage(context).addCard(it) }
    }

    fun preferences(context: Context) = Preferences.get(context)

    fun storage(context: Context) = Repository.get(context)

    private fun addDefaultDeck(context: Context, repository: Repository): Deck {
        return repository.addDeck(context.getString(R.string.decks_default))
    }
}