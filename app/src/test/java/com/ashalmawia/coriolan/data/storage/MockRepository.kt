package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.model.mockLearningProgress
import com.ashalmawia.coriolan.model.*
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
    override fun addTerm(value: String, language: Language, extras: Extras?): Term {
        val exp = Term(terms.size + 1L, value, language, extras ?: Extras.empty())
        terms.add(exp)
        return exp
    }
    override fun updateTerm(term: Term, extras: Extras?): Term {
        if (!terms.contains(term)) {
            throw DataProcessingException("term is not in the repo: $term")
        }

        val updated = term.copy(extras = extras ?: Extras.empty())
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

        val updated = Card(card.id, deckId, card.domain, original, translations)
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
    override fun deckById(id: Long, domain: Domain): Deck? {
        return decks.find { it.id == id }
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
    override fun pendingCards(deck: Deck, date: DateTime): List<Pair<Card, LearningProgress>> {
        return cardsOfDeck(deck)
                .map { card -> Pair(card, getCardLearningProgress(card)) }
                .filter { (_, progress) ->
                    progress.states.isEmpty() || progress.states.any { it.value.due <= date }
                }
    }
    override fun getStatesForCardsWithOriginals(originalIds: List<Long>): Map<Long, LearningProgress> {
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