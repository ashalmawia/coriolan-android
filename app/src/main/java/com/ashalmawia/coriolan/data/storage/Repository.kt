package com.ashalmawia.coriolan.data.storage

import android.content.Context
import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteStorage
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.ExercisesRegistry
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Expression
import com.ashalmawia.coriolan.model.ExpressionType
import java.util.*

interface Repository {

    companion object {
        private lateinit var instance: Repository

        fun get(context: Context): Repository {
            if (!this::instance.isInitialized) {
                instance = InMemoryCache(SqliteStorage(context.applicationContext, ExercisesRegistry.allExercises()))
            }
            return instance
        }
    }

    fun addExpression(value: String, type: ExpressionType): Expression

    fun expressionById(id: Long): Expression?

    fun addCard(data: CardData): Card

    fun cardById(id: Long): Card?

    fun allDecks(): List<Deck>

    fun deckById(id: Long): Deck?

    fun addDeck(name: String): Deck

    fun updateCardState(card: Card, state: State, exercise: Exercise): Card

    fun cardsDueDate(exercise: Exercise, deck: Deck, date: Date): List<Card>
}