package com.ashalmawia.coriolan.data.storage

import android.content.Context
import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.data.CountsSummary
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteStorage
import com.ashalmawia.coriolan.learning.ExercisesRegistry
import com.ashalmawia.coriolan.learning.scheduler.CardWithState
import com.ashalmawia.coriolan.learning.scheduler.Status
import com.ashalmawia.coriolan.learning.scheduler.sr.SRState
import com.ashalmawia.coriolan.model.*
import org.joda.time.DateTime

interface Repository {

    companion object {
        private lateinit var instance: Repository

        fun get(context: Context): Repository {
            if (!Companion::instance.isInitialized) {
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

    fun createDomain(name: String?, langOriginal: Language, langTranslations: Language): Domain

    fun domainById(id: Long): Domain?

    fun allDomains(): List<Domain>

    fun addCard(domain: Domain, deckId: Long, original: Expression, translations: List<Expression>): Card

    fun cardById(id: Long, domain: Domain): Card?

    fun cardByValues(domain: Domain, original: Expression): Card?

    fun updateCard(card: Card, deckId: Long, original: Expression, translations: List<Expression>): Card?

    fun deleteCard(card: Card)

    fun allCards(domain: Domain): List<Card>

    fun allDecks(domain: Domain): List<Deck>

    fun deckById(id: Long, domain: Domain): Deck?

    fun cardsOfDeck(deck: Deck): List<Card>

    fun addDeck(domain: Domain, name: String): Deck

    fun updateDeck(deck: Deck, name: String): Deck?

    fun deleteDeck(deck: Deck): Boolean

    fun deckPendingCounts(exerciseId: String, deck: Deck, date: DateTime): CountsSummary {
        val due = cardsDueDate(exerciseId, deck, date)
        val total = cardsOfDeck(deck)

        val (forward, reverse) = due.partition { it.card.type == CardType.FORWARD }

        return CountsSummary(
                Counts(
                        forward.count { it.state.status == Status.NEW },
                        forward.count { it.state.status == Status.IN_PROGRESS },
                        forward.count { it.state.status == Status.RELEARN },
                        total.count { it.type == CardType.FORWARD }
                ),
                Counts(
                        reverse.count { it.state.status == Status.NEW },
                        reverse.count { it.state.status == Status.IN_PROGRESS },
                        reverse.count { it.state.status == Status.RELEARN },
                        total.count { it.type == CardType.REVERSE }
                )
        )
    }

    fun updateSRCardState(card: Card, state: SRState, exerciseId: String)

    fun getSRCardState(card: Card, exerciseId: String): SRState

    fun cardsDueDate(exerciseId: String, deck: Deck, date: DateTime): List<CardWithState<SRState>>

    fun invalidateCache()
}

class DataProcessingException(message: String, causedBy: Throwable? = null) : RuntimeException(message, causedBy)