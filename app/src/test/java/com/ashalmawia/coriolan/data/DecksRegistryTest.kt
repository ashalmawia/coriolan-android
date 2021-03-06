package com.ashalmawia.coriolan.data

import android.content.Context
import com.ashalmawia.coriolan.data.importer.reversedTo
import com.ashalmawia.coriolan.data.prefs.MockPreferences
import com.ashalmawia.coriolan.data.storage.MockRepository
import com.ashalmawia.coriolan.data.storage.justAddExpression
import com.ashalmawia.coriolan.learning.ExercisesRegistry
import com.ashalmawia.coriolan.learning.MockExercisesRegistry
import com.ashalmawia.coriolan.model.*
import com.ashalmawia.coriolan.util.forward
import com.ashalmawia.coriolan.util.reverse
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

private const val DEFAULT_DECK_NAME = "Default"

@RunWith(MockitoJUnitRunner::class)
class DecksRegistryTest {

    private lateinit var domain: Domain
    private lateinit var mockPrefs: MockPreferences
    private lateinit var mockRepository: MockRepository
    private lateinit var exercisesRegistry: ExercisesRegistry
    private lateinit var context: Context

    @Before
    fun before() {
        mockPrefs = MockPreferences()
        mockRepository = MockRepository()
        exercisesRegistry = MockExercisesRegistry()

        addMockLanguages(mockRepository)
        domain = mockRepository.createDomain("Default", langOriginal(), langTranslations())

        val context = mock(Context::class.java)
        `when`(context.getString(anyInt())).thenReturn(DEFAULT_DECK_NAME)
        this.context = context
    }

    @Test
    fun preinitializeTestNoDefaultDeck() {
        // when
        createDeckRegistry()

        // then
        assertEquals("Default deck is initialized", 1, mockRepository.decks.size)
        assertEquals("Default deck is initialized correctly", DEFAULT_DECK_NAME, mockRepository.decks[0].name)
    }

    private fun createDeckRegistry() = DecksRegistry(context, domain, mockRepository, exercisesRegistry)

    @Test
    fun preinitializeTestHasDefaultDeck() {
        // given
        val deck = mockRepository.addDeck(domain, "Some deck")

        // when
        createDeckRegistry()

        // then
        assertEquals("Default deck is not added", 1, mockRepository.decks.size)
        assertEquals("Default deck is not added", deck, mockRepository.decks[0])
    }

    @Test
    fun `addCardsToDeck__singleTranslation`() {
        // given
        val deck = mockRepository.addDeck(domain, "Mock")
        val cardsData = arrayListOf(
                mockCardData("original", "translation", deck.id),
                mockCardData("original2", "translation2", deck.id)
        )

        val mockRegistry = createDeckRegistry()

        // when
        mockRegistry.addCardsToDeck(cardsData)

        // then
        val cardsOfDeck = mockRepository.cardsOfDeck(deck)
        verifyAddedCardsCorrect(cardsData, cardsOfDeck, domain)
    }

    @Test
    fun `addCardsToDeck__multipleTranslations`() {
        // given
        val deck = mockRepository.addDeck(domain, "Mock")
        val cardsData = arrayListOf(
                mockCardData("shrimp", "креветка", deck.id),
                mockCardData("spring", listOf("весна", "источник"), deck.id)
        )

        val mockRegistry = createDeckRegistry()

        // when
        mockRegistry.addCardsToDeck(cardsData)

        // then
        val cardsOfDeck = mockRepository.cardsOfDeck(deck)
        verifyAddedCardsCorrect(cardsData, cardsOfDeck, domain)
    }

    @Test
    fun `addCardsToDeck__expressionsReused__singleTranslation`() {
        // given
        val deck = mockRepository.addDeck(domain, "Mock")

        val repeatedOriginalValue = "spring"
        val repeatedTranslationValue = "весна"

        val uniqueOriginalValue = "original"
        val uniqueTranslationValue = "перевод"

        val repeatedOriginal = mockRepository.justAddExpression(repeatedOriginalValue, domain.langOriginal())
        val repeatedTranslation = mockRepository.justAddExpression(repeatedTranslationValue, domain.langTranslations())

        val cardsData = arrayListOf(
                mockCardData(repeatedOriginalValue, uniqueTranslationValue, deck.id),
                mockCardData(uniqueOriginalValue, repeatedTranslationValue, deck.id)
        )

        val mockRegistry = createDeckRegistry()

        // when
        mockRegistry.addCardsToDeck(cardsData)

        // then
        val cardsOfDeck = mockRepository.cardsOfDeck(deck)
        val forward = cardsOfDeck.filter { it.type == CardType.FORWARD }
        val reverse = cardsOfDeck.filter { it.type == CardType.REVERSE }
        verifyAddedCardsCorrect(cardsData, cardsOfDeck, domain)
        assertExpressionCorrect(repeatedOriginal, forward[0].original)
        assertExpressionCorrect(repeatedOriginal, reverse[0].translations[0])
        assertExpressionCorrect(repeatedTranslation, forward[1].translations[0])
        assertExpressionCorrect(repeatedTranslation, reverse[1].original)
    }

    @Test
    fun `addCardsToDeck__expressionsReused__multipleTranslations`() {
        // given
        val deck = mockRepository.addDeck(domain, "Mock")

        val repeatedOriginalValue = "spring"
        val repeatedTranslationValue = "весна"
        val repeatedTranslation2Value = "источник"
        val uniqueTranslationValue = "перевод"

        val repeatedOriginal = mockRepository.justAddExpression(repeatedOriginalValue, domain.langOriginal())
        val repeatedTranslation = mockRepository.justAddExpression(repeatedTranslationValue, domain.langTranslations())
        val repeatedTranslation2 = mockRepository.justAddExpression(repeatedTranslation2Value, domain.langTranslations())

        val cardsData = arrayListOf(
                mockCardData(
                        repeatedOriginalValue,
                        listOf(repeatedTranslationValue, uniqueTranslationValue, repeatedTranslation2Value),
                        deck.id)
        )

        val mockRegistry = createDeckRegistry()

        // when
        mockRegistry.addCardsToDeck(cardsData)

        // then
        val cardsOfDeck = mockRepository.cardsOfDeck(deck)
        verifyAddedCardsCorrect(cardsData, cardsOfDeck, domain)
        assertExpressionCorrect(repeatedOriginal, cardsOfDeck[0].original)
        assertExpressionCorrect(repeatedTranslation, cardsOfDeck[0].translations[0])
        assertExpressionCorrect(repeatedTranslation2, cardsOfDeck[0].translations[2])
    }

    @Test
    fun `addCardToDeck no transcription`() {
        // given
        val deck = mockRepository.addDeck(domain, "Mock")

        val originalValue = "spring"
        val firstTranslationValue = "весна"
        val secondTranslationValue = "источник"

        val mockRegistry = createDeckRegistry()

        val cardData = mockCardData(originalValue, listOf(firstTranslationValue, secondTranslationValue), deck.id)

        // when
        val result = mockRegistry.addCardToDeck(cardData)

        // then
        assertTrue(result is AddCardResult.Success)

        val card = mockRepository.cards[0]
        assertExpressionCorrect(card.original, originalValue, domain.langOriginal())
        assertExpressionCorrect(card.translations[0], firstTranslationValue, domain.langTranslations())
        assertExpressionCorrect(card.translations[1], secondTranslationValue, domain.langTranslations())
        assertCardCorrect(card, cardData, domain)
    }

    @Test
    fun `addCardToDeck with transcription`() {
        // given
        val deck = mockRepository.addDeck(domain, "Mock")

        val originalValue = "spring"
        val firstTranslationValue = "весна"
        val secondTranslationValue = "источник"
        val transcription = "[sprɪŋ]"

        val mockRegistry = createDeckRegistry()

        val cardData = mockCardData(
                originalValue,
                listOf(firstTranslationValue, secondTranslationValue),
                deck.id,
                transcription
        )

        // when
        val result = mockRegistry.addCardToDeck(cardData)

        // then
        assertTrue(result is AddCardResult.Success)

        val card = mockRepository.cards[0]
        assertExpressionCorrect(card.original, originalValue, domain.langOriginal())
        assertExpressionCorrect(card.translations[0], firstTranslationValue, domain.langTranslations())
        assertExpressionCorrect(card.translations[1], secondTranslationValue, domain.langTranslations())
        assertCardCorrect(card, cardData, domain)
    }

    @Test
    fun `addCardToDeck__testMatching__originalMatch__newTranslation`() {
        // given
        val deck = mockRepository.addDeck(domain, "Mock")

        val repeatedOriginalValue = "spring"
        val repeatedOriginal = mockRepository.justAddExpression(repeatedOriginalValue, domain.langOriginal())

        val firstTranslation = mockRepository.justAddExpression("весна", domain.langTranslations())

        val forward = mockRepository.addCard(domain, deck.id, repeatedOriginal, listOf(firstTranslation))
        val reverse = mockRepository.addCard(domain, deck.id, firstTranslation, listOf(repeatedOriginal))

        val mockRegistry = createDeckRegistry()

        val secondTranslationValue = "источник"
        val cardData = mockCardData(repeatedOriginalValue, listOf(secondTranslationValue), deck.id)

        // when
        val result = mockRegistry.addCardToDeck(cardData)

        // then
        assertTrue(result is AddCardResult.Success)
        assertCardsMerged(deck, forward, repeatedOriginal, firstTranslation, secondTranslationValue, reverse)
    }

    @Test
    fun `addCardToDeck__testMatching__originalMatch__partialDuplicateAndNewTranslation`() {
        // given
        val deck = mockRepository.addDeck(domain, "Mock")

        val repeatedOriginalValue = "spring"
        val repeatedOriginal = mockRepository.justAddExpression(repeatedOriginalValue, domain.langOriginal())

        val firstTranslationValue = "весна"
        val firstTranslation = mockRepository.justAddExpression(firstTranslationValue, domain.langTranslations())

        val forward = mockRepository.addCard(domain, deck.id, repeatedOriginal, listOf(firstTranslation))
        val reverse = mockRepository.addCard(domain, deck.id, firstTranslation, listOf(repeatedOriginal))

        val mockRegistry = createDeckRegistry()

        val secondTranslationValue = "источник"
        val cardData = mockCardData(
                repeatedOriginalValue, listOf(firstTranslationValue, secondTranslationValue), deck.id
        )

        // when
        val result = mockRegistry.addCardToDeck(cardData)

        // then
        assertTrue(result is AddCardResult.Success)
        assertCardsMerged(deck, forward, repeatedOriginal, firstTranslation, secondTranslationValue, reverse)
    }

    private fun assertCardsMerged(deck: Deck, forward: Card, repeatedOriginal: Expression, firstTranslation: Expression, secondTranslationValue: String, reverse: Card) {
        val cardsOfDeck = mockRepository.cardsOfDeck(deck)
        assertEquals("new forward card was not added", 1, cardsOfDeck.forward().size)
        assertEquals("forward: card id was kept", forward.id, cardsOfDeck.forward()[0].id)
        assertExpressionCorrect(repeatedOriginal, cardsOfDeck.forward()[0].original)
        assertEquals("forward: translations are merged", 2, cardsOfDeck.forward()[0].translations.size)
        assertExpressionCorrect(firstTranslation, cardsOfDeck.forward()[0].translations[0])
        assertEquals("forward: translations are merged", secondTranslationValue, cardsOfDeck.forward()[0].translations[1].value)
        assertEquals("new reverse card was added", 2, cardsOfDeck.reverse().size)
        assertEquals("reverse is kept", reverse, cardsOfDeck.reverse()[0])
        assertEquals("new reverse is added", secondTranslationValue, cardsOfDeck.reverse()[1].original.value)
        assertTrue("new reverse is added", cardsOfDeck.reverse()[1].translations.size == 1)
        assertExpressionCorrect(repeatedOriginal, cardsOfDeck.reverse()[1].translations[0])
    }

    @Test
    fun `addCardToDeck__testMatching__translationsMatch`() {
        // given
        val deck = mockRepository.addDeck(domain, "Mock")

        val firstOriginalValue = "весна"
        val firstOriginal = mockRepository.justAddExpression(firstOriginalValue, domain.langOriginal())

        val repeatedTranslationValue = "spring"
        val repeatedTranslation = mockRepository.justAddExpression(repeatedTranslationValue, domain.langTranslations())

        val forward = mockRepository.addCard(domain, deck.id, firstOriginal, listOf(repeatedTranslation))
        val reverse = mockRepository.addCard(domain, deck.id, repeatedTranslation, listOf(firstOriginal))

        val mockRegistry = createDeckRegistry()

        val secondOriginalValue = "источник"
        val cardData = mockCardData(secondOriginalValue, listOf(repeatedTranslationValue), deck.id)

        // when
        val result = mockRegistry.addCardToDeck(cardData)

        // then
        assertTrue(result is AddCardResult.Success)

        val cardsOfDeck = mockRepository.cardsOfDeck(deck)
        assertEquals("new forward card was added", 2, cardsOfDeck.forward().size)
        assertEquals("old forward is kept", forward, cardsOfDeck.forward()[0])
        assertEquals("new forward is added", secondOriginalValue, cardsOfDeck.forward()[1].original.value)
        assertEquals("new forward is added", listOf(repeatedTranslation), cardsOfDeck.forward()[1].translations)
        assertEquals("new reverse card was not added", 1, cardsOfDeck.reverse().size)
        assertEquals("reverse: card id was kept", reverse.id, cardsOfDeck.reverse()[0].id)
        assertEquals("reverse: original is kept", repeatedTranslation, cardsOfDeck.reverse()[0].original)
        assertEquals("reverse: translations are merged", 2, cardsOfDeck.reverse()[0].translations.size)
        assertEquals("reverse: translations are merged", firstOriginal, cardsOfDeck.reverse()[0].translations[0])
        assertEquals("reverse: translations are merged", secondOriginalValue, cardsOfDeck.reverse()[0].translations[1].value)
    }

    @Test
    fun `addCardToDeck__testMatching__noMatch`() {
        // given
        val deck = mockRepository.addDeck(domain, "Mock")

        val firstOriginal = mockRepository.justAddExpression("spring", domain.langOriginal())
        val firstTranslation = mockRepository.justAddExpression("весна", domain.langTranslations())

        val forward = mockRepository.addCard(domain, deck.id, firstOriginal, listOf(firstTranslation))
        val reverse = mockRepository.addCard(domain, deck.id, firstTranslation, listOf(firstOriginal))

        val mockRegistry = createDeckRegistry()

        val secondOriginalValue = "shrimp"
        val secondTranslationValue = "креветка"
        val cardData = mockCardData(secondOriginalValue, listOf(secondTranslationValue), deck.id)

        // when
        val result = mockRegistry.addCardToDeck(cardData)

        // then
        assertTrue(result is AddCardResult.Success)

        val cardsOfDeck = mockRepository.cardsOfDeck(deck)
        assertEquals("new cards are added", 4, cardsOfDeck.size)
        assertEquals("old forward is kept", forward, cardsOfDeck.forward()[0])
        assertEquals("old reverse is kept", reverse, cardsOfDeck.reverse()[0])
        assertEquals("new forward is correct", secondOriginalValue, cardsOfDeck.forward()[1].original.value)
        assertEquals("new forward is correct", 1, cardsOfDeck.forward()[1].translations.size)
        assertEquals("new forward is correct", secondTranslationValue, cardsOfDeck.forward()[1].translations[0].value)
        assertEquals("new reverse is correct", secondTranslationValue, cardsOfDeck.reverse()[1].original.value)
        assertEquals("new reverse is correct", 1, cardsOfDeck.reverse()[1].translations.size)
        assertEquals("new reverse is correct", secondOriginalValue, cardsOfDeck.reverse()[1].translations[0].value)
    }

    @Test
    fun `addCardToDeck__testMatching__duplicate`() {
        // given
        val deck = mockRepository.addDeck(domain, "Mock")

        val repeatedOriginalValue = "spring"
        val repeatedOriginal = mockRepository.justAddExpression(repeatedOriginalValue, domain.langOriginal())

        val firstTranslationValue = "весна"
        val firstTranslation = mockRepository.justAddExpression(firstTranslationValue, domain.langTranslations())

        val forward = mockRepository.addCard(domain, deck.id, repeatedOriginal, listOf(firstTranslation))
        mockRepository.addCard(domain, deck.id, firstTranslation, listOf(repeatedOriginal))

        val mockRegistry = createDeckRegistry()

        val cardData = mockCardData(repeatedOriginalValue, listOf(firstTranslationValue), deck.id)

        // when
        val result = mockRegistry.addCardToDeck(cardData)

        // then
        assertTrue(result is AddCardResult.Duplicate)
        assertEquals(forward, (result as AddCardResult.Duplicate).card)
    }

    @Test
    fun `editCard__changeTypoInOriginal`() {
        // given
        val deckId = 1L

        val expression1 = mockRepository.justAddExpression("spring", domain.langOriginal())
        val expression2 = mockRepository.justAddExpression("весна", domain.langTranslations())
        val expression3 = mockRepository.justAddExpression("весло", domain.langTranslations())
        val expression4 = mockRepository.justAddExpression("источник", domain.langTranslations())

        mockRepository.addCard(domain, deckId, expression1, listOf(expression2, expression4))
        val card = mockRepository.addCard(domain, deckId, expression3, listOf(expression1))

        val mockRegistry = createDeckRegistry()

        // when
        val edited = mockRegistry.editCard(card,
                mockCardData("весна", listOf("spring"), deckId))

        // then
        assertNotNull("edit was successful", edited)
        assertEquals("correct card was edited", card.id, edited!!.id)
        assertExpressionCorrect(expression2, edited.original)
        assertEquals("translations are preserved", listOf(expression1), edited.translations)
        assertNull("orphan expressions are deleted", mockRepository.expressionById(expression3.id))
    }

    @Test
    fun `editCard__addTranslation`() {
        // given
        val deckId = 1L

        val expression1 = mockRepository.justAddExpression("spring", domain.langOriginal())
        val expression2 = mockRepository.justAddExpression("источник", domain.langTranslations())

        val card = mockRepository.addCard(domain, deckId, expression1, listOf(expression2))
        mockRepository.addCard(domain, deckId, expression2, listOf(expression1))

        val mockRegistry = createDeckRegistry()

        // when
        val edited = mockRegistry.editCard(card,
                mockCardData("spring", listOf("источник", "весна"), deckId))

        // then
        assertNotNull("edit was successful", edited)
        assertEquals("correct card was edited", card.id, edited!!.id)
        assertExpressionCorrect(expression1, edited.original)
        assertEquals("translations are updated", listOf(expression2,
                mockRepository.expressionByValues("весна", domain.langTranslations())), edited.translations)
    }

    @Test
    fun `editCard() add transcription`() {
        // given
        val deckId = 1L

        val expression1 = mockRepository.addExpression("spring", domain.langOriginal())
        val expression2 = mockRepository.addExpression("источник", domain.langTranslations())

        val card = mockRepository.addCard(domain, deckId, expression1, listOf(expression2))
        mockRepository.addCard(domain, deckId, expression2, listOf(expression1))

        val transcription = "[sprɪŋ]"

        val mockRegistry = createDeckRegistry()

        // when
        val edited = mockRegistry.editCard(card,
                mockCardData("spring", listOf("источник", "весна"), deckId, transcription))

        // then
        assertNotNull("edit was successful", edited)
        assertEquals("correct card was edited", card.id, edited!!.id)
        assertEquals("transcription is set", transcription,
                mockRepository.allExtrasForExpression(edited.original).transcription)
        assertNull("unrelated exprerssions are not affected",
                mockRepository.allExtrasForExpression(edited.translations[0]).transcription)
        assertEquals("repository is updated", edited, mockRepository.cardById(edited.id, domain))
    }

    @Test
    fun `editCard() change transcription`() {
        // given
        val deckId = 1L

        val expression1 = mockRepository.addExpression("spring", domain.langOriginal())
        val expression2 = mockRepository.justAddExpression("источник", domain.langTranslations())

        val card = mockRepository.addCard(domain, deckId, expression1, listOf(expression2))
        mockRepository.addCard(domain, deckId, expression2, listOf(expression1))

        val transcriptionNew = "[sprɪŋ]"

        val mockRegistry = createDeckRegistry()

        // when
        val edited = mockRegistry.editCard(card,
                mockCardData("spring", listOf("источник"), deckId, transcriptionNew))

        // then
        assertNotNull("edit was successful", edited)
        assertEquals("correct card was edited", card.id, edited!!.id)
        assertEquals("transcription is set", transcriptionNew,
                mockRepository.allExtrasForExpression(edited.original).transcription)
        assertNull("unrelated exprerssions are not affected",
                mockRepository.allExtrasForExpression(edited.translations[0]).transcription)
        assertEquals("repository is updated", edited, mockRepository.cardById(edited.id, domain))
    }

    @Test
    fun `editCard() delete transcription`() {
        // given
        val deckId = 1L

        val expression1 = mockRepository.addExpression("spring", domain.langOriginal())
        val expression2 = mockRepository.justAddExpression("источник", domain.langTranslations())

        val card = mockRepository.addCard(domain, deckId, expression1, listOf(expression2))
        mockRepository.addCard(domain, deckId, expression2, listOf(expression1))

        val mockRegistry = createDeckRegistry()

        // when
        val edited = mockRegistry.editCard(card,
                mockCardData("spring", listOf("источник"), deckId, null))

        // then
        assertNotNull("edit was successful", edited)
        assertEquals("correct card was edited", card.id, edited!!.id)
        assertNull("transcription is deleted",
                mockRepository.allExtrasForExpression(edited.original).transcription)
        assertNull("unrelated exprerssions are not affected",
                mockRepository.allExtrasForExpression(edited.translations[0]).transcription)
        assertEquals("repository is updated", edited, mockRepository.cardById(edited.id, domain))
    }

    @Test
    fun `editCard__nonExistentCard`() {
        // given
        val deckId = 1L

        val expression1 = mockRepository.justAddExpression("spring", domain.langOriginal())
        val expression2 = mockRepository.justAddExpression("источник", domain.langTranslations())

        mockRepository.addCard(domain, deckId, expression2, listOf(expression1))

        val mockRegistry = createDeckRegistry()

        val card = Card(77L, deckId, domain, expression1, listOf(expression2))

        // when
        val edited = mockRegistry.editCard(card,
                mockCardData("spring", listOf("источник", "весна"), deckId))

        // then
        assertNull("card was not found", edited)
    }

    @Test
    fun `deleteCard`() {
        // given
        val deckId = 1L

        val expression1 = mockRepository.justAddExpression("spring", domain.langOriginal())
        val expression2 = mockRepository.justAddExpression("весна", domain.langTranslations())
        val expression3 = mockRepository.justAddExpression("источник", domain.langTranslations())
        val expression4 = mockRepository.justAddExpression("rocket", domain.langOriginal())
        val expression5 = mockRepository.justAddExpression("ракета", domain.langTranslations())

        val forward = mockRepository.addCard(domain, deckId, expression1, listOf(expression2, expression3))
//        val reverse1 = mockRepository.addCard(domain, deckId, expression2, listOf(expression1))
//        val reverse2 = mockRepository.addCard(domain, deckId, expression3, listOf(expression1))
        val otherForward = mockRepository.addCard(domain, deckId, expression4, listOf(expression5))
        val otherReverse = mockRepository.addCard(domain, deckId, expression5, listOf(expression4))

        val mockRegistry = createDeckRegistry()

        // when
        mockRegistry.deleteCard(forward)

        // then
        assertNull("forward is deleted", mockRepository.cardById(forward.id, domain))
//        assertNull("reverses are deleted", mockRepository.cardById(reverse1.id, domain))
//        assertNull("reverses are deleted", mockRepository.cardById(reverse2.id, domain))

//        assertNull("orphan expressions are deleted", mockRepository.expressionById(expression1.id))
//        assertNull("orphan expressions are deleted", mockRepository.expressionById(expression2.id))
//        assertNull("orphan expressions are deleted", mockRepository.expressionById(expression3.id))

        assertNotNull("other cards are preserved", mockRepository.cardById(otherForward.id, domain))
        assertNotNull("other cards are preserved", mockRepository.cardById(otherReverse.id, domain))
        assertNotNull("used expressions are preserved", mockRepository.expressionById(expression4.id))
        assertNotNull("used expressions are preserved", mockRepository.expressionById(expression5.id))
    }
}

private fun verifyAddedCardsCorrect(cardsData: ArrayList<CardData>, cardsOfDeck: List<Card>, domain: Domain) {
    val forwardCount = cardsData.size
    val reverseCount = cardsData.sumBy { it.translations.size }
    val expectedCount = forwardCount + reverseCount

    assertEquals("amount of cards is correct", expectedCount, cardsOfDeck.size)
    assertEquals("amount of forward cards is correct", forwardCount, cardsOfDeck.count { it.type == CardType.FORWARD })
    assertEquals("amount of reverse cards is correct", reverseCount, cardsOfDeck.count { it.type == CardType.REVERSE })

    for (data in cardsData) {
        val forward = cardsOfDeck.find { it.type == CardType.FORWARD && it.original.value == data.original }
        assertCardCorrect(forward, data, domain)

        val reversedCards = reversedTo(data)
        for (reversedData in reversedCards) {
            val reverse = cardsOfDeck.find { it.type == CardType.REVERSE && it.original.value == reversedData.original }
            assertCardCorrectReverse(reverse, reversedData, domain)
        }
    }
}