package com.ashalmawia.coriolan.data.storage

import androidx.annotation.VisibleForTesting
import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.model.*
import org.joda.time.DateTime

interface Repository {

    fun addLanguage(value: String): Language

    fun languageById(id: Long): Language?

    fun languageByName(name: String): Language?

    fun addTerm(value: String, language: Language, extras: Extras?): Term

    fun updateTerm(term: Term, extras: Extras?): Term

    fun termById(id: Long): Term?

    fun termByValues(value: String, language: Language): Term?

    fun isUsed(term: Term): Boolean

    fun deleteTerm(term: Term)

    fun createDomain(name: String?, langOriginal: Language, langTranslations: Language): Domain

    fun domainById(id: Long): Domain?

    fun allDomains(): List<Domain>

    fun addCard(domain: Domain, deckId: Long, original: Term, translations: List<Term>): Card

    fun cardById(id: Long, domain: Domain): Card?

    fun cardByValues(domain: Domain, original: Term): Card?

    fun updateCard(card: Card, deckId: Long, original: Term, translations: List<Term>): Card

    fun deleteCard(card: Card)

    @VisibleForTesting
    fun allCards(domain: Domain): List<Card>

    fun allDecks(domain: Domain): List<Deck>

    fun deckById(id: Long, domain: Domain): Deck?

    fun cardsOfDeck(deck: Deck): List<Card>

    fun addDeck(domain: Domain, name: String): Deck

    fun updateDeck(deck: Deck, name: String): Deck

    fun deleteDeck(deck: Deck): Boolean

    fun deckPendingCounts(deck: Deck, cardType: CardType, date: DateTime): Counts

    fun updateCardLearningProgress(card: Card, learningProgress: LearningProgress)

    fun getCardLearningProgress(card: Card): LearningProgress

    fun pendingCards(deck: Deck, date: DateTime): List<Pair<Card, LearningProgress>>

    fun getStatesForCardsWithOriginals(originalIds: List<Long>): Map<Long, LearningProgress>

    fun invalidateCache()
}

class DataProcessingException(message: String, causedBy: Throwable? = null) : RuntimeException(message, causedBy)