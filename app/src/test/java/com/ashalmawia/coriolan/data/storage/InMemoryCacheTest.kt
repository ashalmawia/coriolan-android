package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.model.*
import com.nhaarman.mockito_kotlin.*
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class InMemoryCacheTest {

    private lateinit var inner: MockStorage
    private lateinit var cache: InMemoryCache

    @Before
    fun before() {
        inner = spy(MockStorage())
        cache = InMemoryCache(inner)
    }

    @Test
    fun `test__addExpression`() {
        // given
        val value = "shrimp"
        val type = ExpressionType.WORD

        // when
        val added = cache.addExpression(value, type)

        // then
        assertEquals("expression was added", 1, inner.expressions.size)
        assertExpressionCorrect(inner.expressions[0], value, type)

        // when
        val cached = cache.expressionById(added.id)

        // then
        assertNotNull("expression is found", cached)
        assertExpressionCorrect(cached, value, type)
        // expressionById() must not be called as the value is expected to be cached while adding
        verify(inner, never()).expressionById(any())
    }

    @Test
    fun `test__expressionById`() {
        // given
        val expression = mockExpression()
        inner.expressions.add(expression)
        cache.expressionById(expression.id)     // caching is expected to happen here

        // when
        val cached = cache.expressionById(expression.id)

        // then
        assertNotNull("expression was found", cached)
        assertExpressionCorrect(cached, expression.value, expression.type)
        // expressionById() must be called once during the first call,
        // as after that the value is expected to be kept in the cache
        verify(inner, times(1)).expressionById(any())
    }

    @Test
    fun `test__addCard`() {
        // given
        val data = mockCardData("shrimp", "креветка")

        // when
        val card = cache.addCard(data)

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
        verify(cache, times(1)).allDecks()
    }
}

private class MockStorage : Repository {
    val expressions = mutableListOf<Expression>()
    override fun addExpression(value: String, type: ExpressionType): Expression {
        val exp = Expression(expressions.size + 1L, value, type)
        expressions.add(exp)
        return exp
    }
    override fun expressionById(id: Long): Expression? {
        return expressions.find { it.id == id }
    }

    val cards = mutableListOf<Card>()
    override fun addCard(data: CardData): Card {
        val card = Card.create(
                cards.size + 1L,
                addExpression(data.original, data.type),
                data.translations.map { addExpression(it, data.type) }
        )
        cards.add(card)
        decks.find { it.id == data.deckId }?.add(cards)
        return card
    }
    override fun cardById(id: Long): Card? {
        return cards.find { it.id == id }
    }

    val decks = mutableListOf<Deck>()
    override fun allDecks(): List<Deck> {
        return decks
    }
    override fun deckById(id: Long): Deck? {
        return decks.find { it.id == id }
    }
    override fun addDeck(name: String): Deck {
        val deck = Deck(decks.size + 1L, name, listOf())
        decks.add(deck)
        return deck
    }
}