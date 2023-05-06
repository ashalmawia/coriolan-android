package com.ashalmawia.coriolan.data.storage

import androidx.annotation.VisibleForTesting
import com.ashalmawia.coriolan.data.stats.DeckStats
import com.ashalmawia.coriolan.learning.CardWithProgress
import com.ashalmawia.coriolan.model.Counts
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.model.*
import com.ashalmawia.coriolan.ui.learning.CardTypeFilter
import org.joda.time.DateTime

interface Repository {

    fun addLanguage(value: String): Language

    fun languageById(id: Long): Language?

    fun languageByName(name: String): Language?

    fun addTerm(value: String, language: Language, transcription: String?): Term

    fun updateTerm(term: Term, transcription: String?): Term

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

    fun allDecksWithPendingCounts(domain: Domain, date: DateTime): Map<Deck, PendingCardsCount> {
        return allDecks(domain).associateWith { deck ->
            val forward = deckPendingCounts(deck, CardType.FORWARD, date)
            val reverse = deckPendingCounts(deck, CardType.REVERSE, date)
            PendingCardsCount(forward.total, reverse.total)
        }
    }

    fun allDecksCardsCount(domain: Domain): Map<Long, Int>

    fun deckById(id: Long): Deck

    fun cardsOfDeck(deck: Deck): List<Card>

    fun addDeck(domain: Domain, name: String): Deck

    fun updateDeck(deck: Deck, name: String): Deck

    fun deleteDeck(deck: Deck): Boolean

    fun deckStats(deck: Deck): Map<CardTypeFilter, DeckStats>

    fun deckPendingCounts(deck: Deck, cardType: CardType, date: DateTime): Counts

    fun deckPendingCountsMix(deck: Deck, date: DateTime): Counts {
        return deckPendingCounts(deck, CardType.FORWARD, date) + deckPendingCounts(deck, CardType.REVERSE, date)
    }

    fun updateCardLearningProgress(card: Card, learningProgress: LearningProgress)

    fun getCardLearningProgress(card: Card): LearningProgress

    fun pendingCards(deck: Deck, date: DateTime): List<CardWithProgress>

    fun getStatesForCardsWithOriginals(originalIds: List<Long>): Map<Long, LearningProgress>

    fun invalidateCache()
}

class DataProcessingException(message: String, causedBy: Throwable? = null) : RuntimeException(message, causedBy)