package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.exercise.MockEmptyStateProvider
import com.ashalmawia.coriolan.learning.exercise.mockEmptyState
import com.ashalmawia.coriolan.learning.exercise.sr.SRState
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.model.*
import org.junit.Assert.*
import org.junit.Test

abstract class StorageTest {

    private val today = mockToday()
    private fun emptyState() = mockEmptyState(today)

    protected val mockEmptyStateProvider = MockEmptyStateProvider(today)

    private lateinit var domain: Domain

    private val prefilledStorage: Lazy<Repository> = lazy {
        val it = createStorage()
        addMockLanguages(it)
        domain = it.createDomain("Default", langOriginal(), langTranslations())
        it
    }
    private val emptyStorage: Lazy<Repository> = lazy { createStorage() }

    protected abstract fun createStorage(): Repository

    private fun addMockDeck(storage: Repository): Deck {
        return storage.addDeck(domain, "Mock")
    }

    private fun addMockCard(storage: Repository, deckId: Long): Card {
        return addMockCard(storage, deckId, "original ${System.nanoTime()}", listOf(
                "translation first ${System.nanoTime()}",
                "translation second ${System.nanoTime()}"
        ), domain)
    }

    @Test
    fun test__addLanguage() {
        // given
        val storage = emptyStorage.value
        val value = "Russian"

        // when
        val language = storage.addLanguage(value)

        // then
        assertLanguageCorrect(language, value)
    }

    @Test
    fun test__languageById__languageExists() {
        // given
        val storage = emptyStorage.value

        val value = "Russian"
        storage.addLanguage("Some language")
        val language = storage.addLanguage(value)
        storage.addLanguage("Other language")

        // when
        val read = storage.languageById(language.id)

        // then
        assertLanguageCorrect(read, language.value)
        assertEquals("languages match", language, read)
    }

    @Test
    fun test__languageById__languageDoesNotExist() {
        // given
        val storage = emptyStorage.value

        // when
        val read = storage.languageById(777L)

        // then
        assertNull(read)
    }

    @Test
    fun test__languageByName__languageExists() {
        // given
        val storage = emptyStorage.value

        val value = "Russian"
        storage.addLanguage("Some language")
        val language = storage.addLanguage(value)
        storage.addLanguage("Other language")

        // when
        val read = storage.languageByName(value)

        // then
        assertLanguageCorrect(read, value)
        assertEquals("languages match", language, read)
    }

    @Test
    fun test__languageByName__languageDoesNotExist() {
        // given
        val storage = emptyStorage.value

        // when
        val read = storage.languageByName("Something")

        // then
        assertNull(read)
    }

    @Test
    fun test__addTerm__Word() {
        // given
        val storage = prefilledStorage.value

        val value = "shrimp"
        val lang = domain.langTranslations()

        // when
        val term = storage.addTerm(value, lang, null)

        // then
        assertTermCorrect(term, value, lang)
    }

    @Test
    fun test__addTerm__Sentence() {
        // given
        val storage = prefilledStorage.value

        val value = "Shrimp is going out on Fridays."
        val lang = domain.langTranslations()

        // when
        val term = storage.addTerm(value, lang, null)

        // then
        assertTermCorrect(term, value, lang)
    }

    @Test
    fun `addTerm() with transcription`() {
        // given
        val storage = prefilledStorage.value

        val lang = domain.langTranslations()

        val value = "exaggeration"

        // when
        val term = storage.addTerm(value, lang, null)

        // then
        assertTermCorrect(term, value, lang)
    }

    @Test
    fun `update transcription - was none, new none`() {
        // given
        val storage = prefilledStorage.value

        val lang = domain.langTranslations()

        val value = "exaggeration"
        val transcriptionNew = null

        val term = storage.addTerm(value, lang, null)

        // when
        val updated = storage.updateTerm(term, Extras(transcription = null))

        // then
        assertExtrasCorrect(updated.extras, transcriptionNew)
    }

    @Test
    fun `update transcription - was none, new non-empty`() {
        // given
        val storage = prefilledStorage.value

        val lang = domain.langTranslations()

        val value = "exaggeration"
        val transcriptionNew = "[ɪɡˌzædʒəˈreɪʃən]"

        val term = storage.addTerm(value, lang, null)

        // when
        val updated = storage.updateTerm(term, Extras(transcription = transcriptionNew))

        // then
        assertExtrasCorrect(updated.extras, transcriptionNew)
    }

    @Test
    fun `updateTranscription() was non-empty, new none`() {
        // given
        val storage = prefilledStorage.value

        val lang = domain.langTranslations()

        val value = "exaggeration"
        val transcriptionOld = "[ɪɡˌzædʒəˈreɪʃən]"
        val transcriptionNew = null

        val term = storage.addTerm(value, lang, Extras(transcription = transcriptionOld))

        // when
        val updated = storage.updateTerm(term, Extras(transcription = transcriptionNew))

        // then
        assertExtrasCorrect(updated.extras, transcriptionNew)
    }

    @Test
    fun `update transcription - was non-empty, new non-empty`() {
        // given
        val storage = prefilledStorage.value

        val lang = domain.langTranslations()

        val value = "exaggeration"
        val transcriptionOld = "[mɑːtʃ]"
        val transcriptionNew = "[ɪɡˌzædʒəˈreɪʃən]"

        val term = storage.addTerm(value, lang, Extras(transcription = transcriptionOld))

        // when
        val updated = storage.updateTerm(term, Extras(transcription = transcriptionNew))

        // then
        assertExtrasCorrect(updated.extras, transcriptionNew)
    }

    @Test
    fun test__termById__Word() {
        // given
        val storage = emptyStorage.value

        val lang = storage.addLanguage("Russian")

        val value = "shrimp"
        val id = storage.addTerm(value, lang, null).id

        // when
        val term = storage.termById(id)

        // then
        assertTermCorrect(term, value, lang)
    }

    @Test
    fun test__termById__Sentence() {
        // given
        val storage = emptyStorage.value

        val lang = storage.addLanguage("Russian")

        val value = "Shrimp is going out on Fridays."

        val id = storage.addTerm(value, lang, null).id

        // when
        val term = storage.termById(id)

        // then
        assertTermCorrect(term, value, lang)
    }

    @Test
    fun `termById() - with transcription`() {
        // given
        val storage = emptyStorage.value

        val lang = storage.addLanguage("Russian")

        val value = "exaggeration"

        val id = storage.addTerm(value, lang, null).id

        // when
        val term = storage.termById(id)

        // then
        assertTermCorrect(term, value, lang)
    }

    @Test
    fun `termByValues() - does not exist - storage empty`() {
        // given
        val storage = emptyStorage.value

        val lang = storage.addLanguage("Russian")

        val value = "shrimp"

        // when
        val term = storage.termByValues(value, lang)

        // then
        assertNull(term)
    }

    @Test
    fun test__termByValues__DoesNotExist_WrongValue() {
        // given
        val storage = emptyStorage.value

        val lang = storage.addLanguage("Russian")

        storage.addTerm("aaa", lang, null)
        storage.addTerm("bbb", lang, null)

        // when
        val term = storage.termByValues("shrimp", lang)

        // then
        assertNull(term)
    }

    @Test
    fun test__termByValues__DoesNotExist_WrongLanguage() {
        // given
        val storage = emptyStorage.value

        val value = "shrimp"

        val langRussian = storage.addLanguage("Russian")
        val langFrench = storage.addLanguage("French")

        storage.addTerm(value, langRussian, null)

        // when
        val term = storage.termByValues(value, langFrench)

        // then
        assertNull(term)
    }

    @Test
    fun test__termByValues__DoesNotExist_WrongEverything() {
        // given
        val storage = emptyStorage.value

        val langRussian = storage.addLanguage("Russian")
        val langEnglish = storage.addLanguage("English")
        val langFrench = storage.addLanguage("French")

        storage.addTerm("она", langRussian, null)
        storage.addTerm("she", langEnglish, null)

        // when
        val term = storage.termByValues("elle", langFrench)

        // then
        assertNull(term)
    }

    @Test
    fun `termByValues() exists`() {
        // given
        val storage = prefilledStorage.value

        val lang = storage.addLanguage("French")

        val value = "shrimp"

        val id = storage.addTerm(value, lang, null).id

        // when
        val term = storage.termByValues(value, lang)

        // then
        assertNotNull(term)
        assertEquals(id, term!!.id)
        assertTermCorrect(term, value, lang)
    }

    @Test
    fun test__isUsed__emptyStorage() {
        // given
        val storage = prefilledStorage.value

        val term = mockTerm("креветка", domain.langTranslations())

        // when
        val used = storage.isUsed(term)

        // then
        assertFalse(used)
    }

    @Test
    fun test__isUsed__isNotPresent() {
        // given
        val storage = prefilledStorage.value

        storage.addTerm("shrimp", domain.langOriginal(), null)
        storage.addTerm("креветка", domain.langTranslations(), null)
        val term = Term(5L, "spring", domain.langOriginal(), Extras(null))

        // when
        val used = storage.isUsed(term)

        // then
        assertFalse(used)
    }

    @Test
    fun test__isUsed__isNotUsed() {
        // given
        val storage = prefilledStorage.value

        storage.addTerm("shrimp", domain.langOriginal(), null)
        val term = storage.addTerm("креветка", domain.langTranslations(), null)

        // when
        val used = storage.isUsed(term)

        // then
        assertFalse(used)
    }

    @Test
    fun test__isUsed__used() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)

        val term1 = storage.addTerm("shrimp", domain.langOriginal(), null)
        val term2 = storage.addTerm("креветка", domain.langTranslations(), null)
        val term3 = storage.addTerm("spring", domain.langOriginal(), null)

        // when
        storage.addCard(domain, deck.id, term1, listOf(term2))
        storage.addCard(domain, deck.id, term2, listOf(term3))

        // then
        assertTrue(storage.isUsed(term1))
        assertTrue(storage.isUsed(term2))
        assertTrue(storage.isUsed(term3))
    }

    @Test
    fun test__isUsed__addAndRemove() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)

        val term1 = storage.addTerm("shrimp", domain.langOriginal(), null)
        val term2 = storage.addTerm("креветка", domain.langTranslations(), null)

        // when
        val card = storage.addCard(domain, deck.id, term1, listOf(term2))
        storage.deleteCard(card)

        // then
        assertFalse(storage.isUsed(term1))
        assertFalse(storage.isUsed(term2))
    }

    @Test
    fun test__isUsed__addAndRemoveMultipleTerms() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)

        val term1 = storage.addTerm("shrimp", domain.langOriginal(), null)
        val term2 = storage.addTerm("креветка", domain.langTranslations(), null)
        val term3 = storage.addTerm("spring", domain.langOriginal(), null)

        // when
        val card = storage.addCard(domain, deck.id, term1, listOf(term2, term3))
        storage.deleteCard(card)

        // then
        assertFalse(storage.isUsed(term1))
        assertFalse(storage.isUsed(term2))
        assertFalse(storage.isUsed(term3))
    }

    @Test
    fun test__isUsed__addAndRemoveMultipleCards() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)

        val term1 = storage.addTerm("shrimp", domain.langOriginal(), null)
        val term2 = storage.addTerm("креветка", domain.langTranslations(), null)
        val term3 = storage.addTerm("spring", domain.langOriginal(), null)

        // when
        val card1 = storage.addCard(domain, deck.id, term1, listOf(term2))
        storage.addCard(domain, deck.id, term2, listOf(term3))

        storage.deleteCard(card1)

        // then
        assertFalse(storage.isUsed(term1))
        assertTrue(storage.isUsed(term2))
        assertTrue(storage.isUsed(term3))
    }

    @Test
    fun test__deleteTerm__present() {
        // given
        val storage = prefilledStorage.value

        val term1 = storage.addTerm("shrimp", domain.langOriginal(), null)
        val term2 = storage.addTerm("креветка", domain.langTranslations(), null)

        // when
        storage.deleteTerm(term1)
        val read1 = storage.termById(term1.id)

        // then
        assertNull(read1)

        // when
        storage.deleteTerm(term2)
        val read2 = storage.termById(term2.id)

        // then
        assertNull(read2)
    }

    @Test
    fun test__createDomain__prefilledLanguages() {
        // given
        val storage = emptyStorage.value
        addMockLanguages(storage)

        val name = "Some domain"

        // when
        val domain = storage.createDomain(name, langOriginal(), langTranslations())

        // then
        assertDomainCorrect(domain, name, langOriginal(), langTranslations())
    }

    @Test
    fun test__createDomain__newLanguages() {
        // given
        val storage = emptyStorage.value

        val name = "Some deck"
        val langTranslations = storage.addLanguage("Deutsch")
        val langOriginal = storage.addLanguage("French")

        // when
        val domain = storage.createDomain(name, langOriginal, langTranslations)

        // then
        assertDomainCorrect(domain, name, langOriginal, langTranslations)
    }

    @Test
    fun test__domainById__exists() {
        // given
        val storage = emptyStorage.value

        val lang1 = storage.addLanguage("French")
        val lang2 = storage.addLanguage("Russian")
        val lang3 = storage.addLanguage("German")

        storage.createDomain("Some domain 1", lang2, lang1)
        val domain2 = storage.createDomain("Domain 2", lang3, lang2)
        val domain3 = storage.createDomain(null, lang1, lang2)
        storage.createDomain("One more", lang3, lang1)

        // when
        val read1 = storage.domainById(domain2.id)

        // then
        assertEquals(domain2, read1)

        // when
        val read2 = storage.domainById(domain3.id)

        // then
        assertEquals(domain3, read2)
        assertEquals(lang1.value, domain3.name)
        assertEquals(lang1.value, read2!!.name)
    }

    @Test
    fun test__allDomains__empty() {
        // given
        val storage = emptyStorage.value

        // when
        val domains = storage.allDomains()

        // then
        assertTrue(domains.isEmpty())
    }

    @Test
    fun test__allDomains__one() {
        // given
        val storage = emptyStorage.value

        val langOriginal = storage.addLanguage("French")
        val langTranslations = storage.addLanguage("Russian")

        val created = storage.createDomain("Some domain", langOriginal, langTranslations)

        // when
        val domains = storage.allDomains()

        // then
        assertEquals(1, domains.size)
        assertEquals(created, domains[0])
    }

    @Test
    fun test__allDomains__many() {
        // given
        val storage = emptyStorage.value

        val lang1 = storage.addLanguage("French")
        val lang2 = storage.addLanguage("Russian")
        val lang3 = storage.addLanguage("German")

        val domain1 = storage.createDomain("Some domain 1", lang1, lang2)
        val domain2 = storage.createDomain("Domain 2", lang3, lang2)
        val domain3 = storage.createDomain("Some another domain", lang2, lang1)
        val domain4 = storage.createDomain("One more", lang3, lang1)

        // when
        val domains = storage.allDomains()

        // then
        assertEquals(4, domains.size)
        assertEquals(domain1, domains[0])
        assertEquals(domain2, domains[1])
        assertEquals(domain3, domains[2])
        assertEquals(domain4, domains[3])
    }

    @Test
    fun test__addCard__Word__SingleTranslation() {
        // given
        val storage = prefilledStorage.value
        val deck = addMockDeck(storage)

        val data = mockCardData("shrimp", "креветка", deck.id)

        // when
        val card = addMockCard(storage, data, domain)
        val read = storage.cardById(card.id, domain)

        // then
        assertCardCorrect(card, data, domain)
        assertCardCorrect(read, data, domain)
    }

    @Test
    fun test__addCard__Word__MultipleTranslations() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)
        val data = mockCardData("ракета", listOf("firework", "rocket", "missile"), deck.id)

        // when
        val card = addMockCard(storage, data, domain)
        val read = storage.cardById(card.id, domain)

        // then
        assertCardCorrect(card, data, domain)
        assertCardCorrect(read, data, domain)
    }

    @Test
    fun test__addCard__Sentence() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)
        val data = mockCardData("Shrimp is going out on Fridays.", "Креветка гуляет по пятницам.", deck.id)

        // when
        val card = addMockCard(storage, data, domain)
        val read = storage.cardById(card.id, domain)

        // then
        assertCardCorrect(card, data, domain)
        assertCardCorrect(read, data, domain)
    }

    @Test
    fun test__cardById__present() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)
        val data = mockCardData("Shrimp is going out on Fridays.", "Креветка гуляет по пятницам.", deck.id)

        // when
        val card = addMockCard(storage, data, domain)
        val read = storage.cardById(card.id, domain)

        // then
        assertCardCorrect(read, data, domain)
    }

    @Test
    fun test__cardById__absent() {
        // given
        val storage = prefilledStorage.value

        // when
        val read = storage.cardById(1L, domain)

        // then
        assertNull(read)
    }

    @Test
    fun test__cardByValues__present() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)

        addMockCard(storage, deck.id)

        val original = addMockTermOriginal(storage, "my original", domain = domain)
        val translations = listOf(
                addMockTermTranslation(storage, "translation 1", domain = domain),
                addMockTermTranslation(storage, "translation 2", domain = domain)
        )
        val card = storage.addCard(domain, deck.id, original, translations)

        addMockCard(storage, deck.id)
        addMockCard(storage, deck.id)

        // when
        val read = storage.cardByValues(domain, original)

        // then
        assertEquals(card, read)
    }

    @Test
    fun test__cardByValues__absent() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)

        addMockCard(storage, deck.id)

        val original = addMockTermOriginal(storage, "my original", domain = domain)

        addMockTermTranslation(storage, "translation 1", domain = domain)
        addMockTermTranslation(storage, "translation 2", domain = domain)

        addMockCard(storage, deck.id)
        addMockCard(storage, deck.id)

        // when
        val read = storage.cardByValues(domain, original)

        // then
        assertNull(read)
    }

    @Test
    fun test__cardByValues__reverseIsDuplicate() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)

        addMockCard(storage, deck.id)

        val original = addMockTermOriginal(storage, "my original", domain = domain)
        val translation = addMockTermTranslation(storage, "translation", domain = domain)
        storage.addCard(domain, deck.id, translation, listOf(original))

        addMockCard(storage, deck.id)
        addMockCard(storage, deck.id)

        // when
        val read = storage.cardByValues(domain, original)

        // then
        assertNull(read)
    }

    @Test
    fun test__updateCard__moveToAnotherDeck() {
        // given
        val storage = prefilledStorage.value

        val someDeck = storage.addDeck(domain, "some deck")
        addMockCard(storage, someDeck.id)
        addMockCard(storage, someDeck.id)
        val card = addMockCard(storage, someDeck.id)
        addMockCard(storage, someDeck.id)

        val newDeck = storage.addDeck(domain, "new deck")

        // when
        val updated = storage.updateCard(card, newDeck.id, card.original, card.translations)

        // then
        assertNotNull(updated)
        assertEquals(card.id, updated.id)
        assertEquals(newDeck.id, updated.deckId)
        assertEquals(card.original, updated.original)
        assertEquals(card.translations, updated.translations)

        // when
        val read = storage.cardById(card.id, domain)

        // then
        assertEquals(updated, read)

        // when - check that the card was added to the new deck
        val cardsOfDeck = storage.cardsOfDeck(newDeck)

        // then
        assertTrue(cardsOfDeck.contains(read))

        // when - check that the card was removed from the old deck
        val cardsOfOldDeck = storage.cardsOfDeck(someDeck)

        // then
        assertFalse(cardsOfOldDeck.contains(read))

        // when
        val cards = storage.allCards(domain)

        // then
        assertEquals(4, cards.size)
    }

    @Test
    fun test__updateCard__changeOriginal() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)

        addMockCard(storage, deck.id)
        addMockCard(storage, deck.id)
        val card = addMockCard(storage, deck.id)
        addMockCard(storage, deck.id)

        val newOriginal = addMockTermOriginal(storage, "new value", domain = domain)

        // when
        val updated = storage.updateCard(card, card.deckId, newOriginal, card.translations)

        // then
        assertNotNull(updated)
        assertEquals(card.id, updated.id)
        assertEquals(card.deckId, updated.deckId)
        assertEquals(newOriginal, updated.original)
        assertEquals(card.translations, updated.translations)

        // when
        val read = storage.cardById(card.id, domain)

        // then
        assertEquals(updated, read)

        // when
        val cards = storage.allCards(domain)

        // then
        assertEquals(4, cards.size)
    }

    @Test
    fun test__updateCard__addTranslation() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)

        addMockCard(storage, deck.id)
        addMockCard(storage, deck.id)
        val card = addMockCard(storage, deck.id)
        addMockCard(storage, deck.id)

        val newTranslation = addMockTermTranslation(storage, "new translation", domain = domain)
        val newTranslations = card.translations.plus(newTranslation)

        // when
        val updated = storage.updateCard(card, card.deckId, card.original, newTranslations)

        // then
        assertNotNull(updated)
        assertEquals(card.id, updated.id)
        assertEquals(card.deckId, updated.deckId)
        assertEquals(card.original, updated.original)
        assertEquals(newTranslations, updated.translations)

        // when
        val read = storage.cardById(card.id, domain)

        // then
        assertEquals(updated, read)

        // when
        val cards = storage.allCards(domain)

        // then
        assertEquals(4, cards.size)
    }

    @Test
    fun test__updateCard__replaceTranslation() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)

        addMockCard(storage, deck.id)
        addMockCard(storage, deck.id)
        val card = addMockCard(storage, translations = listOf("some translation", "my translation", "translation"), domain = domain)
        addMockCard(storage, deck.id)

        val toBeRemoved = card.translations.find { it.value == "my translation" }!!
        val newTranslation = addMockTermTranslation(storage, "new value", domain = domain)

        val newTranslations = card.translations.minus(toBeRemoved).plus(newTranslation)

        // when
        val updated = storage.updateCard(card, card.deckId, card.original, newTranslations)

        // then
        assertNotNull(updated)
        assertEquals(card.id, updated.id)
        assertEquals(card.deckId, updated.deckId)
        assertEquals(card.original, updated.original)
        assertEquals(newTranslations, updated.translations)

        // when
        val read = storage.cardById(card.id, domain)

        // then
        assertEquals(updated, read)

        // when
        val cards = storage.allCards(domain)

        // then
        assertEquals(4, cards.size)
    }

    @Test
    fun test__updateCard__removeTranslation() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)

        addMockCard(storage, deck.id)
        addMockCard(storage, deck.id)
        val card = addMockCard(storage, translations = listOf("some translation", "my translation", "translation"), domain = domain)
        addMockCard(storage, deck.id)

        val toBeRemoved = card.translations.find { it.value == "my translation" }!!

        val newTranslations = card.translations.minus(toBeRemoved)

        // when
        val updated = storage.updateCard(card, card.deckId, card.original, newTranslations)

        // then
        assertNotNull(updated)
        assertEquals(card.id, updated.id)
        assertEquals(card.deckId, updated.deckId)
        assertEquals(card.original, updated.original)
        assertEquals(newTranslations, updated.translations)

        // when
        val read = storage.cardById(card.id, domain)

        // then
        assertEquals(updated, read)

        // when
        val cards = storage.allCards(domain)

        // then
        assertEquals(4, cards.size)
    }

    @Test
    fun test__updateCard__updateAllInfo() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)
        
        addMockCard(storage, deck.id)
        addMockCard(storage, deck.id)
        val card = addMockCard(storage, translations = listOf("some translation", "my translation", "translation"), domain = domain)
        addMockCard(storage, deck.id)

        val newDeck = storage.addDeck(domain, "New deck")

        val newOriginal = addMockTermOriginal(storage, "new original", domain = domain)

        val toBeRemoved = card.translations.find { it.value == "my translation" }!!
        val newTranslation = addMockTermTranslation(storage, "new translation", domain = domain)
        val newTranslations = card.translations.minus(toBeRemoved).plus(newTranslation)

        // when
        val updated = storage.updateCard(card, newDeck.id, newOriginal, newTranslations)

        // then
        assertNotNull(updated)
        assertEquals(card.id, updated.id)
        assertEquals(newDeck.id, updated.deckId)
        assertEquals(newOriginal, updated.original)
        assertEquals(newTranslations, updated.translations)

        // when
        val read = storage.cardById(card.id, domain)

        // then
        assertEquals(updated, read)

        // when
        val cards = storage.allCards(domain)

        // then
        assertEquals(4, cards.size)
    }

    @Test
    fun test__deleteCard__present() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)

        val data = mockCardData(deckId = deck.id)
        val card = addMockCard(storage, data, domain)

        // when
        storage.deleteCard(card)
        val read = storage.cardById(card.id, domain)

        // then
        assertNull("card was removed", read)
    }

    @Test
    fun test__allCards__emtpy() {
        // given
        val storage = prefilledStorage.value

        // when
        val cards = storage.allCards(domain)

        // then
        assertTrue(cards.isEmpty())
    }

    @Test
    fun test__allCards__nonEmtpy() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)
        val list = (0 until 10).map { addMockCard(storage, deck.id, original = "original $it",
                translations = listOf("translation $it - 1", "transition $it - 2"), domain = domain) }

        // when
        val cards = storage.allCards(domain)

        // then
        assertEquals(list, cards)
    }

    @Test
    fun test__allCards__addDelete() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)
        val card = addMockCard(storage, deck.id)

        // when
        val cards = storage.allCards(domain)

        // then
        assertEquals(listOf(card), cards)

        // when
        storage.deleteCard(card)
        val cards2 = storage.allCards(domain)

        // then
        assertTrue(cards2.isEmpty())
    }

    @Test
    fun test__allCards__updateCard() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)
        val card = addMockCard(storage, deck.id)

        // when
        val cards = storage.allCards(domain)

        // then
        assertEquals(listOf(card), cards)

        // when
        val newOriginal = addMockTermOriginal(storage, "new original", domain = domain)
        val newTranslation = addMockTermTranslation(storage, "new translation", domain = domain)
        val newDeck = storage.addDeck(domain, "new deck")
        val updated = storage.updateCard(card, newDeck.id, newOriginal, listOf(newTranslation))

        val cards2 = storage.allCards(domain)

        // then
        assertEquals(listOf(updated), cards2)
    }

    @Test
    fun test__allCards__updateState() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)
        val card = addMockCard(storage, deck.id)
        val state = mockState(5)

        // when
        val cards = storage.allCards(domain)

        // then
        assertEquals(listOf(card), cards)

        // when
        storage.updateCardState(card, state)
        val cards2 = storage.allCards(domain)

        // then
        assertEquals(listOf(card), cards2)
    }

    @Test
    fun test__addDeck() {
        // given
        val storage = prefilledStorage.value

        val name = "My new deck"

        // when
        val deck = storage.addDeck(domain, name)

        // then
        assertDeckCorrect(deck, name, domain)
    }

    @Test
    fun test__updateDeck() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My new deck")
        val name = "Updated name"

        // when
        val updated = storage.updateDeck(deck, name)

        // then
        assertDeckCorrect(updated, name, domain)
        assertEquals(deck.id, updated.id)
        assertEquals(1, storage.allDecks(domain).size)
    }

    @Test
    fun test__deleteDeck__nonEmpty() {
        // given
        val storage = prefilledStorage.value
        val deck = storage.addDeck(domain, "Some deck")
        addMockCard(storage, deck.id)

        // when
        val result = storage.deleteDeck(deck)

        // then
        assertFalse(result)
        assertEquals(1, storage.allDecks(domain).size)
    }

    @Test
    fun test__deleteDeck__empty() {
        // given
        val storage = prefilledStorage.value
        val deck = storage.addDeck(domain, "Some deck")

        // when
        val result = storage.deleteDeck(deck)

        // then
        assertTrue(result)
        assertEquals(0, storage.allDecks(domain).size)
    }

    @Test
    fun test__deckPendingCounts__empty() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "mock deck")
        val cardType = CardType.FORWARD
        val today = today

        // when
        val counts = storage.deckPendingCounts(deck, cardType, today)

        // then
        assertEquals(0, counts.new)
        assertEquals(0, counts.review)
        assertEquals(0, counts.relearn)
        assertEquals(0, counts.total)
    }

    @Test
    fun test__deckPendingCounts__noPendingCards() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "mock deck")
        val forward = (0 until 3)
                .map { mockCardData("original $it", "translation $it", deck.id) }
                .map {
                    addMockCard(storage, it, domain, CardType.FORWARD)
                }
        val reverse = (0 until 5)
                .map { mockCardData("translation 1 $it", "original 1 $it", deck.id) }
                .map {
                    addMockCard(storage, it, domain, CardType.REVERSE)
                }
        val today = today

        storage.updateCardState(forward[0], State(SRState(today.plusDays(3), 4)))
        storage.updateCardState(forward[1], State(SRState(today.plusDays(1), 4)))
        storage.updateCardState(forward[2], State(SRState(today.plusDays(10), 4)))

        storage.updateCardState(reverse[0], State(SRState(today.plusDays(3), 4)))
        storage.updateCardState(reverse[1], State(SRState(today.plusDays(1), 4)))
        storage.updateCardState(reverse[2], State(SRState(today.plusDays(10), 4)))
        storage.updateCardState(reverse[3], State(SRState(today.plusDays(10), 4)))
        storage.updateCardState(reverse[4], State(SRState(today.plusDays(10), 4)))

        // when
        val counts = storage.deckPendingCounts(deck, CardType.FORWARD, today)

        // then
        assertEquals(0, counts.new)
        assertEquals(0, counts.review)
        assertEquals(0, counts.relearn)
        assertEquals(forward.count(), counts.total)

        // when
        val counts1 = storage.deckPendingCounts(deck, CardType.REVERSE, today)

        // then
        assertEquals(0, counts1.new)
        assertEquals(0, counts1.review)
        assertEquals(0, counts1.relearn)
        assertEquals(reverse.count(), counts1.total)
    }

    @Test
    fun test__deckPendingCounts__hasPendingCards() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "mock deck")
        val forward = (0 until 4)
                .map { mockCardData("original $it", "translation $it", deck.id) }
                .map {
                    addMockCard(storage, it, domain, CardType.FORWARD)
                }
        val reverse = (0 until 6)
                .map { mockCardData("translation 1 $it", "original 1 $it", deck.id) }
                .map {
                    addMockCard(storage, it, domain, CardType.REVERSE)
                }
        val today = today

        storage.updateCardState(forward[0], State(SRState(today.plusDays(3), 4)))
        storage.updateCardState(forward[1], State(SRState(today.minusDays(1), 4)))
        storage.updateCardState(forward[2], State(SRState(today, -1)))
        storage.updateCardState(forward[3], State(SRState(today.plusDays(1), 4)))

        storage.updateCardState(reverse[0], State(SRState(today, 0)))
        storage.updateCardState(reverse[1], State(SRState(today.plusDays(1), 4)))
        storage.updateCardState(reverse[2], State(SRState(today.minusDays(10), 4)))
        storage.updateCardState(reverse[3], State(SRState(today, 0)))
        storage.updateCardState(reverse[4], emptyState())
        storage.updateCardState(reverse[5], emptyState())

        // when
        val counts = storage.deckPendingCounts(deck, CardType.FORWARD, today)

        // then
        assertEquals(1, counts.new)
        assertEquals(1, counts.review)
        assertEquals(0, counts.relearn)
        assertEquals(forward.count(), counts.total)

        // when
        val counts1 = storage.deckPendingCounts(deck, CardType.REVERSE, today)

        // then
        assertEquals(2, counts1.new)
        assertEquals(1, counts1.review)
        assertEquals(2, counts1.relearn)
        assertEquals(reverse.count(), counts1.total)
    }

    @Test
    fun test__deckById__NoCards() {
        // given
        val storage = prefilledStorage.value

        val name = "EN - My deck"
        val id = storage.addDeck(domain, name).id
        storage.addDeck(domain, "wrong deck 1")
        storage.addDeck(domain, "wrong deck 2")

        // when
        val deck = storage.deckById(id, domain)

        // assert
        assertDeckCorrect(deck, name, domain)
    }

    @Test
    fun test__deckById__HasCards() {
        // given
        val storage = prefilledStorage.value

        val name = "EN - My deck"
        storage.addDeck(domain, "wrong deck 1")
        val id = storage.addDeck(domain, name).id
        storage.addDeck(domain, "wrong deck 2")
        val cards = listOf(
                mockCardData("shrimp", "креветка", id),
                mockCardData("ракета", listOf("rocket", "missile", "firework"), id),
                mockCardData("Shrimp is going out on Fridays.", "Креветка гуляет по пятницам.", id)
        )
        for (card in cards) {
            addMockCard(storage, card, domain)
        }

        // when
        val deck = storage.deckById(id, domain)!!

        // assert
        assertDeckCorrect(deck, name, domain)
        assertDeckCardsCorrect(storage.cardsOfDeck(deck), cards, domain)
    }

    @Test
    fun test__deckById__DoesNotExist() {
        // given
        val storage = prefilledStorage.value

        val id = 100L
        storage.addDeck(domain, "wrong deck 1")
        storage.addDeck(domain, "wrong deck 2")

        // when
        val deck = storage.deckById(id, domain)

        // assert
        assertNull("deck not found", deck)
    }

    @Test
    fun test__cardsOfDeck__NewDeck() {
        // given
        val storage = prefilledStorage.value

        // when
        val deck = storage.addDeck(domain, "Mock deck")
        val cards = storage.cardsOfDeck(deck)

        // assert
        assertEquals("new deck is empty", 0, cards.size)
    }

    @Test
    fun test__cardsOfDeck__EmptyDeck() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "Mock deck")
        val wrongDeck1 = storage.addDeck(domain, "wrong deck 1")
        for (i in 0 until 3) {
            addMockCard(storage, wrongDeck1.id)
        }
        val wrongDeck2 = storage.addDeck(domain, "wrong deck 2")
        for (i in 0 until 3) {
            addMockCard(storage, wrongDeck2.id)
        }

        // when
        val cards = storage.cardsOfDeck(deck)

        // assert
        assertEquals("empty deck has no cards", 0, cards.size)
    }

    @Test
    fun test__cardsOfDeck__NonEmptyDeck() {
        // given
        val storage = prefilledStorage.value

        val decks = mutableListOf<Deck>()
        val cardData = mutableListOf<List<CardData>>()
        for (i in 0 until 3) {
            val deck = storage.addDeck(domain, "deck $i")
            decks.add(deck)
            cardData.add(listOf(
                    mockCardData("original ${2*i}", "translation ${2*i}", deck.id),
                    mockCardData("original ${2*i+1}", "translation ${2*i+1}", deck.id)
            ))
        }
        cardData.forEach { it.forEach { addMockCard(storage, it, domain) } }

        for (i in 0 until decks.size) {
            // when
            val cards = storage.cardsOfDeck(decks[i])

            // assert
            assertDeckCardsCorrect(cards, cardData[i], domain)
        }
    }

    @Test
    fun test__cardsOfDeck__NonEmptyDeck__differentCount() {
        // given
        val storage = prefilledStorage.value

        val decks = mutableListOf<Deck>()
        val cardData = mutableListOf<List<CardData>>()
        val deck1 = storage.addDeck(domain, "deck 1")
        decks.add(deck1)
        cardData.add(listOf(
                mockCardData("original $1", "translation $1", deck1.id),
                mockCardData("original $2", "translation $2", deck1.id)
        ))
        val deck2 = storage.addDeck(domain, "deck 2")
        decks.add(deck2)
        cardData.add(listOf())
        val deck3 = storage.addDeck(domain, "deck 3")
        decks.add(deck3)
        cardData.add(listOf(
                mockCardData("original $4", "translation $4", deck3.id),
                mockCardData("original $5", "translation $5", deck3.id)
        ))
        cardData.forEach { it.forEach { addMockCard(storage, it, domain) } }

        for (i in 0 until decks.size) {
            // when
            val cards = storage.cardsOfDeck(decks[i])

            // assert
            assertDeckCardsCorrect(cards, cardData[i], domain)
        }
    }

    @Test
    fun test__allDecks__DecksAreEmpty() {
        // given
        val storage = prefilledStorage.value

        val decks = mutableListOf<Deck>()
        for (i in 0 until 3) {
            decks.add(storage.addDeck(domain, "deck $i"))
        }

        // when
        val allDecks = storage.allDecks(domain)

        // then
        assertEquals("decks number is correct", decks.size, allDecks.size)
        for (i in 0 until decks.size) {
            assertDeckCorrect(allDecks[i], decks[i].name, domain)
        }
    }

    @Test
    fun test__allDecks__DecksAreNonEmpty() {
        // given
        val storage = prefilledStorage.value

        val decks = mutableListOf<Deck>()
        val cardData = mutableListOf<List<CardData>>()
        for (i in 0 until 3) {
            val deck = storage.addDeck(domain, "deck $i")
            decks.add(deck)
            cardData.add(listOf(
                    mockCardData("original $i", "translation $i", deck.id)
            ))
        }
        cardData.forEach { it.forEach { addMockCard(storage, it, domain) } }

        // when
        val allDecks = storage.allDecks(domain)

        // then
        assertEquals("decks number is correct", decks.size, allDecks.size)
        for (i in 0 until decks.size) {
            assertDeckCorrect(allDecks[i], decks[i].name, domain)
            assertDeckCardsCorrect(storage.cardsOfDeck(decks[i]), cardData[i], domain)
        }
    }

    @Test
    fun test__allDecks__NoDecks() {
        // given
        val storage = prefilledStorage.value

        // when
        val allDecks = storage.allDecks(domain)

        // then
        assertEquals("no decks found", 0, allDecks.size)
    }

    @Test
    fun test__updateSRCardState__nonExistent() {
        // given
        val storage = prefilledStorage.value

        val card = mockCard(id = 77L)

        // when
        val state = storage.getCardState(card)

        // then
        assertEquals(emptyState(), state)
    }

    @Test
    fun test__updateSRCardState__stateNeverUpdated() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)
        val card = addMockCard(storage, deck.id)

        // when
        val state = storage.getCardState(card)

        // then
        assertEquals(emptyState(), state)
    }

    @Test
    fun test__updateSRCardState__existent() {
        // given
        val storage = prefilledStorage.value

        // when
        val deck = addMockDeck(storage)
        val card = addMockCard(storage, deck.id)

        val state = storage.getCardState(card)

        // then
        assertEquals("state is correct", Status.NEW, state.spacedRepetition.status)
        assertEquals("new card is due today", today, state.spacedRepetition.due)

        // given
        val newState = State(SRState(today.plusDays(8), 8))

        // when
        storage.updateCardState(card, newState)
        val readState = storage.getCardState(card)

        // then
        assertEquals("state is correct", newState.spacedRepetition.status, readState.spacedRepetition.status)
        assertEquals("new card is due today", newState.spacedRepetition.due, readState.spacedRepetition.due)
    }

    @Test
    fun test__cardsDueDate__newCards() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "mock deck")
        val count = 3
        val cardData = mutableListOf<CardData>()
        for (i in 0 until count) {
            val data = mockCardData("original $i", "translation $i", deck.id)
            cardData.add(data)
            addMockCard(storage, data, domain)
        }

        // when
        val due = storage.pendingCards(deck, today)

        // then
        assertEquals("all new cards are due today", count, due.size)
        for (i in 0 until count) {
            assertCardCorrect(due[i].first, cardData[i], domain)
            assertEquals("state is correct", today, due[i].second.spacedRepetition.due)
            assertEquals("state is correct", Status.NEW, due[i].second.spacedRepetition.status)
        }
    }

    @Test
    fun test__cardsDueDate__cardsInProgress() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "mock deck")
        val count = 3
        val cardsData = mutableListOf<CardData>()
        val cards = mutableListOf<Card>()
        for (i in 0 until count) {
            val data = mockCardData("original $i", "translation $i", deck.id)
            cardsData.add(data)
            val added = addMockCard(storage, data, domain)
            cards.add(added)
        }
        val today = today

        storage.updateCardState(cards[0], State(SRState(today, 4)))
        storage.updateCardState(cards[1], State(SRState(today.plusDays(1), 4)))
        storage.updateCardState(cards[2], State(SRState(today.minusDays(1), 4)))

        // when
        val due = storage.pendingCards(deck, today)

        // then
        assertEquals(2, due.size)
        assertCardCorrect(due[0].first, cardsData[0], domain)
        assertCardCorrect(due[1].first, cardsData[2], domain)
    }

    @Test
    fun test__cardsDueDate__noPendingCards() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "mock deck")
        val count = 3
        val cards = (0 until count)
                .map { mockCardData("original $it", "translation $it", deck.id) }
                .map { addMockCard(storage, it, domain) }
        val today = today

        storage.updateCardState(cards[0], State(SRState(today.plusDays(3), 4)))
        storage.updateCardState(cards[1], State(SRState(today.plusDays(1), 4)))
        storage.updateCardState(cards[2], State(SRState(today.plusDays(10), 4)))

        // when
        val due = storage.pendingCards(deck, today)

        // then
        assertEquals(0, due.size)
    }

    @Test
    fun test__getStatesForCardsWithOriginals__emptyRequest() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "mock deck")
        val count = 3
        val cards = (0 until count)
                .map { mockCardData("original $it", "translation $it", deck.id) }
                .map { addMockCard(storage, it, domain) }

        // when
        val map = storage.getStatesForCardsWithOriginals(emptyList())

        // then
        assertEquals(0, map.size)

        val state1 = State(SRState(today.plusDays(3), 4))
        val state2 = State(SRState(today.plusDays(1), 4))
        val state3 = State(SRState(today.plusDays(10), 4))

        // given
        storage.updateCardState(cards[0], state1)
        storage.updateCardState(cards[1], state2)
        storage.updateCardState(cards[2], state3)

        // when
        val map2 = storage.getStatesForCardsWithOriginals(emptyList())

        // then
        assertEquals(0, map2.size)
    }

    @Test
    fun test__getStatesForCardsWithOriginals__termsNotThere() {
        // when
        val storage = emptyStorage.value

        // when
        val map = storage.getStatesForCardsWithOriginals(listOf(1L, 5L, 10L))

        // then
        assertEquals(0, map.size)
    }

    @Test
    fun test__getStatesForCardsWithOriginals__valuesAbsent() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "mock deck")
        val count = 3
        val cards = (0 until count)
                .map { mockCardData("original $it", "translation $it", deck.id) }
                .map { addMockCard(storage, it, domain) }

        // when
        val map = storage.getStatesForCardsWithOriginals(cards.map { it.original.id })

        // then
        assertEquals(3, map.size)
        map.values.forEach { assertEquals(mockEmptyState(today), it) }
    }

    @Test
    fun test__getStatesForCardsWithOriginals__valuesPresent() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "mock deck")
        val count = 3
        val cards = (0 until count)
                .map { mockCardData("original $it", "translation $it", deck.id) }
                .map { addMockCard(storage, it, domain) }
        val today = today

        val state1 = State(SRState(today.plusDays(3), 4))
        val state2 = State(SRState(today.plusDays(1), 4))
        val state3 = State(SRState(today.plusDays(10), 4))

        storage.updateCardState(cards[0], state1)
        storage.updateCardState(cards[1], state2)
        storage.updateCardState(cards[2], state3)

        // when
        val map = storage.getStatesForCardsWithOriginals(cards.map { it.original.id })

        // then
        assertEquals(3, map.size)
        assertEquals(state1, map[cards[0].original.id])
        assertEquals(state2, map[cards[1].original.id])
        assertEquals(state3, map[cards[2].original.id])
    }
}