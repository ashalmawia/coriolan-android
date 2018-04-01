package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.learning.scheduler.sr.emptyState
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

    private val exercise = "mock"

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

        // when
        cache.invalidateCache()
        val cached2 = cache.expressionById(added.id)

        // then
        assertNotNull("expression is found", cached2)
        assertExpressionCorrect(cached2, value, type, lang)
        // expressionById() must be called as the cache was cleared
        verify(inner, times(1)).expressionById(any())
    }

    @Test
    fun `test__expressionById`() {
        // given
        val lang = mockLanguage(value = "Russian")
        val expression = mockExpression(language = lang)
        inner.expressions.add(expression)
        cache.expressionById(expression.id)     // caching is expected to happen here

        // when
        cache.expressionById(expression.id)
        val cached = cache.expressionById(expression.id)

        // then
        assertNotNull("expression was found", cached)
        assertExpressionCorrect(cached, expression.value, expression.type, lang)
        // expressionById() must be called once during the first call,
        // as after that the value is expected to be kept in the cache
        verify(inner, times(1)).expressionById(any())

        // when
        cache.invalidateCache()
        val cached2 = cache.expressionById(expression.id)

        // then
        assertNotNull("expression is found", cached2)
        assertExpressionCorrect(cached, expression.value, expression.type, lang)
        // expressionById() must be called as the cache was cleared
        verify(inner, times(2)).expressionById(any())
    }

    @Test
    fun `test__addCard`() {
        // given
        val domain = mockDomain()
        val data = mockCardData("shrimp", "креветка")

        // when
        val card = addMockCard(cache, data, domain)

        // then
        assertNotNull("card is added", card)
        assertCardCorrect(card, data, domain)

        // when
        val cached = cache.cardById(card.id, domain)

        // then
        assertNotNull("card is found", cached)
        assertCardCorrect(cached, data, domain)
        // cardById() must not be called as the value is expected to be cached while adding
        verify(inner, never()).cardById(any(), any())

        // when
        cache.invalidateCache()
        val cached2 = cache.cardById(card.id, domain)

        // then
        assertNotNull("expression is found", cached2)
        assertCardCorrect(cached2, data, domain)
        // cardById() must be called as the cache was cleared
        verify(inner, times(1)).cardById(any(), any())
    }

    @Test
    fun `test__addDeck`() {
        // given
        val name = "My deck"
        val domain = mockDomain()

        // when
        val deck = cache.addDeck(domain, name)

        // then
        assertNotNull("deck is added", deck)
        assertDeckCorrect(deck, name, domain)

        // when
        val cached = cache.deckById(deck.id, domain)
        cache.deckById(deck.id, domain)
        cache.deckById(deck.id, domain)

        // then
        assertNotNull("deck is found", cached)
        assertDeckCorrect(cached, name, domain)
        // deckById() must not be called as the value is expected to be cached while adding
        verify(inner, times(1)).allDecks(any())

        // when
        cache.invalidateCache()
        val cached2 = cache.deckById(deck.id, domain)
        cache.deckById(deck.id, domain)
        cache.deckById(deck.id, domain)

        // then
        assertNotNull("expression is found", cached2)
        assertDeckCorrect(cached2, name, domain)
        // deckById() must be called as the cache was cleared
        verify(inner, times(2)).allDecks(any())
    }

    @Test
    fun `test__deckById`() {
        // given
        val deck = mockDeck()
        val domain = deck.domain

        inner.decks.add(deck)
        cache.deckById(deck.id, domain)     // caching is expected to happen here

        // when
        val cached = cache.deckById(deck.id, domain)
        cache.deckById(deck.id, domain)

        // then
        assertNotNull("deck is found", cached)
        assertDeckCorrect(cached, deck.name, domain)
        // allDecks() must be called once during the first call,
        // as after that the value is expected to be kept in the cache
        verify(inner, times(1)).allDecks(any())

        // when
        cache.invalidateCache()
        val cached2 = cache.deckById(deck.id, domain)
        cache.deckById(deck.id, domain)

        // then
        assertNotNull("expression is found", cached2)
        assertDeckCorrect(cached2, deck.name, domain)
        // allDecks() must be called as the cache was cleared
        verify(inner, times(2)).allDecks(any())
    }

    @Test
    fun `test__allDecks`() {
        // given
        val domain = mockDomain()
        val decks = mutableListOf<Deck>()
        for (i in 0 until 5) {
            decks.add(mockDeck(domain = domain))
        }
        inner.decks.addAll(decks)
        cache.allDecks(domain)        // caching is expected to happen here

        // when
        val cached = cache.allDecks(domain)

        // then
        assertEquals("number of decks is correct", decks.size, cached.size)
        for (i in 0 until decks.size) {
            assertDeckCorrect(cached[i], decks[i].name, domain)
        }
        // allDecks() must be called once during the first call,
        // as after that the value is expected to be kept in the cache
        verify(inner, times(1)).allDecks(domain)

        // when
        cache.invalidateCache()
        val cached2 = cache.allDecks(domain)
        cache.allDecks(domain)

        // then
        assertEquals("number of decks is correct", decks.size, cached2.size)
        for (i in 0 until decks.size) {
            assertDeckCorrect(cached2[i], decks[i].name, domain)
        }
        // allDecks() must be called as the cache was cleared
        verify(inner, times(2)).allDecks(any())
    }

    @Test
    fun `test__updateCardState__stateIsPassed`() {
        // given
        val card = addMockCard(cache)
        val state = mockState()

        // when
        cache.updateSRCardState(card, state, exercise)

        // then
        assertEquals("state is updated", state, inner.states[card.id])
    }

    @Test
    fun `test__allCards`() {
        // given
        val domain = mockDomain()
        val cards = (0 until 9).map { addMockCard(cache, original = "original $it", translations = listOf("tr $it"), domain = domain) }

        // when
        val read = cache.allCards(domain)

        // then
        assertEquals(cards, read)
        verify(inner, times(1)).allCards(any())

        // when
        val read2 = cache.allCards(domain)

        // then
        assertEquals(cards, read2)
        verify(inner, times(1)).allCards(any())

        // when
        val cardById = cache.cardById(cards[1].id, domain)

        // then
        assertEquals(cards[1], cardById)
        verify(inner, never()).cardById(any(), any())

        // when
        val mutable = cards.toMutableList()
        val card = addMockCard(cache, original = "original X", translations = listOf("translation X1", "translation X2"), domain = domain)
        mutable.add(card)
        cache.deleteCard(mutable[5])
        mutable.removeAt(5)
        val updated = cache.updateCard(mutable[7], 8L, mockExpression("some word"), listOf(mockExpression("translation")))
        assertNotNull(updated)
        mutable[7] = updated!!
        val readUpdated = cache.cardById(updated.id, domain)
        val all = cache.allCards(domain)

        // then
        assertEquals(updated, readUpdated)
        assertEquals(mutable, all)
        verify(inner, never()).cardById(any(), any())
        verify(inner, times(1)).allCards(any())

        // when
        cache.invalidateCache()
        cache.allCards(domain)

        // then
        verify(inner, times(2)).allCards(any())
    }

    @Test
    fun `test__cardsDueDate`() {
        // given
        val domain = mockDomain()
        val deck = cache.addDeck(domain, "name")
        val card = addMockCard(cache, mockCardData())

        // when
        val due = cache.cardsDueDate(exercise, deck, today())

        // then
        assertEquals("number of due cards is correct", 1, due.size)
        assertTrue("the returned copy is the one from cache", due[0].card == card)
        assertTrue("the returned copy is the one from cache", due[0].state == emptyState())
    }
}