package com.ashalmawia.coriolan.data.storage

import android.content.Context
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.data.importer.reversedTo
import com.ashalmawia.coriolan.data.prefs.MockPreferences
import com.ashalmawia.coriolan.learning.scheduler.emptyState
import com.ashalmawia.coriolan.model.*
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
class DeckRegistryTest {

    private lateinit var mockPrefs: MockPreferences
    private lateinit var mockRepository: MockRepository
    private lateinit var context: Context

    @Before
    fun before() {
        mockPrefs = MockPreferences()
        mockRepository = MockRepository()

        val context = mock(Context::class.java)
        `when`(context.getString(anyInt())).thenReturn(DEFAULT_DECK_NAME)
        this.context = context
    }

    @Test
    fun preinitializeTestNoDefaultDeck() {
        // when
        val registry = DecksRegistry(context, mockPrefs, mockRepository)

        // then
        assertNotNull("Default deck is initialized", registry.default())
        assertEquals("Default deck is initialized correctly", DEFAULT_DECK_NAME, registry.default().name)
    }

    @Test
    fun preinitializeTestHasDefaultDeck() {
        // given
        mockRepository.addDeck("Some deck")
        val defaultDeck = mockRepository.addDeck(DEFAULT_DECK_NAME)
        mockRepository.addDeck("Some other deck")

        mockPrefs.setDefaultDeckId(defaultDeck.id)

        // when
        val registry = DecksRegistry(context, mockPrefs, mockRepository)

        // then
        assertEquals("Default deck is set correctly", defaultDeck, registry.default())
    }

    @Test
    fun `addCardsToDeck__singleTranslation`() {
        // given
        val deck = mockDeck()
        val cardsData = arrayListOf(
                mockCardData("original", "translation", deck.id),
                mockCardData("original2", "translation2", deck.id)
        )

        val mockRegistry = DecksRegistry(context, mockPrefs, mockRepository)

        // when
        mockRegistry.addCardsToDeck(cardsData)

        // then
        val cardsOfDeck = mockRepository.cardsOfDeck(deck)
        verifyAddedCardsCorrect(cardsData, cardsOfDeck)
    }

    @Test
    fun `addCardsToDeck__multipleTranslations`() {
        // given
        val deck = mockDeck()
        val cardsData = arrayListOf(
                mockCardData("shrimp", "креветка", deck.id),
                mockCardData("spring", listOf("весна", "источник"), deck.id)
        )

        val mockRegistry = DecksRegistry(context, mockPrefs, mockRepository)

        // when
        mockRegistry.addCardsToDeck(cardsData)

        // then
        val cardsOfDeck = mockRepository.cardsOfDeck(deck)
        verifyAddedCardsCorrect(cardsData, cardsOfDeck)
    }

    @Test
    fun `addCardsToDeck__expressionsReused__singleTranslation`() {
        // given
        val deck = mockDeck()
        val type = ExpressionType.WORD

        val repeatedOriginalValue = "spring"
        val repeatedTranslationValue = "весна"

        val uniqueOriginalValue = "original"
        val uniqueTranslationValue = "перевод"

        val repeatedOriginal = mockRepository.addExpression(repeatedOriginalValue, type, langOriginal())
        val repeatedTranslation = mockRepository.addExpression(repeatedTranslationValue, type, langTranslations())

        val cardsData = arrayListOf(
                mockCardData(repeatedOriginalValue, uniqueTranslationValue, deck.id),
                mockCardData(uniqueOriginalValue, repeatedTranslationValue, deck.id)
        )

        val mockRegistry = DecksRegistry(context, mockPrefs, mockRepository)

        // when
        mockRegistry.addCardsToDeck(cardsData)

        // then
        val cardsOfDeck = mockRepository.cardsOfDeck(deck)
        val forward = cardsOfDeck.filter { it.type == CardType.FORWARD }
        val reverse = cardsOfDeck.filter { it.type == CardType.REVERSE }
        verifyAddedCardsCorrect(cardsData, cardsOfDeck)
        assertEquals("expression is reused", repeatedOriginal, forward[0].original)
        assertEquals("expression is reused", repeatedOriginal, reverse[0].translations[0])
        assertEquals("expression is reused", repeatedTranslation, forward[1].translations[0])
        assertEquals("expression is reused", repeatedTranslation, reverse[1].original)
    }

    @Test
    fun `addCardsToDeck__expressionsReused__multipleTranslations`() {
        // given
        val deck = mockDeck()
        val type = ExpressionType.WORD

        val repeatedOriginalValue = "spring"
        val repeatedTranslationValue = "весна"
        val repeatedTranslation2Value = "источник"
        val uniqueTranslationValue = "перевод"

        val repeatedOriginal = mockRepository.addExpression(repeatedOriginalValue, type, langOriginal())
        val repeatedTranslation = mockRepository.addExpression(repeatedTranslationValue, type, langTranslations())
        val repeatedTranslation2 = mockRepository.addExpression(repeatedTranslation2Value, type, langTranslations())

        val cardsData = arrayListOf(
                mockCardData(
                        repeatedOriginalValue,
                        listOf(repeatedTranslationValue, uniqueTranslationValue, repeatedTranslation2Value),
                        deck.id)
        )

        val mockRegistry = DecksRegistry(context, mockPrefs, mockRepository)

        // when
        mockRegistry.addCardsToDeck(cardsData)

        // then
        val cardsOfDeck = mockRepository.cardsOfDeck(deck)
        verifyAddedCardsCorrect(cardsData, cardsOfDeck)
        assertEquals("expression is reused", repeatedOriginal, cardsOfDeck[0].original)
        assertEquals("expression is reused", repeatedTranslation, cardsOfDeck[0].translations[0])
        assertEquals("expression is reused", repeatedTranslation2, cardsOfDeck[0].translations[2])
    }

    @Test
    fun `editCard__changeTypoInOriginal`() {
        // given
        val type = ExpressionType.WORD
        val deckId = 1L

        val expression1 = mockRepository.addExpression("spring", type, langOriginal())
        val expression2 = mockRepository.addExpression("весна", type, langTranslations())
        val expression3 = mockRepository.addExpression("весло", type, langTranslations())
        val expression4 = mockRepository.addExpression("источник", type, langTranslations())

        mockRepository.addCard(deckId, expression1, listOf(expression2, expression4))
        val card = mockRepository.addCard(deckId, expression3, listOf(expression1))

        val mockRegistry = DecksRegistry(context, mockPrefs, mockRepository)

        // when
        val edited = mockRegistry.editCard(card,
                CardData("весна", langTranslations(), listOf("spring"), langOriginal(), deckId, type))

        // then
        assertNotNull("edit was successful", edited)
        assertEquals("correct card was edited", card.id, edited!!.id)
        assertEquals("expressions are reused", expression2, edited.original)
        assertEquals("translations are preserved", listOf(expression1), edited.translations)
        assertNull("orphan expressions are deleted", mockRepository.expressionById(expression3.id))
    }

    @Test
    fun `editCard__addTranslation`() {
        // given
        val type = ExpressionType.WORD
        val deckId = 1L

        val expression1 = mockRepository.addExpression("spring", type, langOriginal())
        val expression2 = mockRepository.addExpression("источник", type, langTranslations())

        val card = mockRepository.addCard(deckId, expression1, listOf(expression2))
        mockRepository.addCard(deckId, expression2, listOf(expression1))

        val mockRegistry = DecksRegistry(context, mockPrefs, mockRepository)

        // when
        val edited = mockRegistry.editCard(card,
                CardData("spring", langOriginal(), listOf("источник", "весна"), langTranslations(), deckId, type))

        // then
        assertNotNull("edit was successful", edited)
        assertEquals("correct card was edited", card.id, edited!!.id)
        assertEquals("expressions are reused", expression1, edited.original)
        assertEquals("translations are updated", listOf(expression2,
                mockRepository.expressionByValues("весна", type, langTranslations())), edited.translations)
    }

    @Test
    fun `editCard__nonExistentCard`() {
        // given
        val type = ExpressionType.WORD
        val deckId = 1L

        val expression1 = mockRepository.addExpression("spring", type, langOriginal())
        val expression2 = mockRepository.addExpression("источник", type, langTranslations())

        mockRepository.addCard(deckId, expression2, listOf(expression1))

        val mockRegistry = DecksRegistry(context, mockPrefs, mockRepository)

        val card = Card(77L, deckId, expression1, listOf(expression2), emptyState())

        // when
        val edited = mockRegistry.editCard(card,
                CardData("spring", langOriginal(), listOf("источник", "весна"), langTranslations(), deckId, type))

        // then
        assertNull("card was not found", edited)
    }

    @Test
    fun `deleteCard`() {
        // given
        val type = ExpressionType.WORD
        val deckId = 1L

        val expression1 = mockRepository.addExpression("spring", type, langOriginal())
        val expression2 = mockRepository.addExpression("весна", type, langTranslations())
        val expression3 = mockRepository.addExpression("источник", type, langTranslations())

        val card = mockRepository.addCard(deckId, expression1, listOf(expression2, expression3))
        val card2 = mockRepository.addCard(deckId, expression3, listOf(expression1))

        val mockRegistry = DecksRegistry(context, mockPrefs, mockRepository)

        // when
        mockRegistry.deleteCard(card)

        // then
        assertNull("card is deleted", mockRepository.cardById(card.id))
        assertNull("orphan expressions are deleted", mockRepository.expressionById(expression2.id))

        assertNotNull("other cards are preserved", mockRepository.cardById(card2.id))
        assertNotNull("used expressions are preserved", mockRepository.expressionById(expression1.id))
        assertNotNull("used expressions are preserved", mockRepository.expressionById(expression3.id))
    }
}

private fun verifyAddedCardsCorrect(cardsData: ArrayList<CardData>, cardsOfDeck: List<Card>) {
    val forwardCount = cardsData.size
    val reverseCount = cardsData.sumBy { it.translations.size }
    val expectedCount = forwardCount + reverseCount

    assertEquals("amount of cards is correct", expectedCount, cardsOfDeck.size)
    assertEquals("amount of forward cards is correct", forwardCount, cardsOfDeck.count { it.type == CardType.FORWARD })
    assertEquals("amount of reverse cards is correct", reverseCount, cardsOfDeck.count { it.type == CardType.REVERSE })

    for (data in cardsData) {
        val forward = cardsOfDeck.find { it.type == CardType.FORWARD && it.original.value == data.original }
        assertCardCorrect(forward, data)

        val reversedCards = reversedTo(data)
        for (reversedData in reversedCards) {
            val reverse = cardsOfDeck.find { it.type == CardType.REVERSE && it.original.value == reversedData.original }
            assertCardCorrect(reverse, reversedData)
        }
    }
}