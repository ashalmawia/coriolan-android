package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.storage.sqlite.SqliteStorage
import com.ashalmawia.coriolan.learning.LearningExerciseDescriptor
import com.ashalmawia.coriolan.learning.scheduler.sr.SRState
import com.ashalmawia.coriolan.learning.scheduler.today
import com.ashalmawia.coriolan.model.*
import org.junit.Assert
import org.junit.Test
import org.robolectric.RuntimeEnvironment
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ConstrantStorageTest {

    private val exercise = LearningExerciseDescriptor()
    private val exercises = listOf(exercise)

    private lateinit var domain: Domain

    private val prefilledStorage: Lazy<Repository> = lazy {
        val it = createStorage()
        addMockLanguages(it)
        domain = it.createDomain("Default", langOriginal(), langTranslations())
        it
    }
    private val emptyStorage: Lazy<Repository> = lazy { createStorage() }

    private fun createStorage() = SqliteStorage(RuntimeEnvironment.application, exercises)

    @Test(expected = DataProcessingException::class)
    fun `test__addLanguage__nameUnique`() {
        // given
        val storage = emptyStorage.value
        val value = "Russian"

        // when
        storage.addLanguage(value)
        storage.addLanguage(value)
    }

    @Test(expected = DataProcessingException::class)
    fun `test__addExpression__valueUnique`() {
        // given
        val storage = prefilledStorage.value

        val value = "shrimp"
        val type = ExpressionType.WORD
        val lang = domain.langOriginal()

        // when
        storage.addExpression(value, type, lang)
        storage.addExpression(value, type, lang)
    }

    @Test
    fun `test__addExpression__sameValueDifferentLangs`() {
        // given
        val storage = prefilledStorage.value

        val value = "shrimp"
        val type = ExpressionType.WORD

        // when
        storage.addExpression(value, type, domain.langOriginal())
        storage.addExpression(value, type, domain.langTranslations())
    }

    @Test(expected = DataProcessingException::class)
    fun `test__addExpression__languageIncorrect`() {
        // given
        val storage = emptyStorage.value

        val value = "shrimp"
        val type = ExpressionType.WORD
        val lang = mockLanguage(value = "Russian")

        // when
        storage.addExpression(value, type, lang)
    }

    @Test(expected = DataProcessingException::class)
    fun `test__deleteExpression__expressionIncorrect__emptyStorage`() {
        // given
        val storage = prefilledStorage.value

        val type = ExpressionType.WORD
        val expression = Expression(5L, "креветка", type, domain.langTranslations())

        // when
        storage.deleteExpression(expression)
    }

    @Test(expected = DataProcessingException::class)
    fun `test__deleteExpression__expressionIncorrect__notPresent`() {
        // given
        val storage = prefilledStorage.value

        val type = ExpressionType.WORD

        storage.addExpression("shrimp", type, domain.langOriginal())
        storage.addExpression("креветка", type, domain.langTranslations())

        val expression = Expression(5L, "spring", type, domain.langOriginal())

        // when
        storage.deleteExpression(expression)
    }

    @Test(expected = DataProcessingException::class)
    fun `test__createDomain__nameUnique`() {
        // given
        val storage = prefilledStorage.value
        val name = "My domain"

        // when
        storage.createDomain(name, domain.langOriginal(), domain.langTranslations())
        storage.createDomain(name, domain.langOriginal(), domain.langTranslations())
    }

    @Test(expected = DataProcessingException::class)
    fun `test__createDomain__langOriginalIncorrect`() {
        // given
        val storage = prefilledStorage.value
        val name = "My domain"

        // when
        storage.createDomain(name, mockLanguage(5L, "Bulgarian"), domain.langTranslations())
    }

    @Test(expected = DataProcessingException::class)
    fun `test__createDomain__langTranslationsIncorrect`() {
        // given
        val storage = prefilledStorage.value
        val name = "My domain"

        // when
        storage.createDomain(name, domain.langOriginal(), mockLanguage(7L, "Romanian"))
    }

    @Test(expected = DataProcessingException::class)
    fun `test__addCard__domainIncorrect`() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val original = storage.addExpression("shrimp", ExpressionType.WORD, domain.langOriginal())
        val translation = storage.addExpression("креветка", ExpressionType.WORD, domain.langTranslations())

        val dummyDomain = Domain(5L, "some name", domain.langOriginal(), domain.langTranslations())

        // when
        storage.addCard(dummyDomain, deck.id, original, listOf(translation))
    }

    @Test(expected = DataProcessingException::class)
    fun `test__addCard__deckIncorrect`() {
        // given
        val storage = prefilledStorage.value

        val original = storage.addExpression("shrimp", ExpressionType.WORD, domain.langOriginal())
        val translation = storage.addExpression("креветка", ExpressionType.WORD, domain.langTranslations())

        val dummyDeckId = 5L

        // when
        storage.addCard(domain, dummyDeckId, original, listOf(translation))
    }

    @Test(expected = DataProcessingException::class)
    fun `test__addCard__originalIncorrect`() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val translation = storage.addExpression("креветка", ExpressionType.WORD, domain.langTranslations())

        val dummyOriginal = Expression(10L, "shrimp", ExpressionType.WORD, domain.langOriginal())

        // when
        storage.addCard(domain, deck.id, dummyOriginal, listOf(translation))
    }

    @Test(expected = DataProcessingException::class)
    fun `test__addCard__dummyTranslation`() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val original = storage.addExpression("shrimp", ExpressionType.WORD, domain.langOriginal())

        val dummyTranslation = Expression(15L, "креветка", ExpressionType.WORD, domain.langTranslations())

        // when
        storage.addCard(domain, deck.id, original, listOf(dummyTranslation))
    }

    @Test(expected = DataProcessingException::class)
    fun `test__addCard__dummyTranslation2`() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val original = storage.addExpression("shrimp", ExpressionType.WORD, domain.langOriginal())
        val translation = storage.addExpression("креветка", ExpressionType.WORD, domain.langTranslations())

        val dummyTranslation = Expression(11L, "dummy", ExpressionType.WORD, domain.langTranslations())

        // when
        storage.addCard(domain, deck.id, original, listOf(translation, dummyTranslation))
    }

    @Test(expected = DataProcessingException::class)
    fun `test__addCard__noTranslation`() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val original = storage.addExpression("shrimp", ExpressionType.WORD, domain.langOriginal())

        // when
        storage.addCard(domain, deck.id, original, listOf())
    }

    @Test(expected = DataProcessingException::class)
    fun `test__updateCard__cardAbsent`() {
        // given
        val storage = prefilledStorage.value
        val card = mockCard()

        // when
        storage.updateCard(card, 2L, mockExpression(), listOf(mockExpression()))
    }

    @Test(expected = DataProcessingException::class)
    fun `test__updateCard__deckIncorrect`() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val original = storage.addExpression("shrimp", ExpressionType.WORD, domain.langOriginal())
        val translation = storage.addExpression("креветка", ExpressionType.WORD, domain.langTranslations())

        val card = storage.addCard(domain, deck.id, original, listOf(translation))

        val dummyDeckId = 5L

        // when
        storage.updateCard(card, dummyDeckId, original, listOf(translation))
    }

    @Test(expected = DataProcessingException::class)
    fun `test__updateCard__originalIncorrect`() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val original = storage.addExpression("shrimp", ExpressionType.WORD, domain.langOriginal())
        val translation = storage.addExpression("креветка", ExpressionType.WORD, domain.langTranslations())

        val card = storage.addCard(domain, deck.id, original, listOf(translation))

        val dummyOriginal = Expression(10L, "shrimp", ExpressionType.WORD, domain.langOriginal())

        // when
        storage.updateCard(card, deck.id, dummyOriginal, listOf(translation))
    }

    @Test(expected = DataProcessingException::class)
    fun `test__updateCard__dummyTranslation`() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val original = storage.addExpression("shrimp", ExpressionType.WORD, domain.langOriginal())
        val translation = storage.addExpression("креветка", ExpressionType.WORD, domain.langTranslations())

        val card = storage.addCard(domain, deck.id, original, listOf(translation))

        val dummyTranslation = Expression(15L, "креветка", ExpressionType.WORD, domain.langTranslations())

        // when
        storage.updateCard(card, deck.id, original, listOf(dummyTranslation))
    }

    @Test(expected = DataProcessingException::class)
    fun `test__updateCard__dummyTranslation2`() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val original = storage.addExpression("shrimp", ExpressionType.WORD, domain.langOriginal())
        val translation = storage.addExpression("креветка", ExpressionType.WORD, domain.langTranslations())

        val card = storage.addCard(domain, deck.id, original, listOf(translation))

        val dummyTranslation = Expression(11L, "dummy", ExpressionType.WORD, domain.langTranslations())

        // when
        storage.updateCard(card, deck.id, original, listOf(translation, dummyTranslation))
    }

    @Test(expected = DataProcessingException::class)
    fun `test__updateCard__noTranslation`() {
        // given
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val original = storage.addExpression("shrimp", ExpressionType.WORD, domain.langOriginal())
        val translation = storage.addExpression("креветка", ExpressionType.WORD, domain.langTranslations())

        val card = storage.addCard(domain, deck.id, original, listOf(translation))

        // when
        storage.updateCard(card, deck.id, original, listOf())
    }

    @Test(expected = DataProcessingException::class)
    fun `test__deleteCard__absent`() {
        // given
        val storage = prefilledStorage.value

        val notAddedCard = mockCard()
        val searched = storage.cardById(notAddedCard.id, domain)
        Assert.assertNull("card is not in the DB", searched)

        // when
        storage.deleteCard(notAddedCard)
    }

    @Test(expected = DataProcessingException::class)
    fun `test__addDeck__domainIncorrect`() {
        // given
        val storage = emptyStorage.value
        val name = "My new deck"

        val dummyDomain = Domain(5L, "some name", mockLanguage(), mockLanguage())

        // when
        storage.addDeck(dummyDomain, name)
    }

    @Test(expected = DataProcessingException::class)
    fun `test__addDeck__nameUnique`() {
        // given
        val storage = prefilledStorage.value

        val name = "My new deck"

        // when
        storage.addDeck(domain, name)
        storage.addDeck(domain, name)
    }

    @Test
    fun `test__addDeck__sameNameDifferentDomains`() {
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
    fun `test__updateDeck__wrongDeck`() {
        // given
        val storage = prefilledStorage.value
        val deck = mockDeck(id = 777L)

        // when
        storage.updateDeck(deck, "some name")
    }

    @Test(expected = DataProcessingException::class)
    fun `test__updateDeck__nameUnique`() {
        // given
        val storage = prefilledStorage.value
        val name = "My new deck"
        storage.addDeck(domain, name)
        val deck = storage.addDeck(domain, "Some deck")

        // when
        storage.updateDeck(deck, name)
    }

    @Test
    fun `test__updateDeck__sameNameDifferentDomains`() {
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
    fun `test__updateSRCardState__cardIncorrect`() {
        // when
        val storage = prefilledStorage.value
        val newState = SRState(today().plusDays(8), 8)

        val dummyCard = mockCard(domain = domain)

        // when
        storage.updateSRCardState(dummyCard, newState, exercise.stableId)
    }

    @Test(expected = DataProcessingException::class)
    fun `test__updateSRCardState__exerciseIncorrect`() {
        // when
        val storage = prefilledStorage.value

        val deck = storage.addDeck(domain, "My deck")

        val original = storage.addExpression("shrimp", ExpressionType.WORD, domain.langOriginal())
        val translation = storage.addExpression("креветка", ExpressionType.WORD, domain.langTranslations())

        val card = storage.addCard(domain, deck.id, original, listOf(translation))
        val newState = SRState(today().plusDays(8), 8)

        val dummyExerciseId = "dummy"

        // when
        storage.updateSRCardState(card, newState, dummyExerciseId)
    }
}