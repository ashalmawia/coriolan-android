package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.stats.DeckStats
import com.ashalmawia.coriolan.learning.CardWithProgress
import com.ashalmawia.coriolan.model.Counts
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.model.mockLearningProgress
import com.ashalmawia.coriolan.model.*
import com.ashalmawia.coriolan.ui.learning.CardTypeFilter
import org.joda.time.DateTime

class MockRepository : Repository {
    val langs = mutableListOf<Language>()
    override fun addLanguage(value: String): Language {
        val lang = Language(langs.size + 1L, value)
        langs.add(lang)
        return lang
    }

    override fun languageById(id: Long): Language? {
        return langs.find { it.id == id }
    }
    override fun languageByName(name: String): Language? {
        return langs.find { it.value == name }
    }

    private val terms = mutableListOf<Term>()
    override fun addTerm(value: String, language: Language, transcription: String?): Term {
        val exp = Term(terms.size + 1L, value, language, transcription)
        terms.add(exp)
        return exp
    }
    override fun updateTerm(term: Term, transcription: String?): Term {
        if (!terms.contains(term)) {
            throw DataProcessingException("term is not in the repo: $term")
        }

        val updated = term.copy(transcription = transcription)
        terms.remove(term)
        terms.add(updated)
        return updated
    }
    override fun termById(id: Long): Term? {
        return terms.find { it.id == id }
    }
    override fun termByValues(value: String, language: Language): Term? {
        return terms.find { it.value == value && it.language == language }
    }
    override fun isUsed(term: Term): Boolean {
        return cards.any { it.original.id == term.id || it.translations.any { it.id == term.id } }
    }
    override fun deleteTerm(term: Term) {
        terms.remove(term)
    }

    private val domains = mutableListOf<Domain>()
    override fun createDomain(name: String?, langOriginal: Language, langTranslations: Language): Domain {
        val domain = Domain(domains.size + 1L, name, langOriginal, langTranslations)
        domains.add(domain)
        return domain
    }
    override fun domainById(id: Long): Domain? = domains.find { it.id == id }
    override fun allDomains(): List<Domain> {
        return domains
    }

    val cards = mutableListOf<Card>()
    override fun addCard(domain: Domain, deckId: Long, original: Term, translations: List<Term>): Card {
        val card = Card(
                cards.size + 1L,
                deckId,
                domain,
                if (domain.langOriginal() == original.language) CardType.FORWARD else CardType.REVERSE,
                original,
                translations
        )
        cards.add(card)
        return card
    }
    override fun cardById(id: Long, domain: Domain): Card? {
        return cards.find { it.id == id }
    }
    override fun cardByValues(domain: Domain, original: Term): Card? {
        return cards.find { it.original.id == original.id }
    }
    override fun updateCard(card: Card, deckId: Long, original: Term, translations: List<Term>): Card {
        if (!cards.contains(card)) {
            throw DataProcessingException("card is not in the repo: $card")
        }

        val updated = Card(card.id, deckId, card.domain, card.type, original, translations)
        cards.remove(card)
        cards.add(updated)
        return updated
    }
    override fun deleteCard(card: Card) {
        cards.remove(card)
    }
    override fun allCards(domain: Domain): List<Card> {
        return cards
    }

    val decks = mutableListOf<Deck>()
    override fun allDecks(domain: Domain): List<Deck> {
        return decks
    }
    override fun allDecksCardsCount(domain: Domain): Map<Long, Int> {
        return decks.associate {
            val id = it.id
            val count = cards.count { it.deckId == id }
            Pair(id, count)
        }.filter { it.value > 0 }
    }
    override fun deckById(id: Long): Deck {
        return decks.find { it.id == id } ?: throw DataProcessingException("could not find id $id")
    }
    override fun cardsOfDeck(deck: Deck): List<Card> {
        return cards.filter { it.deckId == deck.id }
    }
    override fun addDeck(domain: Domain, name: String): Deck {
        val deck = Deck(decks.size + 1L, domain, name)
        decks.add(deck)
        return deck
    }
    override fun updateDeck(deck: Deck, name: String): Deck {
        if (!decks.contains(deck)) {
            throw DataProcessingException("deck is not in the repo: $deck")
        }

        val updated = Deck(deck.id, deck.domain, name)
        decks.remove(deck)
        decks.add(updated)
        return updated
    }
    override fun deleteDeck(deck: Deck): Boolean {
        if (!decks.contains(deck)) {
            throw DataProcessingException("deck $deck was not found")
        }
        return if (cardsOfDeck(deck).isEmpty()) {
            decks.remove(deck)
            true
        } else {
            false
        }
    }

    override fun deckPendingCounts(deck: Deck, cardType: CardType, date: DateTime): Counts {
        val due = pendingCards(deck, date)

        val deckDue = due.filter { it.card.type == cardType }

        return Counts(
                deckDue.count { it.status == Status.NEW },
                deckDue.count { it.status == Status.IN_PROGRESS
                        || it.status == Status.LEARNT },
                deckDue.count { it.status == Status.RELEARN }
        )
    }

    override fun deckStats(deck: Deck): Map<CardTypeFilter, DeckStats> {
        val cards = cards
                .filter { it.deckId == deck.id }
                .associateWith { card -> states[card.id] ?: mockLearningProgress() }
        val forward = cards.filterKeys { it.type == CardType.FORWARD }
        val reverse = cards.filterKeys { it.type == CardType.REVERSE }
        return mapOf(
                CardTypeFilter.FORWARD to DeckStats(
                        forward.count { (_, status) -> status.status == Status.NEW },
                        forward.count { (_, status) -> status.status == Status.IN_PROGRESS || status.status == Status.RELEARN },
                        forward.count { (_, status) -> status.status == Status.LEARNT }
                ),
                CardTypeFilter.REVERSE to DeckStats(
                        reverse.count { (_, status) -> status.status == Status.NEW },
                        reverse.count { (_, status) -> status.status == Status.IN_PROGRESS || status.status == Status.RELEARN },
                        reverse.count { (_, status) -> status.status == Status.LEARNT }
                ),
                CardTypeFilter.BOTH to DeckStats(
                        cards.count { (_, status) -> status.status == Status.NEW },
                        cards.count { (_, status) -> status.status == Status.IN_PROGRESS || status.status == Status.RELEARN },
                        cards.count { (_, status) -> status.status == Status.LEARNT }
                )
        )
    }

    val states = mutableMapOf<Long, LearningProgress>()
    override fun updateCardLearningProgress(card: Card, learningProgress: LearningProgress) {
        if (!cards.contains(card)) {
            throw DataProcessingException("card is not in the repo: $card")
        }

        states[card.id] = learningProgress
    }
    override fun getCardLearningProgress(card: Card): LearningProgress {
        return states[card.id] ?: mockLearningProgress()
    }
    override fun pendingCards(deck: Deck, date: DateTime): List<CardWithProgress> {
        return cardsOfDeck(deck)
                .map { card -> CardWithProgress(card, getCardLearningProgress(card)) }
                .filter { (_, progress) ->
                    progress.state.due <= date
                }
    }
    override fun getProgressForCardsWithOriginals(originalIds: List<Long>): Map<Long, LearningProgress> {
        val cards = originalIds.mapNotNull { cards.find { card -> card.original.id == it } }
        return cards.associateBy(
                { it.original.id },
                { states[it.id] ?: mockLearningProgress() }
        )
    }

    override fun invalidateCache() {
        // nothing to do here
    }
}

fun Repository.justAddTerm(value: String, language: Language) =
        addTerm(value, language, null)