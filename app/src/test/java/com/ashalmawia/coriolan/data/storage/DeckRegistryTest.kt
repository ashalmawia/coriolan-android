package com.ashalmawia.coriolan.data.storage

import android.content.Context
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.data.importer.reversedTo
import com.ashalmawia.coriolan.data.prefs.MockPreferences
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.model.*
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DeckRegistryTest {

    @Test
    fun preinitializeTestNoDefaultDeck() {
        // given
        val defaultDeck = mockDeck()

        val context = mock(Context::class.java)
        `when`(context.getString(anyInt())).thenReturn(defaultDeck.name)

        val mockStorage = mock(Repository::class.java)
        `when`(mockStorage.addDeck(any())).thenReturn(defaultDeck)

        val mockPrefs = MockPreferences()

        // when
        val registry = DecksRegistry(context, mockPrefs, mockStorage)

        // then
        verify(mockStorage).addDeck(any())
        assertEquals("Default deck is initialized correctly", defaultDeck, registry.default())
    }

    @Test
    fun preinitializeTestHasDefaultDeck() {
        // given
        val defaultDeck = mockDeck()

        val context = mock(Context::class.java)

        val mockStorage = mock(Repository::class.java)
        `when`(mockStorage.deckById(anyLong())).thenReturn(defaultDeck)

        val mockPrefs = mock(Preferences::class.java)
        `when`(mockPrefs.getDefaultDeckId()).thenReturn(defaultDeck.id)

        // when
        val registry = DecksRegistry(context, mockPrefs, mockStorage)

        // then
        verify(mockStorage).deckById(anyLong())
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

        val mockPrefs = MockPreferences()
        val mockRepository = MockRepository()
        val context = mock(Context::class.java)
        `when`(context.getString(anyInt())).thenReturn("Default")

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

        val mockPrefs = MockPreferences()
        val mockRepository = MockRepository()

        val context = mock(Context::class.java)
        `when`(context.getString(anyInt())).thenReturn("Default")

        val mockRegistry = DecksRegistry(context, mockPrefs, mockRepository)

        // when
        mockRegistry.addCardsToDeck(cardsData)

        // then
        val cardsOfDeck = mockRepository.cardsOfDeck(deck)
        verifyAddedCardsCorrect(cardsData, cardsOfDeck)
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