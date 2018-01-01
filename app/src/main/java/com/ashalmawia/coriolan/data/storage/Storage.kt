package com.ashalmawia.coriolan.data.storage

import android.content.Context
import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteStorage
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Expression
import com.ashalmawia.coriolan.model.ExpressionType

interface Storage {

    companion object {
        private lateinit var instance: Storage

        fun get(context: Context): Storage {
            if (!this::instance.isInitialized) {
                instance = InMemoryCache(SqliteStorage(context))
            }
            return instance
        }
    }

    fun addExpression(value: String, type: ExpressionType): Expression

    fun expressionById(id: Long): Expression?

    fun addCard(data: CardData): Card

    fun cardById(id: Long): Card?

    fun allDecks() :List<Deck>

    fun deckById(id: Long): Deck?

    fun addDeck(name: String): Deck
}