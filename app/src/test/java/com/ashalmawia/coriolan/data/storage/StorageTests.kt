package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.model.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test

abstract class StorageTest {

    private lateinit var storage: Storage

    protected abstract fun createStorage(): Storage

    @Before
    fun before() {
        storage = createStorage()
    }

    @Test
    fun `test__addExpression__Word`() {
        // given
        val value = "shrimp"
        val type = ExpressionType.WORD

        // when
        val expression = storage.addExpression(value, type)

        // then
        assertExpressionCorrect(expression, value, type)
    }

    @Test
    fun `test__addExpression__Sentence`() {
        // given
        val value = "Shrimp is going out on Fridays."
        val type = ExpressionType.WORD

        // when
        val expression = storage.addExpression(value, type)

        // then
        assertExpressionCorrect(expression, value, type)
    }

    @Test
    fun `test__expressionById__Word`() {
        // given
        val value = "shrimp"
        val type = ExpressionType.WORD
        val id = storage.addExpression(value, type).id

        // when
        val expression = storage.expressionById(id)

        // then
        assertExpressionCorrect(expression, value, type)
    }

    @Test
    fun `test__expressionById__Sentence`() {
        // given
        val value = "Shrimp is going out on Fridays."
        val type = ExpressionType.WORD
        val id = storage.addExpression(value, type).id

        // when
        val expression = storage.expressionById(id)

        // then
        assertExpressionCorrect(expression, value, type)
    }

    @Test
    fun `test__addCard__Word__SingleTranslation`() {
        // given
        val data = mockCardData("shrimp", "креветка")

        // when
        val card = storage.addCard(data)

        // then
        assertCardCorrect(card, data)
    }

    @Test
    fun `test__addCard__Word__MultipleTranslations`() {
        // given
        val data = mockCardData("ракета", listOf("firework", "rocket", "missile"))

        // when
        val card = storage.addCard(data)

        // then
        assertCardCorrect(card, data)
    }

    @Test
    fun `test__addCard__Sentence`() {
        // given
        val data = mockCardData("Shrimp is going out on Fridays.", "Креветка гуляет по пятницам.")

        // when
        val card = storage.addCard(data)

        // then
        assertCardCorrect(card, data)
    }

    @Test
    fun `test__addDeck`() {     // TODO: add test for name being unique
        // given
        val name = "My new deck"

        // when
        val deck = storage.addDeck(name)

        // assert
        assertDeckCorrect(deck, name)
        Assert.assertEquals("new deck is empty", 0, deck.cards().size)
    }

    @Test
    fun `test__deckById__NoCards`() {
        // given
        val name = "EN - My deck"
        val id = storage.addDeck(name).id
        storage.addDeck("wrong deck 1")
        storage.addDeck("wrong deck 2")

        // when
        val deck = storage.deckById(id)

        // assert
        assertDeckCorrect(deck, name)
        Assert.assertEquals("deck is empty", 0, deck!!.cards().size)
    }

    @Test
    fun `test__deckById__HasCards`() {
        // given
        val name = "EN - My deck"
        storage.addDeck("wrong deck 1")
        val id = storage.addDeck(name).id
        storage.addDeck("wrong deck 2")
        val cards = listOf(
                mockCardData("shrimp", "креветка", id),
                mockCardData("ракета", listOf("rocket", "missile", "firework"), id),
                mockCardData("Shrimp is going out on Fridays.", "Креветка гуляет по пятницам.", id)
        )
        for (card in cards) {
            storage.addCard(card)
        }

        // when
        val deck = storage.deckById(id)

        // assert
        assertDeckCorrect(deck, name, cards)
    }

    @Test
    fun `test__deckById__DoesNotExist`() {
        // given
        val id = 100L
        storage.addDeck("wrong deck 1")
        storage.addDeck("wrong deck 2")

        // when
        val deck = storage.deckById(id)

        // assert
        Assert.assertNull("deck not found", deck)
    }

    @Test
    fun `test__allDecks__DecksAreEmpty`() {
        // given
        val decks = mutableListOf<Deck>()
        for (i in 0 until 3) {
            decks.add(storage.addDeck("deck $1"))
        }

        // when
        val allDecks = storage.allDecks()

        // then
        Assert.assertEquals("decks number is correct", decks.size, allDecks.size)
        for (i in 0 until decks.size) {
            assertDeckCorrect(allDecks[i], decks[i].name)
        }
    }

    @Test
    fun `test__allDecks__DecksAreNonEmpty`() {
        // given
        val decks = mutableListOf<Deck>()
        val cardData = mutableListOf<List<CardData>>()
        for (i in 0 until 3) {
            val deck = storage.addDeck("deck $i")
            decks.add(deck)
            cardData.add(listOf(
                    mockCardData("original $i", "translation $i", deck.id)
            ))
        }
        cardData.forEach { it.forEach { storage.addCard(it) } }

        // when
        val allDecks = storage.allDecks()

        // then
        Assert.assertEquals("decks number is correct", decks.size, allDecks.size)
        for (i in 0 until decks.size) {
            assertDeckCorrect(allDecks[i], decks[i].name, cardData[i])
        }
    }

    @Test
    fun `test__allDecks__NoDecks`() {
        // given

        // when
        val allDecks = storage.allDecks()

        // then
        Assert.assertEquals("no decks found", 0, allDecks.size)
    }
}