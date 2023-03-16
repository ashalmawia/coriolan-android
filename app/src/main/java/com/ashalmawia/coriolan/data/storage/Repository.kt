package com.ashalmawia.coriolan.data.storage

import androidx.annotation.VisibleForTesting
import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.exercise.sr.SRState
import com.ashalmawia.coriolan.model.*
import org.joda.time.DateTime

interface Repository {

    fun addLanguage(value: String): Language

    fun languageById(id: Long): Language?

    fun languageByName(name: String): Language?

    fun addExpression(value: String, language: Language): Expression

    fun setTranscription(expression: Expression, transcription: String?)
            = setExtra(expression, ExtraType.TRANSCRIPTION, transcription)

    fun setExtra(expression: Expression, type: ExtraType, value: String?)

    fun allExtrasForExpression(expression: Expression): ExpressionExtras

    fun allExtrasForCard(card: Card): List<ExpressionExtras>

    fun expressionById(id: Long): Expression?

    fun expressionByValues(value: String, language: Language): Expression?

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

    @VisibleForTesting
    fun allCards(domain: Domain): List<Card>

    fun allDecks(domain: Domain): List<Deck>

    fun deckById(id: Long, domain: Domain): Deck?

    fun cardsOfDeck(deck: Deck): List<Card>

    fun addDeck(domain: Domain, name: String): Deck

    fun updateDeck(deck: Deck, name: String): Deck?

    fun deleteDeck(deck: Deck): Boolean

    fun deckPendingCounts(exerciseId: String, deck: Deck, cardType: CardType, date: DateTime): Counts {
        val due = cardsDueDate(exerciseId, deck, date)
        val total = cardsOfDeck(deck)

        val deckDue = due.filter { it.card.type == cardType }

        return Counts(
                deckDue.count { it.state.status == Status.NEW },
                deckDue.count { it.state.status == Status.IN_PROGRESS || it.state.status == Status.LEARNT },
                deckDue.count { it.state.status == Status.RELEARN },
                total.filter { it.type == cardType }.size
        )
    }

    fun updateSRCardState(card: Card, state: SRState, exerciseId: String)

    fun getSRCardState(card: Card, exerciseId: String): SRState

    fun cardsDueDate(exerciseId: String, deck: Deck, date: DateTime): List<CardWithState<SRState>>

    fun getStatesForCardsWithOriginals(originalIds: List<Long>, exerciseId: String): Map<Long, SRState>

    fun invalidateCache()
}

class DataProcessingException(message: String, causedBy: Throwable? = null) : RuntimeException(message, causedBy)