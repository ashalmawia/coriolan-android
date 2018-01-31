package com.ashalmawia.coriolan.data.storage

import android.content.Context
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.model.Deck
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
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

        val mockPrefs = mock(Preferences::class.java)
        `when`(mockPrefs.getDefaultDeckId()).thenReturn(null)

        val registry = mock(DecksRegistry::class.java)
        `when`(registry.preferences(any())).thenReturn(mockPrefs)
        `when`(registry.storage(any())).thenReturn(mockStorage)
        `when`(registry.preinitialize(any())).thenCallRealMethod()
        `when`(registry.default()).thenCallRealMethod()

        // when
        registry.preinitialize(context)

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

        val registry = mock(DecksRegistry::class.java)
        `when`(registry.preferences(any())).thenReturn(mockPrefs)
        `when`(registry.storage(any())).thenReturn(mockStorage)
        `when`(registry.preinitialize(any())).thenCallRealMethod()
        `when`(registry.default()).thenCallRealMethod()

        // when
        registry.preinitialize(context)

        // then
        verify(mockStorage).deckById(anyLong())
        assertEquals("Default deck is set correctly", defaultDeck, registry.default())
    }

//    @Test
//    fun addCardsToDeckTest() {
//        // given
//        val deck = mockDeck()
//        val cardsData = arrayListOf(
//                CardData("original", "translation", deck.id, ExpressionType.WORD),
//                CardData("original2", "translation2", deck.id, ExpressionType.UNKNOWN)
//        )
//
//        val context = mock(Context::class.java)
//
//        val mockStorage = object : Storage {
//
//            var called = 0
//            override fun addCard(data: CardData): Card {
//                called++
//                return Card.create(
//                        111L,
//                        Expression(1L, data.original, data.type),
//                        Expression(2L, data.translation, data.type))
//            }
//
//            override fun addExpression(value: String, type: ExpressionType): Expression { throw UnsupportedOperationException() }
//            override fun expressionById(id: Long): Expression? { throw UnsupportedOperationException() }
//            override fun allDecks(): List<Deck> { throw UnsupportedOperationException() }
//            override fun deckById(id: Long): Deck? { throw UnsupportedOperationException() }
//            override fun addDeck(name: String): Deck { throw UnsupportedOperationException() }
//        }
//
//        val mockRegistry = mock(DecksRegistry::class.java)
//        `when`(mockRegistry.storage(any())).thenThrow(IllegalAccessError())
//        `when`(mockRegistry.addCardsToDeck(any(), any(), anyList())).thenCallRealMethod()
//
//        // when
//        mockRegistry.addCardsToDeck(context, deck, cardsData)
//
//        // then
//        assertEquals("addCard() was called correct amount of times", cardsData.size, mockStorage.called)
//        assertEquals("amount of cards is correct", cardsData.size, deck.cards().size)
//        for (i in 0 until cardsData.size) {
//            assertEquals("cards data is correct", cardsData[i].original, deck.cards()[i].original.value)
//            assertEquals("cards data is correct", cardsData[i].translation, deck.cards()[i].translations[i].value)
//        }
//    }

    private fun mockDeck() = Deck(111L, "Default", arrayListOf())
}