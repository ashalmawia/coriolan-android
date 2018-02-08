package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.LanguagesRegistry
import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.exercise.MockExercise
import com.ashalmawia.coriolan.learning.scheduler.PERIOD_LEARNT
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.learning.scheduler.Status
import com.ashalmawia.coriolan.learning.scheduler.today
import com.ashalmawia.coriolan.model.*
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
    fun `test__addLanguage`() {
        // given
        val value = "Russian"

        // when
        val language = storage.addLanguage(value)

        // then
        assertLanguageCorrect(language, value)
    }

    @Test
    fun `test__languageById__languageExists`() {
        // given
        val value = "Russian"

        storage.addLanguage("Some language")
        val language = storage.addLanguage(value)
        storage.addLanguage("Other language")

        // when
        val read = storage.languageById(language.id)

        // then
        assertLanguageCorrect(read, language.value)
        assertEquals("language id is correct", language.id, read!!.id)
    }

    @Test
    fun `test__languageById__languageDoesNotExist`() {
        // given

        // when
        val read = storage.languageById(777L)

        // then
        assertNull(read)
    }

    @Test
    fun `test__addExpression__Word`() {
        // given
        val value = "shrimp"
        val type = ExpressionType.WORD
        val lang = mockLanguage(value = "Russian")

        // when
        val expression = storage.addExpression(value, type, lang)

        // then
        assertExpressionCorrect(expression, value, type, lang)
    }

    @Test
    fun `test__addExpression__Sentence`() {
        // given
        val value = "Shrimp is going out on Fridays."
        val type = ExpressionType.WORD
        val lang = mockLanguage(value = "Russian")

        // when
        val expression = storage.addExpression(value, type, lang)

        // then
        assertExpressionCorrect(expression, value, type, lang)
    }

    @Test
    fun `test__expressionById__Word`() {
        // given
        val lang = storage.addLanguage("Russian")

        val value = "shrimp"
        val type = ExpressionType.WORD
        val id = storage.addExpression(value, type, lang).id

        // when
        val expression = storage.expressionById(id)

        // then
        assertExpressionCorrect(expression, value, type, lang)
    }

    @Test
    fun `test__expressionById__Sentence`() {
        // given
        val lang = storage.addLanguage("Russian")

        val value = "Shrimp is going out on Fridays."
        val type = ExpressionType.WORD
        val id = storage.addExpression(value, type, lang).id

        // when
        val expression = storage.expressionById(id)

        // then
        assertExpressionCorrect(expression, value, type, lang)
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
    }

    @Test
    fun `test__deckById__HasCards`() {
        // given
        addMockLanguages(storage)

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
        val deck = storage.deckById(id)!!

        // assert
        assertDeckCorrect(deck, name)
        assertDeckCardsCorrect(storage.cardsOfDeck(deck), cards)
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
    fun `test__cardsOfDeck__NewDeck`() {
        // given

        // when
        val deck = storage.addDeck("Mock deck")
        val cards = storage.cardsOfDeck(deck)

        // assert
        assertEquals("new deck is empty", 0, cards.size)
    }

    @Test
    fun `test__cardsOfDeck__EmptyDeck`() {
        // given
        val deck = storage.addDeck("Mock deck")
        val wrongDeck1 = storage.addDeck("wrong deck 1")
        for (i in 0 until 3) {
            storage.addCard(mockCardData(deckId = wrongDeck1.id))
        }
        val wrongDeck2 = storage.addDeck("wrong deck 2")
        for (i in 0 until 3) {
            storage.addCard(mockCardData(deckId = wrongDeck2.id))
        }

        // when
        val cards = storage.cardsOfDeck(deck)

        // assert
        assertEquals("empty deck has no cards", 0, cards.size)
    }

    @Test
    fun `test__cardsOfDeck__NonEmptyDeck`() {
        // given
        addMockLanguages(storage)

        val decks = mutableListOf<Deck>()
        val cardData = mutableListOf<List<CardData>>()
        for (i in 0 until 3) {
            val deck = storage.addDeck("deck $i")
            decks.add(deck)
            cardData.add(listOf(
                    mockCardData("original ${i*i}", "translation ${i*i}", deck.id),
                    mockCardData("original ${i*i+1}", "translation ${i*i+1}", deck.id)
            ))
        }
        cardData.forEach { it.forEach { storage.addCard(it) } }

        for (i in 0 until decks.size) {
            // when
            val cards = storage.cardsOfDeck(decks[i])

            // assert
            assertDeckCardsCorrect(cards, cardData[i])
        }
    }

    @Test
    fun `test__cardsOfDeck__NonEmptyDeck__differentCount`() {
        // given
        addMockLanguages(storage)

        val decks = mutableListOf<Deck>()
        val cardData = mutableListOf<List<CardData>>()
        val deck1 = storage.addDeck("deck 1")
        decks.add(deck1)
        cardData.add(listOf(
                mockCardData("original $1", "translation $1", deck1.id),
                mockCardData("original $2", "translation $2", deck1.id)
        ))
        val deck2 = storage.addDeck("deck 2")
        decks.add(deck2)
        cardData.add(listOf())
        val deck3 = storage.addDeck("deck 3")
        decks.add(deck3)
        cardData.add(listOf(
                mockCardData("original $4", "translation 4", deck3.id),
                mockCardData("original 5", "translation 5", deck3.id)
        ))
        cardData.forEach { it.forEach { storage.addCard(it) } }

        for (i in 0 until decks.size) {
            // when
            val cards = storage.cardsOfDeck(decks[i])

            // assert
            assertDeckCardsCorrect(cards, cardData[i])
        }
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
        addMockLanguages(storage)

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
            assertDeckCorrect(allDecks[i], decks[i].name)
            assertDeckCardsCorrect(storage.cardsOfDeck(decks[i]), cardData[i])
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
        assertEquals("new card is due today", today(), card.state.due)

        // given
        val newState = State(today().plusDays(8), 8)

        // when
        val secondRead = storage.updateCardState(card, newState, exercise)

        // then
        assertEquals("state is correct", newState.status, secondRead.state.status)
        assertEquals("new card is due today", newState.due, secondRead.state.due)
    }

    @Test
    fun `test__cardsDueDate__newCards`() {
        // given
        addMockLanguages(storage)

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
            assertEquals("state is correct", today(), due[i].state.due)
            assertEquals("state is correct", Status.NEW, due[i].state.status)
        }
    }

    @Test
    fun `test__cardsDueDate__cardsInProgress`() {
        // given
        addMockLanguages(storage)

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
        storage.updateCardState(cards[1], State(today.plusDays(1), 4), exercise)
        storage.updateCardState(cards[2], State(today.minusDays(1), 4), exercise)

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

        storage.updateCardState(cards[0], State(today.plusDays(3), 4), exercise)
        storage.updateCardState(cards[1], State(today.plusDays(1), 4), exercise)
        storage.updateCardState(cards[2], State(today.plusDays(10), 4), exercise)

        // when
        val due = storage.cardsDueDate(exercise, deck, today)

        // then
        assertEquals(0, due.size)
    }

    @Test
    fun `test__cardsDueDateCount__allNewCards`() {
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
        val counts = storage.cardsDueDateCount(exercise, deck, today())

        // then
        assertEquals(count, counts.countNew())
        assertEquals(0, counts.countReview())
        assertEquals(0, counts.countRelearn())
        assertTrue(counts.isAnythingPending())
    }

    @Test
    fun `test__cardsDueDateCount__allInProgressCards`() {
        // given
        val deck = storage.addDeck("mock deck")
        val count = 3
        val cardData = mutableListOf<CardData>()
        val date = today()
        for (i in 0 until count) {
            val data = mockCardData("original $i", "translation $i", deck.id)
            cardData.add(data)
            val card = storage.addCard(data)
            storage.updateCardState(card, State(date, 4), exercise)
        }

        // when
        val counts = storage.cardsDueDateCount(exercise, deck, today())

        // then
        assertEquals(0, counts.countNew())
        assertEquals(count, counts.countReview())
        assertEquals(0, counts.countRelearn())
        assertTrue(counts.isAnythingPending())
    }

    @Test
    fun `test__cardsDueDateCount__allLearntCards`() {
        // given
        val deck = storage.addDeck("mock deck")
        val count = 3
        val cardData = mutableListOf<CardData>()
        val date = today()
        for (i in 0 until count) {
            val data = mockCardData("original $i", "translation $i", deck.id)
            cardData.add(data)
            val card = storage.addCard(data)
            storage.updateCardState(card, State(date, PERIOD_LEARNT + 1), exercise)
        }

        // when
        val counts = storage.cardsDueDateCount(exercise, deck, today())

        // then
        assertEquals(0, counts.countNew())
        assertEquals(count, counts.countReview())
        assertEquals(0, counts.countRelearn())
        assertTrue(counts.isAnythingPending())
    }

    @Test
    fun `test__cardsDueDateCount__allRelearnCards`() {
        // given
        val deck = storage.addDeck("mock deck")
        val count = 3
        val cardData = mutableListOf<CardData>()
        val date = today()
        for (i in 0 until count) {
            val data = mockCardData("original $i", "translation $i", deck.id)
            cardData.add(data)
            val card = storage.addCard(data)
            storage.updateCardState(card, State(date, 0), exercise)
        }

        // when
        val counts = storage.cardsDueDateCount(exercise, deck, today())

        // then
        assertEquals(0, counts.countNew())
        assertEquals(0, counts.countReview())
        assertEquals(count, counts.countRelearn())
        assertTrue(counts.isAnythingPending())
    }

    @Test
    fun `test__cardsDueDateCount__mixedCards`() {
        // given
        val deck = storage.addDeck("mock deck")
        val count = 10
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
        storage.updateCardState(cards[1], State(today.plusDays(1), 4), exercise)
        storage.updateCardState(cards[2], State(today.minusDays(3), -1), exercise)
        storage.updateCardState(cards[3], State(today, 1), exercise)
        storage.updateCardState(cards[4], State(today.plusDays(1), -1), exercise)
        storage.updateCardState(cards[5], State(today.minusDays(1), PERIOD_LEARNT), exercise)
        storage.updateCardState(cards[6], State(today.minusDays(2), 0), exercise)
        storage.updateCardState(cards[7], State(today, -1), exercise)
        storage.updateCardState(cards[8], State(today.minusDays(1), 0), exercise)
        storage.updateCardState(cards[9], State(today.minusDays(2), PERIOD_LEARNT * 2), exercise)

        // when
        val counts = storage.cardsDueDateCount(exercise, deck, today)

        // then
        assertEquals(2, counts.countNew())
        assertEquals(4, counts.countReview())
        assertEquals(2, counts.countRelearn())
        assertTrue(counts.isAnythingPending())
    }

    @Test
    fun `test__cardsDueDateCount__noPendingCards`() {
        // given
        val deck = storage.addDeck("mock deck")
        val count = 3
        val cards = (0 until count)
                .map { mockCardData("original $it", "translation $it", deck.id) }
                .map { storage.addCard(it) }
        val today = today()

        storage.updateCardState(cards[0], State(today.plusDays(3), 0), exercise)
        storage.updateCardState(cards[1], State(today.plusDays(1), 4), exercise)
        storage.updateCardState(cards[2], State(today.plusDays(10), -1), exercise)

        // when
        val counts = storage.cardsDueDateCount(exercise, deck, today)

        // then
        assertEquals(0, counts.countNew())
        assertEquals(0, counts.countReview())
        assertEquals(0, counts.countRelearn())
        assertFalse(counts.isAnythingPending())
    }
}

private fun addMockLanguages(storage: Repository) {
    storage.addLanguage(LanguagesRegistry.original().value)
    storage.addLanguage(LanguagesRegistry.translations().value)
}