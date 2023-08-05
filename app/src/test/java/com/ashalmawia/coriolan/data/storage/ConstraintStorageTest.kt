package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.storage.sqlite.SqliteRepositoryOpenHelper
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteStorage
import com.ashalmawia.coriolan.model.mockLearningProgress
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.model.*
import com.ashalmawia.coriolan.util.asDeckId
import com.ashalmawia.coriolan.util.asDomainId
import com.ashalmawia.coriolan.util.asLanguageId
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.SQLiteMode

@RunWith(RobolectricTestRunner::class)
@SQLiteMode(SQLiteMode.Mode.LEGACY)
class ConstraintStorageTest {

    private val today = mockToday()

    private lateinit var domain: Domain

    private val prefilledStorage: Lazy<Repository> = lazy {
        val it = createStorage()
        addMockLanguages(it)
        domain = it.createDomain("Default", langOriginal(), langTranslations())
        it
    }
    private val emptyStorage: Lazy<Repository> = lazy { createStorage() }

    private fun createStorage(): Repository {
        val helper = SqliteRepositoryOpenHelper(RuntimeEnvironment.application)
        return SqliteStorage(helper)
    }

    @Test(expected = DataProcessingException::class)
    fun test__addLanguage__nameUnique() {
        // given
        val storage = emptyStorage.value
        val value = "Russian"

        // when
        storage.addLanguage(value)
        storage.addLanguage(value)
    }

    @Test(expected = DataProcessingException::class)
    fun test__updateLanguage__notPresent() {
        // given
        val storage = emptyStorage.value
        val value = "Russian"

        // when
        storage.updateLanguage(Language(1L.asLanguageId(), ""), value)
    }

    @Test(expected = DataProcessingException::class)
    fun test__updateLanguage__nameUnique() {
        // given
        val storage = emptyStorage.value
        val sharedName = "Russian"
        storage.addLanguage(sharedName)
        val language = storage.addLanguage("English")

        // when
        storage.updateLanguage(language, sharedName)
    }

    @Test(expected = DataProcessingException::class)
    fun test__addTerm__valueUnique() {
        // given
        val storage = prefilledStorage.value

        val value = "shrimp"
        val lang = domain.langOriginal()

        // when
        storage.justAddTerm(value, lang)
        storage.justAddTerm(value, lang)
    }

    @Test
    fun test__addTerm__sameValueDifferentLangs() {
        // given
        val storage = prefilledStorage.value

        val value = "shrimp"

        // when
        storage.justAddTerm(value, domain.langOriginal())
        storage.justAddTerm(value, domain.langTranslations())
    }

    @Test(expected = DataProcessingException::class)
    fun test__addTerm__languageIncorrect() {
        // given
        val storage = emptyStorage.value

        val value = "shrimp"
        val lang = mockLanguage(value = "Russian")

        // when
        storage.justAddTerm(value, lang)
    }

    @Test(expected = DataProcessingException::class)
    fun test__deleteTerm__termIncorrect__emptyStorage() {
        // given
        val storage = prefilledStorage.value
        val term = mockTerm("креветка", domain.langTranslations())

        // when
        storage.deleteTerm(term)
    }

    @Test(expected = DataProcessingException::class)
    fun test__deleteTerm__termIncorrect__notPresent() {
        // given
        val storage = prefilledStorage.value

        storage.justAddTerm("shrimp", domain.langOriginal())
        storage.justAddTerm("креветка", domain.langTranslations())

        val term = mockTerm("spring", domain.langOriginal())

        // when
        storage.deleteTerm(term)
    }

    @Test(expected = DataProcessingException::class)
    fun test__createDomain__langOriginalIncorrect() {
        // given
        val storage = prefilledStorage.value
        val name = "My domain"

        // when
        storage.createDomain(name, mockLanguage(5L, "Bulgarian"), domain.langTranslations())
    }

    @Test(expected = DataProcessingException::class)
    fun test__createDomain__langTranslationsIncorrect() {
        // given
        val storage = prefilledStorage.value
        val name = "My domain"

        // when
        storage.createDomain(name, domain.langOriginal(), mockLanguage(7L, "Romanian"))
    }

    @Test(expected = DataProcessingException::class)
    fun test__createDomain__alreadyExists() {
        // given
        val storage = prefilledStorage.value
        val name = "My domain"

        // when
        storage.createDomain(name, domain.langOriginal(), domain.langTranslations())
    }

    @Test(expected = DataProcessingException::class)
    fun test__deleteDomain__notPresent() {
        // given
        val storage = emptyStorage.value

        // when
        storage.deleteDomain(mockDomain())
    }

    @Test(expected = DataProcessingException::class)
    fun test__addCard__domainIncorrect() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val original = storage.justAddTerm("shrimp", domain.langOriginal())
        val translation = storage.justAddTerm("креветка", domain.langTranslations())

        val dummyDomain = Domain(5L.asDomainId(), "some name", domain.langOriginal(), domain.langTranslations())

        // when
        storage.addCard(dummyDomain, deck.id, original, listOf(translation))
    }

    @Test(expected = DataProcessingException::class)
    fun test__addCard__deckIncorrect() {
        // given
        val storage = prefilledStorage.value

        val original = storage.justAddTerm("shrimp", domain.langOriginal())
        val translation = storage.justAddTerm("креветка", domain.langTranslations())

        val dummyDeckId = 5L.asDeckId()

        // when
        storage.addCard(domain, dummyDeckId, original, listOf(translation))
    }

    @Test(expected = DataProcessingException::class)
    fun test__addCard__originalIncorrect() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val translation = storage.justAddTerm("креветка", domain.langTranslations())

        val dummyOriginal = mockTerm("shrimp", domain.langOriginal())

        // when
        storage.addCard(domain, deck.id, dummyOriginal, listOf(translation))
    }

    @Test(expected = DataProcessingException::class)
    fun test__addCard__dummyTranslation() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val original = storage.justAddTerm("shrimp", domain.langOriginal())

        val dummyTranslation = mockTerm("креветка", domain.langTranslations())

        // when
        storage.addCard(domain, deck.id, original, listOf(dummyTranslation))
    }

    @Test(expected = DataProcessingException::class)
    fun test__addCard__dummyTranslation2() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val original = storage.justAddTerm("shrimp", domain.langOriginal())
        val translation = storage.justAddTerm("креветка", domain.langTranslations())

        val dummyTranslation = mockTerm("dummy", domain.langTranslations())

        // when
        storage.addCard(domain, deck.id, original, listOf(translation, dummyTranslation))
    }

    @Test(expected = DataProcessingException::class)
    fun test__addCard__noTranslation() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val original = storage.justAddTerm("shrimp", domain.langOriginal())

        // when
        storage.addCard(domain, deck.id, original, listOf())
    }

    @Test(expected = DataProcessingException::class)
    fun test__updateCard__cardAbsent() {
        // given
        val storage = prefilledStorage.value
        val card = mockCard()

        // when
        storage.updateCard(card, 2L.asDeckId(), mockTerm(), listOf(mockTerm()))
    }

    @Test(expected = DataProcessingException::class)
    fun test__updateCard__deckIncorrect() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val original = storage.justAddTerm("shrimp", domain.langOriginal())
        val translation = storage.justAddTerm("креветка", domain.langTranslations())

        val card = storage.addCard(domain, deck.id, original, listOf(translation))

        val dummyDeckId = 5L.asDeckId()

        // when
        storage.updateCard(card, dummyDeckId, original, listOf(translation))
    }

    @Test(expected = DataProcessingException::class)
    fun test__updateCard__originalIncorrect() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val original = storage.justAddTerm("shrimp", domain.langOriginal())
        val translation = storage.justAddTerm("креветка", domain.langTranslations())

        val card = storage.addCard(domain, deck.id, original, listOf(translation))

        val dummyOriginal = mockTerm("shrimp", domain.langOriginal())

        // when
        storage.updateCard(card, deck.id, dummyOriginal, listOf(translation))
    }

    @Test(expected = DataProcessingException::class)
    fun test__updateCard__dummyTranslation() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val original = storage.justAddTerm("shrimp", domain.langOriginal())
        val translation = storage.justAddTerm("креветка", domain.langTranslations())

        val card = storage.addCard(domain, deck.id, original, listOf(translation))

        val dummyTranslation = mockTerm("креветка", domain.langTranslations())

        // when
        storage.updateCard(card, deck.id, original, listOf(dummyTranslation))
    }

    @Test(expected = DataProcessingException::class)
    fun test__updateCard__dummyTranslation2() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val original = storage.justAddTerm("shrimp", domain.langOriginal())
        val translation = storage.justAddTerm("креветка", domain.langTranslations())

        val card = storage.addCard(domain, deck.id, original, listOf(translation))

        val dummyTranslation = mockTerm("dummy", domain.langTranslations())

        // when
        storage.updateCard(card, deck.id, original, listOf(translation, dummyTranslation))
    }

    @Test(expected = DataProcessingException::class)
    fun test__updateCard__noTranslation() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val original = storage.justAddTerm("shrimp", domain.langOriginal())
        val translation = storage.justAddTerm("креветка", domain.langTranslations())

        val card = storage.addCard(domain, deck.id, original, listOf(translation))

        // when
        storage.updateCard(card, deck.id, original, listOf())
    }

    @Test(expected = DataProcessingException::class)
    fun test__deleteCard__absent() {
        // given
        val storage = prefilledStorage.value

        val notAddedCard = mockCard()
        val searched = storage.cardById(notAddedCard.id, domain)
        Assert.assertNull("card is not in the DB", searched)

        // when
        storage.deleteCard(notAddedCard)
    }

    @Test(expected = DataProcessingException::class)
    fun test__addDeck__domainIncorrect() {
        // given
        val storage = emptyStorage.value
        val name = "My new deck"

        val dummyDomain = Domain(5L.asDomainId(), "some name", mockLanguage(), mockLanguage())

        // when
        storage.addDeck(dummyDomain, name)
    }

    @Test(expected = DataProcessingException::class)
    fun test__addDeck__nameUnique() {
        // given
        val storage = prefilledStorage.value

        val name = "My new deck"

        // when
        storage.addDeck(domain, name)
        storage.addDeck(domain, name)
    }

    @Test
    fun test__addDeck__sameNameDifferentDomains() {
        // given
        val storage = prefilledStorage.value
        val lang = storage.addLanguage("French")
        val domain2 = storage.createDomain("Another domain", lang, domain.langTranslations())

        val name = "My new deck"

        // when
        val deck1 = storage.addDeck(domain, name)
        val deck2 = storage.addDeck(domain2, name)

        // assert
        assertDeckCorrect(deck1, name, domain)
        assertDeckCorrect(deck2, name, domain2)
    }

    @Test(expected = DataProcessingException::class)
    fun test__updateDeck__wrongDeck() {
        // given
        val storage = prefilledStorage.value
        val deck = mockDeck(id = 777L.asDeckId())

        // when
        storage.updateDeck(deck, "some name")
    }

    @Test(expected = DataProcessingException::class)
    fun test__updateDeck__nameUnique() {
        // given
        val storage = prefilledStorage.value
        val name = "My new deck"
        storage.addDeck(domain, name)
        val deck = storage.addDeck(domain, "Some deck")

        // when
        storage.updateDeck(deck, name)
    }

    @Test
    fun test__updateDeck__sameNameDifferentDomains() {
        // given
        val storage = prefilledStorage.value
        val lang = storage.addLanguage("French")
        val domain2 = storage.createDomain("Another domain", lang, domain.langTranslations())

        val deck1 = storage.addDeck(domain, "Some deck")
        val deck2 = storage.addDeck(domain2, "A deck")

        val name = "My new deck"

        // when
        val updated1 = storage.updateDeck(deck1, name)
        val updated2 = storage.updateDeck(deck2, name)

        // assert
        assertDeckCorrect(updated1, name, domain)
        assertDeckCorrect(updated2, name, domain2)
    }

    @Test(expected = DataProcessingException::class)
    fun test__deleteDeck__wrongDeck() {
        // given
        val storage = prefilledStorage.value
        val deck = mockDeck(id = 77L.asDeckId())

        // when
        storage.deleteDeck(deck)
    }

    @Test(expected = DataProcessingException::class)
    fun test__updateCardLearningProgress__cardIncorrect() {
        // when
        val storage = prefilledStorage.value
        val newLearningProgress = mockLearningProgress(today.plusDays(8), 8)

        val dummyCard = mockCard(domain = domain)

        // when
        storage.updateCardLearningProgress(dummyCard, newLearningProgress)
    }
}