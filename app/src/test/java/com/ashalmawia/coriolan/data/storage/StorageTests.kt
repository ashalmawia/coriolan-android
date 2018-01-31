package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.exercise.MockExercise
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.learning.scheduler.Status
import com.ashalmawia.coriolan.learning.scheduler.today
import com.ashalmawia.coriolan.model.*
import com.ashalmawia.coriolan.util.addDays
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

abstract class StorageTest {

    private lateinit var storage: Repository

    private val exercise = MockExercise()

    protected abstract fun createStorage(exercises: List<Exercise>): Repository

    @Before
    fun before() {
        val exercises = listOf(exercise)
        storage = createStorage(exercises)
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
        assertEquals("new deck is empty", 0, deck.cards().size)
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
        assertEquals("deck is empty", 0, deck!!.cards().size)
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
        assertNull("deck not found", deck)
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
        assertEquals("decks number is correct", decks.size, allDecks.size)
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
        assertEquals("decks number is correct", decks.size, allDecks.size)
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
        assertEquals("no decks found", 0, allDecks.size)
    }

    @Test
    fun `test__updateCardState`() {
        // when
        val card = storage.addCard(mockCardData())

        // then
        assertEquals("state is correct", Status.NEW, card.state.status)
//        assertEquals("new card is due today", today(), card.state.due)            todo: uncomment

        // given
        val newState = State(today().addDays(8), 8)

        // when
        val secondRead = storage.updateCardState(card, newState, exercise)

        // then
        assertEquals("state is correct", newState.status, secondRead.state.status)
//        assertEquals("new card is due today", newState.due, secondRead.state.due)     todo: uncomment
    }

    @Test
    fun `test__cardsDueDate__newCards`() {
        // given
        val deck = storage.addDeck("mock deck")
        val count = 3
        val cardData = mutableListOf<CardData>()
        for (i in 0 until count) {
            val data = mockCardData("original $i", "translation $i", deck.id)
            cardData.add(data)
            storage.addCard(data)
        }

        // when
        val due = storage.cardsDueDate(exercise, deck, today())

        // then
        assertEquals("all new cards are due today", count, due.size)
        for (i in 0 until count) {
            assertCardCorrect(due[i], cardData[i])
//            assertEquals("state is correct", today(), due[i].state.due)   TODO: uncomment
            assertEquals("state is correct", Status.NEW, due[i].state.status)
        }
    }

    @Test
    fun `test__cardsDueDate__cardsInProgress`() {
        // given
        val deck = storage.addDeck("mock deck")
        val count = 3
        val cardsData = mutableListOf<CardData>()
        val cards = mutableListOf<Card>()
        for (i in 0 until count) {
            val data = mockCardData("original $i", "translation $i", deck.id)
            cardsData.add(data)
            val added = storage.addCard(data)
            cards.add(added)
        }
        val today = today()

        storage.updateCardState(cards[0], State(today, 4), exercise)
        storage.updateCardState(cards[1], State(today.addDays(1), 4), exercise)
        storage.updateCardState(cards[2], State(today.addDays(-1), 4), exercise)

        // when
        val due = storage.cardsDueDate(exercise, deck, today)

        // then
        assertEquals(2, due.size)
        assertCardCorrect(due[0], cardsData[0])
        assertCardCorrect(due[1], cardsData[2])
    }

    @Test
    fun `test__cardsDueDate__noPendingCards`() {
        // given
        val deck = storage.addDeck("mock deck")
        val count = 3
        val cards = (0 until count)
                .map { mockCardData("original $it", "translation $it", deck.id) }
                .map { storage.addCard(it) }
        val today = today()

        storage.updateCardState(cards[0], State(today.addDays(3), 4), exercise)
        storage.updateCardState(cards[1], State(today.addDays(1), 4), exercise)
        storage.updateCardState(cards[2], State(today.addDays(10), 4), exercise)

        // when
        val due = storage.cardsDueDate(exercise, deck, today)

        // then
        assertEquals(0, due.size)
    }
}