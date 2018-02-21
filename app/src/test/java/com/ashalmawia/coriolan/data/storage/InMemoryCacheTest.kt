package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.learning.exercise.MockExercise
import com.ashalmawia.coriolan.learning.scheduler.today
import com.ashalmawia.coriolan.model.*
import com.nhaarman.mockito_kotlin.*
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class InMemoryCacheTest {

    private lateinit var inner: MockRepository
    private lateinit var cache: InMemoryCache

    private val exercise = MockExercise()

    @Before
    fun before() {
        inner = spy(MockRepository())
        cache = InMemoryCache(inner)
    }

    @Test
    fun `test__addExpression`() {
        // given
        val value = "shrimp"
        val type = ExpressionType.WORD
        val lang = mockLanguage()

        // when
        val added = cache.addExpression(value, type, lang)

        // then
        assertEquals("expression was added", 1, inner.expressions.size)
        assertExpressionCorrect(inner.expressions[0], value, type, lang)

        // when
        val cached = cache.expressionById(added.id)

        // then
        assertNotNull("expression is found", cached)
        assertExpressionCorrect(cached, value, type, lang)
        // expressionById() must not be called as the value is expected to be cached while adding
        verify(inner, never()).expressionById(any())
    }

    @Test
    fun `test__expressionById`() {
        // given
        val lang = mockLanguage(value = "Russian")
        val expression = mockExpression(language = lang)
        inner.expressions.add(expression)
        cache.expressionById(expression.id)     // caching is expected to happen here

        // when
        val cached = cache.expressionById(expression.id)

        // then
        assertNotNull("expression was found", cached)
        assertExpressionCorrect(cached, expression.value, expression.type, lang)
        // expressionById() must be called once during the first call,
        // as after that the value is expected to be kept in the cache
        verify(inner, times(1)).expressionById(any())
    }

    @Test
    fun `test__addCard`() {
        // given
        val data = mockCardData("shrimp", "креветка")

        // when
        val card = addMockCard(cache, data)

        // then
        assertNotNull("card is added", card)
        assertCardCorrect(card, data)

        // when
        val cached = cache.cardById(card.id)

        // then
        assertNotNull("card is found", cached)
        assertCardCorrect(cached, data)
        // cardById() must not be called as the value is expected to be cached while adding
        verify(inner, never()).cardById(any())
    }

    @Test
    fun `test__addDeck`() {
        // given
        val name = "My deck"

        // when
        val deck = cache.addDeck(name)

        // then
        assertNotNull("deck is added", deck)
        assertDeckCorrect(deck, name)

        // when
        val cached = cache.deckById(deck.id)

        // then
        assertNotNull("deck is found", cached)
        assertDeckCorrect(cached, name)
        // expressionById() must not be called as the value is expected to be cached while adding
        verify(inner, never()).deckById(deck.id)
    }

    @Test
    fun `test__deckById`() {
        // given
        val deck = mockDeck()
        inner.decks.add(deck)
        cache.deckById(deck.id)     // caching is expected to happen here

        // when
        val cached = cache.deckById(deck.id)

        // then
        assertNotNull("deck is found", cached)
        assertDeckCorrect(cached, deck.name)
        // deckById() must be called once during the first call,
        // as after that the value is expected to be kept in the cache
        verify(inner, times(1)).allDecks()
    }

    @Test
    fun `test__allDecks`() {
        // given
        val decks = mutableListOf<Deck>()
        for (i in 0 until 5) {
            decks.add(mockDeck())
        }
        inner.decks.addAll(decks)
        cache.allDecks()        // caching is expected to happen here

        // when
        val cached = cache.allDecks()

        // then
        assertEquals("number of decks is correct", decks.size, cached.size)
        for (i in 0 until decks.size) {
            assertDeckCorrect(cached[i], decks[i].name)
        }
        // allDecks() must be called once during the first call,
        // as after that the value is expected to be kept in the cache
        verify(inner, times(1)).allDecks()
    }

    @Test
    fun `test__updateCardState__stateIsPassed`() {
        // given
        val card = mockCard()
        val state = mockState()

        // when
        cache.updateCardState(card, state, exercise)

        // then
        assertEquals("state is updated", state, inner.states[card.id])
    }

    @Test
    fun `test__updateCardState__updatedStateIsCachedCorrectly`() {
        // given
        val cardData = mockCardData()

        val card = addMockCard(cache, cardData)

        val state = mockState()

        // when
        cache.updateCardState(card, state, exercise)
        val cached = cache.cardById(card.id)

        // then
        assertEquals("state is updated", state, cached!!.state)
        verify(inner, never()).cardById(any())
    }

    @Test
    fun `test__cardsDueDate`() {
        // given
        val deck = cache.addDeck("name")
        val card = addMockCard(cache, mockCardData())

        // when
        val due = cache.cardsDueDate(exercise, deck, today())

        // then
        assertEquals("number of due cards is correct", 1, due.size)
        assertTrue("the returned copy is the one from cache", due[0] == card)
    }
}