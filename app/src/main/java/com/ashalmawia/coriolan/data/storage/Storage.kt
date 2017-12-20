package com.ashalmawia.coriolan.data.storage

import android.content.Context
import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.data.storage.stub.StubStorage
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck

interface Storage {

    companion object {
        private lateinit var instance: Storage

        fun get(context: Context): Storage {
            if (!this::instance.isInitialized) {
                instance = StubStorage()
            }
            return instance
        }
    }

    fun addCard(data: CardData): Card

    fun cardsByDeckId(id: Int): List<Card>

    fun allDecks() :List<Deck>

    fun deckById(id: Int): Deck?
}