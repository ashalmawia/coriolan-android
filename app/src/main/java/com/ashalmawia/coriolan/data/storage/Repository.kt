package com.ashalmawia.coriolan.data.storage

import android.content.Context
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteStorage
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.ExercisesRegistry
import com.ashalmawia.coriolan.learning.assignment.Counts
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.model.*
import org.joda.time.DateTime

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

    fun addLanguage(value: String): Language

    fun languageById(id: Long): Language?

    fun addExpression(value: String, type: ExpressionType, language: Language): Expression

    fun expressionById(id: Long): Expression?

    fun expressionByValues(value: String, type: ExpressionType, language: Language): Expression?

    fun isUsed(expression: Expression): Boolean

    fun deleteExpression(expression: Expression)

    fun createDomain(name: String, langOriginal: Language, langTranslations: Language): Domain

    fun allDomains(): List<Domain>

    fun addCard(domainId: Long, deckId: Long, original: Expression, translations: List<Expression>): Card

    fun cardById(id: Long): Card?

    fun updateCard(card: Card, deckId: Long, original: Expression, translations: List<Expression>): Card?

    fun deleteCard(card: Card)

    fun allDecks(): List<Deck>

    fun deckById(id: Long): Deck?

    fun cardsOfDeck(deck: Deck): List<Card>

    fun addDeck(domainId: Long, name: String): Deck

    fun updateCardState(card: Card, state: State, exercise: Exercise): Card

    fun cardsDueDate(exercise: Exercise, deck: Deck, date: DateTime): List<Card>

    fun cardsDueDateCount(exercise: Exercise, deck: Deck, date: DateTime): Counts
}