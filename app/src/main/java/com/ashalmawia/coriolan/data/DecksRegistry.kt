package com.ashalmawia.coriolan.data

import android.content.Context
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Deck

class DecksRegistry(context: Context, preferences: Preferences, private val repository: Repository) {

    companion object {
        private lateinit var instance: DecksRegistry

        fun get(): DecksRegistry {
            return instance
        }

        fun initialize(context: Context, preferences: Preferences, repository: Repository) {
            instance = DecksRegistry(context, preferences, repository)
        }
    }

    private val def: Deck

    init {
        val defaultDeckId = preferences.getDefaultDeckId()
        if (defaultDeckId != null) {
            def = repository.deckById(defaultDeckId)!!
        } else {
            def = addDefaultDeck(context, repository)
            preferences.setDefaultDeckId(def.id)
        }
    }

    fun default(): Deck {
        return def
    }

    fun allDecks(): List<Deck> {
        return repository.allDecks()
    }

    fun addCardsToDeck(data: List<CardData>) {
        data.map { addCard(it) }
    }

    /**
     * For each card it will add a forward card and all possible reverse cards.
     *
     * E.g. for the input "spring -- 1. весна 2. источник" it will generate 2 cards:
     * 1. forward: "spring -- 1. весна 2. источник"
     * 2. reverse: "весна -- spring"
     * 3. reverse: "источник -- spring"
     */
    private fun addCard(cardData: CardData) {
        repository.addCard(cardData)
        CardData.reversedTo(cardData).forEach { repository.addCard(it) }
    }

    private fun addDefaultDeck(context: Context, repository: Repository): Deck {
        return repository.addDeck(context.getString(R.string.decks_default))
    }
}