package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.StateType
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.exercise.MockEmptyStateProvider
import com.ashalmawia.coriolan.learning.exercise.MockExercise
import com.ashalmawia.coriolan.learning.exercise.mockEmptySRState
import com.ashalmawia.coriolan.learning.exercise.sr.SRState
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.model.*
import org.junit.Assert.*
import org.junit.Test

abstract class StorageTest {

    private val exercise = MockExercise(stateType = StateType.SR_STATE)
    private val exercises = listOf(exercise)

    private val today = mockToday()
    private fun emptyState() = mockEmptySRState(today)

    protected val mockEmptyStateProvider = MockEmptyStateProvider(today)

    private lateinit var domain: Domain

    private val prefilledStorage: Lazy<Repository> = lazy {
        val it = createStorage(exercises)
        addMockLanguages(it)
        domain = it.createDomain("Default", langOriginal(), langTranslations())
        it
    }
    private val emptyStorage: Lazy<Repository> = lazy { createStorage(exercises) }

    protected abstract fun createStorage(exercises: List<Exercise<*, *>>): Repository

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
    fun test__addExpression__Word() {
        // given
        val storage = prefilledStorage.value

        val value = "shrimp"
        val lang = domain.langTranslations()

        // when
        val expression = storage.addExpression(value, lang)

        // then
        assertExpressionCorrect(expression, value, lang)
    }

    @Test
    fun test__addExpression__Sentence() {
        // given
        val storage = prefilledStorage.value

        val value = "Shrimp is going out on Fridays."
        val lang = domain.langTranslations()

        // when
        val expression = storage.addExpression(value, lang)

        // then
        assertExpressionCorrect(expression, value, lang)
    }

    @Test
    fun `addExpression() with transcription`() {
        // given
        val storage = prefilledStorage.value

        val lang = domain.langTranslations()

        val value = "exaggeration"

        // when
        val expression = storage.addExpression(value, lang)

        // then
        assertExpressionCorrect(expression, value, lang)
    }

    @Test
    fun `updateTranscription() was none, new none`() {
        // given
        val storage = prefilledStorage.value

        val lang = domain.langTranslations()

        val value = "exaggeration"
        val transcriptionNew = null

        val expression = storage.addExpression(value, lang)

        // when
        storage.setTranscription(expression, transcriptionNew)
        val extras = storage.allExtrasForExpression(expression)

        // then
        assertExtrasCorrect(extras, transcriptionNew)
    }

    @Test
    fun `updateTranscription() was none, new non-empty`() {
        // given
        val storage = prefilledStorage.value

        val lang = domain.langTranslations()

        val value = "exaggeration"
        val transcriptionOld = null
        val transcriptionNew = "[ɪɡˌzædʒəˈreɪʃən]"

        val expression = storage.addExpression(value, lang)
        storage.setTranscription(expression, transcriptionOld)

        // when
        storage.setTranscription(expression, transcriptionNew)
        val extras = storage.allExtrasForExpression(expression)

        // then
        assertExtrasCorrect(extras, transcriptionNew)
    }

    @Test
    fun `updateTranscription() was non-empty, new none`() {
        // given
        val storage = prefilledStorage.value

        val lang = domain.langTranslations()

        val value = "exaggeration"
        val transcriptionOld = "[ɪɡˌzædʒəˈreɪʃən]"
        val transcriptionNew = null

        val expression = storage.addExpression(value, lang)
        storage.setTranscription(expression, transcriptionOld)

        // when
        storage.setTranscription(expression, transcriptionNew)
        val extras = storage.allExtrasForExpression(expression)

        // then
        assertExtrasCorrect(extras, transcriptionNew)
    }

    @Test
    fun `updateTranscription() was non-empty, new non-empty`() {
        // given
        val storage = prefilledStorage.value

        val lang = domain.langTranslations()

        val value = "exaggeration"
        val transcriptionOld = "[mɑːtʃ]"
        val transcriptionNew = "[ɪɡˌzædʒəˈreɪʃən]"

        val expression = storage.addExpression(value, lang)
        storage.setTranscription(expression, transcriptionOld)

        // when
        storage.setTranscription(expression, transcriptionNew)
        val extras = storage.allExtrasForExpression(expression)

        // then
        assertExtrasCorrect(extras, transcriptionNew)
    }

    @Test
    fun `setExtra() multiple extra types`() {
        // given
        val storage = prefilledStorage.value

        val lang = domain.langTranslations()

        val value = "exaggeration"
        val transcription = "[mɑːtʃ]"
        val unknown = "blah blah"

        val expression = storage.addExpression(value, lang)

        // when
        storage.setTranscription(expression, transcription)
        storage.setExtra(expression, ExtraType.UNKNOWN, unknown)

        val extras = storage.allExtrasForExpression(expression)

        // then
        extras.also {
            assertEquals(2, it.map.size)
            assertEquals(transcription, it.transcription)
            assertEquals(unknown, it.map[ExtraType.UNKNOWN]?.value)
        }

        // when
        storage.setTranscription(expression, null)

        val withoutTranscription = storage.allExtrasForExpression(expression)

        // then
        withoutTranscription.also {
            assertEquals(1, it.map.size)
            assertEquals(null, it.transcription)
            assertEquals(unknown, it.map[ExtraType.UNKNOWN]?.value)
        }

        // when
        storage.setExtra(expression, ExtraType.UNKNOWN, null)

        val empty = storage.allExtrasForExpression(expression)

        // then
        assertTrue(empty.map.isEmpty())

        // when
        val newUnknown = "lalflalfl"
        storage.setExtra(expression, ExtraType.UNKNOWN, newUnknown)
        storage.setTranscription(expression, null)

        val withUnknown = storage.allExtrasForExpression(expression)

        // then
        withUnknown.also {
            assertEquals(1, withUnknown.map.size)
            assertNull(withUnknown.transcription)
            assertEquals(newUnknown, withUnknown.map[ExtraType.UNKNOWN]?.value)
        }
    }

    @Test
    fun `allExtrasForCard()`() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)

        val expression1 = storage.addExpression("ходить", domain.langTranslations())
        val expression2 = storage.addExpression("go", domain.langOriginal())
        val expression3 = storage.addExpression("walk", domain.langOriginal())

        val transcription2 = "[gəu]"
        val transcription3 = "[wɔːk]"

        storage.setTranscription(expression2, transcription2)
        storage.setTranscription(expression3, transcription3)

        // when
        val card1 = storage.addCard(domain, deck.id, expression2, listOf(expression1))
        val card2 = storage.addCard(domain, deck.id, expression3, listOf(expression1))
        val card3 = storage.addCard(domain, deck.id, expression1, listOf(expression2, expression3))

        // when
        val extras1 = storage.allExtrasForCard(card1)

        // then
        extras1.also {
            assertEquals(1, it.size)
            assertExtrasCorrect(it[0], transcription2)
            assertEquals(it[0], storage.allExtrasForExpression(expression2))
        }

        // when
        val extras2 = storage.allExtrasForCard(card2)

        // then
        extras2.also {
            assertEquals(1, it.size)
            assertExtrasCorrect(it[0], transcription3)
            assertEquals(it[0], storage.allExtrasForExpression(expression3))
        }

        // when
        val extras3 = storage.allExtrasForCard(card3)

        // then
        extras3.also {
            assertEquals(2, it.size)
            assertExtrasCorrect(it[0], transcription2)
            assertEquals(it[0], storage.allExtrasForExpression(expression2))
            assertExtrasCorrect(it[1], transcription3)
            assertEquals(it[1], storage.allExtrasForExpression(expression3))
        }
    }

    @Test
    fun test__expressionById__Word() {
        // given
        val storage = emptyStorage.value

        val lang = storage.addLanguage("Russian")

        val value = "shrimp"
        val id = storage.addExpression(value, lang).id

        // when
        val expression = storage.expressionById(id)

        // then
        assertExpressionCorrect(expression, value, lang)
    }

    @Test
    fun test__expressionById__Sentence() {
        // given
        val storage = emptyStorage.value

        val lang = storage.addLanguage("Russian")

        val value = "Shrimp is going out on Fridays."

        val id = storage.addExpression(value, lang).id

        // when
        val expression = storage.expressionById(id)

        // then
        assertExpressionCorrect(expression, value, lang)
    }

    @Test
    fun `expressionById() - with transcription`() {
        // given
        val storage = emptyStorage.value

        val lang = storage.addLanguage("Russian")

        val value = "exaggeration"

        val id = storage.addExpression(value, lang).id

        // when
        val expression = storage.expressionById(id)

        // then
        assertExpressionCorrect(expression, value, lang)
    }

    @Test
    fun `expressionByValues() - does not exist - storage empty`() {
        // given
        val storage = emptyStorage.value

        val lang = storage.addLanguage("Russian")

        val value = "shrimp"

        // when
        val expression = storage.expressionByValues(value, lang)

        // then
        assertNull(expression)
    }

    @Test
    fun test__expressionByValues__DoesNotExist_WrongValue() {
        // given
        val storage = emptyStorage.value

        val lang = storage.addLanguage("Russian")

        storage.addExpression("aaa", lang)
        storage.addExpression("bbb", lang)

        // when
        val expression = storage.expressionByValues("shrimp", lang)

        // then
        assertNull(expression)
    }

    @Test
    fun test__expressionByValues__DoesNotExist_WrongLanguage() {
        // given
        val storage = emptyStorage.value

        val value = "shrimp"

        val langRussian = storage.addLanguage("Russian")
        val langFrench = storage.addLanguage("French")

        storage.addExpression(value, langRussian)

        // when
        val expression = storage.expressionByValues(value, langFrench)

        // then
        assertNull(expression)
    }

    @Test
    fun test__expressionByValues__DoesNotExist_WrongEverything() {
        // given
        val storage = emptyStorage.value

        val langRussian = storage.addLanguage("Russian")
        val langEnglish = storage.addLanguage("English")
        val langFrench = storage.addLanguage("French")

        storage.addExpression("она", langRussian)
        storage.addExpression("she", langEnglish)

        // when
        val expression = storage.expressionByValues("elle", langFrench)

        // then
        assertNull(expression)
    }

    @Test
    fun `expressionByValues() exists`() {
        // given
        val storage = prefilledStorage.value

        val lang = storage.addLanguage("French")

        val value = "shrimp"

        val id = storage.addExpression(value, lang).id

        // when
        val expression = storage.expressionByValues(value, lang)

        // then
        assertNotNull(expression)
        assertEquals(id, expression!!.id)
        assertExpressionCorrect(expression, value, lang)
    }

    @Test
    fun test__isUsed__emptyStorage() {
        // given
        val storage = prefilledStorage.value

        val expression = mockExpression("креветка", domain.langTranslations())

        // when
        val used = storage.isUsed(expression)

        // then
        assertFalse(used)
    }

    @Test
    fun test__isUsed__isNotPresent() {
        // given
        val storage = prefilledStorage.value

        storage.addExpression("shrimp", domain.langOriginal())
        storage.addExpression("креветка", domain.langTranslations())
        val expression = Expression(5L, "spring", domain.langOriginal())

        // when
        val used = storage.isUsed(expression)

        // then
        assertFalse(used)
    }

    @Test
    fun test__isUsed__isNotUsed() {
        // given
        val storage = prefilledStorage.value

        storage.addExpression("shrimp", domain.langOriginal())
        val expression = storage.addExpression("креветка", domain.langTranslations())

        // when
        val used = storage.isUsed(expression)

        // then
        assertFalse(used)
    }

    @Test
    fun test__isUsed__used() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)

        val expression1 = storage.addExpression("shrimp", domain.langOriginal())
        val expression2 = storage.addExpression("креветка", domain.langTranslations())
        val expression3 = storage.addExpression("spring", domain.langOriginal())

        // when
        storage.addCard(domain, deck.id, expression1, listOf(expression2))
        storage.addCard(domain, deck.id, expression2, listOf(expression3))

        // then
        assertTrue(storage.isUsed(expression1))
        assertTrue(storage.isUsed(expression2))
        assertTrue(storage.isUsed(expression3))
    }

    @Test
    fun test__isUsed__addAndRemove() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)

        val expression1 = storage.addExpression("shrimp", domain.langOriginal())
        val expression2 = storage.addExpression("креветка", domain.langTranslations())

        // when
        val card = storage.addCard(domain, deck.id, expression1, listOf(expression2))
        storage.deleteCard(card)

        // then
        assertFalse(storage.isUsed(expression1))
        assertFalse(storage.isUsed(expression2))
    }

    @Test
    fun test__isUsed__addAndRemoveMultipleExpressions() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)

        val expression1 = storage.addExpression("shrimp", domain.langOriginal())
        val expression2 = storage.addExpression("креветка", domain.langTranslations())
        val expression3 = storage.addExpression("spring", domain.langOriginal())

        // when
        val card = storage.addCard(domain, deck.id, expression1, listOf(expression2, expression3))
        storage.deleteCard(card)

        // then
        assertFalse(storage.isUsed(expression1))
        assertFalse(storage.isUsed(expression2))
        assertFalse(storage.isUsed(expression3))
    }

    @Test
    fun test__isUsed__addAndRemoveMultipleCards() {
        // given
        val storage = prefilledStorage.value

        val deck = addMockDeck(storage)

        val expression1 = storage.addExpression("shrimp", domain.langOriginal())
        val expression2 = storage.addExpression("креветка", domain.langTranslations())
        val expression3 = storage.addExpression("spring", domain.langOriginal())

        // when
        val card1 = storage.addCard(domain, deck.id, expression1, listOf(expression2))
        storage.addCard(domain, deck.id, expression2, listOf(expression3))

        storage.deleteCard(card1)

        // then
        assertFalse(storage.isUsed(expression1))
        assertTrue(storage.isUsed(expression2))
        assertTrue(storage.isUsed(expression3))
    }

    @Test
    fun test__deleteExpression__present() {
        // given
        val storage = prefilledStorage.value

        val expression1 = storage.addExpression("shrimp", domain.langOriginal())
        val expression2 = storage.addExpression("креветка", domain.langTranslations())

        // when
        storage.deleteExpression(expression1)
        val read1 = storage.expressionById(expression1.id)

        // then
        assertNull(read1)

        // when
        storage.deleteExpression(expression2)
        val read2 = storage.expressionById(expression2.id)

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

        val original = addMockExpressionOriginal(storage, "my original", domain = domain)
        val translations = listOf(
                addMockExpressionTranslation(storage, "translation 1", domain = domain),
                addMockExpressionTranslation(storage, "translation 2", domain = domain)
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

        val original = addMockExpressionOriginal(storage, "my original", domain = domain)

        addMockExpressionTranslation(storage, "translation 1", domain = domain)
        addMockExpressionTranslation(storage, "translation 2", domain = domain)

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

        val original = addMockExpressionOriginal(storage, "my original", domain = domain)
        val translation = addMockExpressionTranslation(storage, "translation", domain = domain)
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
        assertEquals(card.id, updated!!.id)
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

        val newOriginal = addMockExpressionOriginal(storage, "new value", domain = domain)

        // when
        val updated = storage.updateCard(card, card.deckId, newOriginal, card.translations)

        // then
        assertNotNull(updated)
        assertEquals(card.id, updated!!.id)
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

        val newTranslation = addMockExpressionTranslation(storage, "new translation", domain = domain)
        val newTranslations = card.translations.plus(newTranslation)

        // when
        val updated = storage.updateCard(card, card.deckId, card.original, newTranslations)

        // then
        assertNotNull(updated)
        assertEquals(card.id, updated!!.id)
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
        val newTranslation = addMockExpressionTranslation(storage, "new value", domain = domain)

        val newTranslations = card.translations.minus(toBeRemoved).plus(newTranslation)

        // when
        val updated = storage.updateCard(card, card.deckId, card.original, newTranslations)

        // then
        assertNotNull(updated)
        assertEquals(card.id, updated!!.id)
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
        assertEquals(card.id, updated!!.id)
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

        val newOriginal = addMockExpressionOriginal(storage, "new original", domain = domain)

        val toBeRemoved = card.translations.find { it.value == "my translation" }!!
        val newTranslation = addMockExpressionTranslation(storage, "new translation", domain = domain)
        val newTranslations = card.translations.minus(toBeRemoved).plus(newTranslation)

        // when
        val updated = storage.updateCard(card, newDeck.id, newOriginal, newTranslations)

        // then
        assertNotNull(updated)
        assertEquals(card.id, updated!!.id)
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
        val newOriginal = addMockExpressionOriginal(storage, "new original", domain = domain)
        val newTranslation = addMockExpressionTranslation(storage, "new translation", domain = domain)
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
        storage.updateSRCardState(card, state, exercise.stableId)
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
        assertEquals(deck.id, updated!!.id)
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
        val today = today

        // when
        val counts = storage.deckPendingCounts(exercise.stableId, deck, today)

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

        storage.updateSRCardState(forward[0], SRState(today.plusDays(3), 4), exercise.stableId)
        storage.updateSRCardState(forward[1], SRState(today.plusDays(1), 4), exercise.stableId)
        storage.updateSRCardState(forward[2], SRState(today.plusDays(10), 4), exercise.stableId)

        storage.updateSRCardState(reverse[0], SRState(today.plusDays(3), 4), exercise.stableId)
        storage.updateSRCardState(reverse[1], SRState(today.plusDays(1), 4), exercise.stableId)
        storage.updateSRCardState(reverse[2], SRState(today.plusDays(10), 4), exercise.stableId)
        storage.updateSRCardState(reverse[3], SRState(today.plusDays(10), 4), exercise.stableId)
        storage.updateSRCardState(reverse[4], SRState(today.plusDays(10), 4), exercise.stableId)

        // when
        val counts = storage.deckPendingCounts(exercise.stableId, deck.copy(type = CardType.FORWARD), today)

        // then
        assertEquals(0, counts.new)
        assertEquals(0, counts.review)
        assertEquals(0, counts.relearn)
        assertEquals(forward.count(), counts.total)

        // when
        val counts1 = storage.deckPendingCounts(exercise.stableId, deck.copy(type = CardType.REVERSE), today)

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

        storage.updateSRCardState(forward[0], SRState(today.plusDays(3), 4), exercise.stableId)
        storage.updateSRCardState(forward[1], SRState(today.minusDays(1), 4), exercise.stableId)
        storage.updateSRCardState(forward[2], SRState(today, -1), exercise.stableId)
        storage.updateSRCardState(forward[3], SRState(today.plusDays(1), 4), exercise.stableId)

        storage.updateSRCardState(reverse[0], SRState(today, 0), exercise.stableId)
        storage.updateSRCardState(reverse[1], SRState(today.plusDays(1), 4), exercise.stableId)
        storage.updateSRCardState(reverse[2], SRState(today.minusDays(10), 4), exercise.stableId)
        storage.updateSRCardState(reverse[3], SRState(today, 0), exercise.stableId)
        storage.updateSRCardState(reverse[4], emptyState(), exercise.stableId)
        storage.updateSRCardState(reverse[5], emptyState(), exercise.stableId)

        // when
        val counts = storage.deckPendingCounts(exercise.stableId, deck.copy(type = CardType.FORWARD), today)

        // then
        assertEquals(1, counts.new)
        assertEquals(1, counts.review)
        assertEquals(0, counts.relearn)
        assertEquals(forward.count(), counts.total)

        // when
        val counts1 = storage.deckPendingCounts(exercise.stableId, deck.copy(type = CardType.REVERSE), today)

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
        val state = storage.getSRCardState(card, exercise.stableId)

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
        val state = storage.getSRCardState(card, exercise.stableId)

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

        val state = storage.getSRCardState(card, exercise.stableId)

        // then
        assertEquals("state is correct", Status.NEW, state.status)
        assertEquals("new card is due today", today, state.due)

        // given
        val newState = SRState(today.plusDays(8), 8)

        // when
        storage.updateSRCardState(card, newState, exercise.stableId)
        val readState = storage.getSRCardState(card, exercise.stableId)

        // then
        assertEquals("state is correct", newState.status, readState.status)
        assertEquals("new card is due today", newState.due, readState.due)
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
        val due = storage.cardsDueDate(exercise.stableId, deck, today)

        // then
        assertEquals("all new cards are due today", count, due.size)
        for (i in 0 until count) {
            assertCardCorrect(due[i].card, cardData[i], domain)
            assertEquals("state is correct", today, due[i].state.due)
            assertEquals("state is correct", Status.NEW, due[i].state.status)
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

        storage.updateSRCardState(cards[0], SRState(today, 4), exercise.stableId)
        storage.updateSRCardState(cards[1], SRState(today.plusDays(1), 4), exercise.stableId)
        storage.updateSRCardState(cards[2], SRState(today.minusDays(1), 4), exercise.stableId)

        // when
        val due = storage.cardsDueDate(exercise.stableId, deck, today)

        // then
        assertEquals(2, due.size)
        assertCardCorrect(due[0].card, cardsData[0], domain)
        assertCardCorrect(due[1].card, cardsData[2], domain)
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

        storage.updateSRCardState(cards[0], SRState(today.plusDays(3), 4), exercise.stableId)
        storage.updateSRCardState(cards[1], SRState(today.plusDays(1), 4), exercise.stableId)
        storage.updateSRCardState(cards[2], SRState(today.plusDays(10), 4), exercise.stableId)

        // when
        val due = storage.cardsDueDate(exercise.stableId, deck, today)

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
        val map = storage.getStatesForCardsWithOriginals(emptyList(), exercise.stableId)

        // then
        assertEquals(0, map.size)

        val state1 = SRState(today.plusDays(3), 4)
        val state2 = SRState(today.plusDays(1), 4)
        val state3 = SRState(today.plusDays(10), 4)

        // given
        storage.updateSRCardState(cards[0], state1, exercise.stableId)
        storage.updateSRCardState(cards[1], state2, exercise.stableId)
        storage.updateSRCardState(cards[2], state3, exercise.stableId)

        // when
        val map2 = storage.getStatesForCardsWithOriginals(emptyList(), exercise.stableId)

        // then
        assertEquals(0, map2.size)
    }

    @Test
    fun test__getStatesForCardsWithOriginals__expressionsNotThere() {
        // when
        val storage = emptyStorage.value

        // when
        val map = storage.getStatesForCardsWithOriginals(listOf(1L, 5L, 10L), exercise.stableId)

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
        val map = storage.getStatesForCardsWithOriginals(cards.map { it.original.id }, exercise.stableId)

        // then
        assertEquals(3, map.size)
        map.values.forEach { assertEquals(mockEmptySRState(today), it) }
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

        val state1 = SRState(today.plusDays(3), 4)
        val state2 = SRState(today.plusDays(1), 4)
        val state3 = SRState(today.plusDays(10), 4)

        storage.updateSRCardState(cards[0], state1, exercise.stableId)
        storage.updateSRCardState(cards[1], state2, exercise.stableId)
        storage.updateSRCardState(cards[2], state3, exercise.stableId)

        // when
        val map = storage.getStatesForCardsWithOriginals(cards.map { it.original.id }, exercise.stableId)

        // then
        assertEquals(3, map.size)
        assertEquals(state1, map[cards[0].original.id])
        assertEquals(state2, map[cards[1].original.id])
        assertEquals(state3, map[cards[2].original.id])
    }
}