package com.ashalmawia.coriolan.data.logbook

import com.ashalmawia.coriolan.data.backup.logbook.createNonEmptyLogbookWithMockData
import com.ashalmawia.coriolan.data.logbook.sqlite.SqliteLogbook
import com.ashalmawia.coriolan.data.storage.provideLogbookHelper
import com.ashalmawia.coriolan.learning.LearningDay
import com.ashalmawia.coriolan.learning.exercise.CardAction
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.model.mockDeck
import com.ashalmawia.coriolan.util.orZero
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.SQLiteMode

@RunWith(RobolectricTestRunner::class)
@SQLiteMode(SQLiteMode.Mode.LEGACY)
class SqliteLogbookTest {

    private val logbook = SqliteLogbook(provideLogbookHelper())

    private val today = mockToday()
    private val exerciseId = ExerciseId.TEST

    @Test
    fun test__cardsStudiedOnDate__noEntry() {
        // given
        val date = today

        // when
        val counts = logbook.cardsStudiedOnDate(date)

        // then
        assertEmpty(counts)
    }

    @Test
    fun test__recordNewCardStudied() {
        // given
        val date = today

        // when
        recordNewCardStudied(date)
        val counts = logbook.cardsStudiedOnDate(date, exerciseId)

        // then
        assertValues(1, 0, 0, counts)
        assertEquals(counts, logbook.cardsStudiedOnDate(date))

        // when
        recordNewCardStudied(date)
        val counts2 = logbook.cardsStudiedOnDate(date, exerciseId)

        // then
        assertValues(2, 0, 0, counts2)
        assertEquals(counts2, logbook.cardsStudiedOnDate(date))

        // when
        undoNewCardStudied(date)
        val counts3 = logbook.cardsStudiedOnDate(date, exerciseId)

        // then
        assertValues(1, 0, 0, counts3)
        assertEquals(counts3, logbook.cardsStudiedOnDate(date))
    }

    @Test
    fun test__recordReviewStudied() {
        // given
        val date = today

        // when
        recordReviewStudied(date)
        val counts = logbook.cardsStudiedOnDate(date, exerciseId)

        // then
        assertValues(0, 1, 0, counts)
        assertEquals(counts, logbook.cardsStudiedOnDate(date))

        // when
        recordReviewStudied(date)
        val counts2 = logbook.cardsStudiedOnDate(date, exerciseId)

        // then
        assertValues(0, 2, 0, counts2)
        assertEquals(counts2, logbook.cardsStudiedOnDate(date))

        // when
        undoReviewStudied(date)
        val counts3 = logbook.cardsStudiedOnDate(date, exerciseId)

        // then
        assertValues(0, 1, 0, counts3)
        assertEquals(counts3, logbook.cardsStudiedOnDate(date))
    }

    @Test
    fun test__recordCardRelearned() {
        // given
        val date = today

        // when
        recordCardRelearned(date)
        val counts = logbook.cardsStudiedOnDate(date, exerciseId)

        // then
        assertValues(0, 0, 1, counts)
        assertEquals(counts, logbook.cardsStudiedOnDate(date))

        // when
        recordCardRelearned(date)
        val counts2 = logbook.cardsStudiedOnDate(date, exerciseId)

        // then
        assertValues(0, 0, 2, counts2)
        assertEquals(counts2, logbook.cardsStudiedOnDate(date))

        // when
        undoCardRelearned(date)
        val counts3 = logbook.cardsStudiedOnDate(date, exerciseId)

        // then
        assertValues(0, 0, 1, counts3)
        assertEquals(counts3, logbook.cardsStudiedOnDate(date))
    }

    @Test
    fun test__recordsCombination() {
        // given
        val date = today

        // when
        recordReviewStudied(date)
        recordNewCardStudied(date)
        recordNewCardStudied(date)
        recordNewCardStudied(date)
        recordReviewStudied(date)
        recordReviewStudied(date)
        recordNewCardStudied(date)
        val counts = logbook.cardsStudiedOnDate(date, exerciseId)

        // then
        assertValues(4, 3, 0, counts)
        assertEquals(counts, logbook.cardsStudiedOnDate(date))

        // when
        recordCardRelearned(date)
        recordReviewStudied(date)
        recordReviewStudied(date)
        undoNewCardStudied(date)
        recordNewCardStudied(date)
        recordCardRelearned(date)
        undoReviewStudied(date)
        recordReviewStudied(date)
        undoNewCardStudied(date)
        val counts2 = logbook.cardsStudiedOnDate(date, exerciseId)
        assertEquals(counts2, logbook.cardsStudiedOnDate(date))

        // then
        assertValues(3, 5, 2, counts2)
    }

    @Test
    fun test__multipleExercises() {
        // given
        val date = today
        val deckId = 1L
        val anotherDeckId = 2L
        val anotherExerciseId = ExerciseId.FLASHCARDS

        // when
        logbook.incrementCardActions(date, exerciseId, deckId, CardAction.NEW_CARD_FIRST_SEEN)
        logbook.incrementCardActions(date, exerciseId, deckId, CardAction.NEW_CARD_FIRST_SEEN)
        logbook.incrementCardActions(date, exerciseId, anotherDeckId, CardAction.CARD_REVIEWED)
        logbook.incrementCardActions(date, exerciseId, deckId, CardAction.CARD_RELEARNED)
        logbook.incrementCardActions(date, exerciseId, anotherDeckId, CardAction.CARD_REVIEWED)

        logbook.incrementCardActions(date, anotherExerciseId, deckId, CardAction.NEW_CARD_FIRST_SEEN)
        logbook.incrementCardActions(date, anotherExerciseId, anotherDeckId, CardAction.CARD_REVIEWED)
        logbook.incrementCardActions(date, anotherExerciseId, deckId, CardAction.NEW_CARD_FIRST_SEEN)
        logbook.incrementCardActions(date, anotherExerciseId, deckId, CardAction.NEW_CARD_FIRST_SEEN)
        logbook.incrementCardActions(date, anotherExerciseId, anotherDeckId, CardAction.NEW_CARD_FIRST_SEEN)
        logbook.decrementCardActions(date, anotherExerciseId, deckId, CardAction.NEW_CARD_FIRST_SEEN)

        val countsA = logbook.cardsStudiedOnDate(date, exerciseId)
        val countsB = logbook.cardsStudiedOnDate(date, anotherExerciseId)
        val countsTotal = logbook.cardsStudiedOnDate(date)

        // then
        assertValues(2, 2, 1, countsA)
        assertValues(3, 1, 0, countsB)
        assertValues(5, 3, 1, countsTotal)
    }

    @Test
    fun test__multipleDecks() {
        // given
        val date = today
        val deckId1 = 1L
        val deckId2 = 2L
        val anotherExerciseId = ExerciseId.FLASHCARDS

        // when
        logbook.incrementCardActions(date, exerciseId, deckId1, CardAction.NEW_CARD_FIRST_SEEN)
        logbook.incrementCardActions(date, exerciseId, deckId1, CardAction.NEW_CARD_FIRST_SEEN)
        logbook.incrementCardActions(date, exerciseId, deckId1, CardAction.CARD_REVIEWED)
        logbook.incrementCardActions(date, anotherExerciseId, deckId1, CardAction.CARD_RELEARNED)
        logbook.incrementCardActions(date, exerciseId, deckId1, CardAction.CARD_REVIEWED)

        logbook.incrementCardActions(date, exerciseId, deckId2, CardAction.NEW_CARD_FIRST_SEEN)
        logbook.incrementCardActions(date, anotherExerciseId, deckId2, CardAction.CARD_REVIEWED)
        logbook.incrementCardActions(date, exerciseId, deckId2, CardAction.NEW_CARD_FIRST_SEEN)
        logbook.incrementCardActions(date, anotherExerciseId, deckId2, CardAction.NEW_CARD_FIRST_SEEN)
        logbook.incrementCardActions(date, exerciseId, deckId2, CardAction.NEW_CARD_FIRST_SEEN)
        logbook.decrementCardActions(date, exerciseId, deckId2, CardAction.NEW_CARD_FIRST_SEEN)

        val countsA = logbook.cardsStudiedOnDate(date, deckId1)
        val countsB = logbook.cardsStudiedOnDate(date, deckId2)
        val countsTotal = logbook.cardsStudiedOnDate(date)

        // then
        assertValues(2, 2, 1, countsA)
        assertValues(3, 1, 0, countsB)
        assertValues(5, 3, 1, countsTotal)
    }

    @Test
    fun test__cardsStudiedOnDateRange_sameDate() {
        // given
        val date = today
        val deckId1 = 1L
        val deckId2 = 2L
        val decks = listOf(mockDeck(id = deckId1), mockDeck(id = deckId2))

        // when
        var counts = logbook.cardsStudiedOnDateRange(date, date, decks)

        // then
        assertTrue(counts.isEmpty())

        // when
        logbook.incrementCardActions(date, exerciseId, deckId1, CardAction.NEW_CARD_FIRST_SEEN)
        logbook.incrementCardActions(date, exerciseId, deckId1, CardAction.CARD_REVIEWED)
        logbook.incrementCardActions(date, exerciseId, deckId2, CardAction.NEW_CARD_FIRST_SEEN)
        counts = logbook.cardsStudiedOnDateRange(date, date, decks)

        // then
        assertEquals(1, counts.size)
        assertEquals(mapOf(
                CardAction.NEW_CARD_FIRST_SEEN to 2,
                CardAction.CARD_REVIEWED to 1
        ), counts[date])
    }

    @Test
    fun test__cardsStudiedOnDateRange_week() {
        // given
        val dateStart = today.minusDays(7)
        val dateEnd = today
        val deckId1 = 1L
        val deckId2 = 2L
        val decks = listOf(mockDeck(id = deckId1), mockDeck(id = deckId2))

        // when
        var counts = logbook.cardsStudiedOnDateRange(dateStart, dateEnd, decks)

        // then
        assertTrue(counts.isEmpty())

        // when
        logbook.incrementCardActions(dateStart, exerciseId, deckId1, CardAction.NEW_CARD_FIRST_SEEN)
        counts = logbook.cardsStudiedOnDateRange(dateStart, dateEnd, decks)

        // then
        assertEquals(1, counts.size)
        assertEquals(mapOf(CardAction.NEW_CARD_FIRST_SEEN to 1), counts[dateStart])

        // when
        logbook.incrementCardActions(dateStart, exerciseId, deckId2, CardAction.NEW_CARD_FIRST_SEEN)
        logbook.incrementCardActions(dateStart.plusDays(2), exerciseId, deckId1, CardAction.CARD_REVIEWED)
        logbook.incrementCardActions(dateEnd, exerciseId, deckId1, CardAction.CARD_REVIEWED)
        counts = logbook.cardsStudiedOnDateRange(dateStart, dateEnd, decks)

        // then
        assertEquals(3, counts.size)
        assertEquals(mapOf(CardAction.NEW_CARD_FIRST_SEEN to 2), counts[dateStart])
        assertEquals(mapOf(CardAction.CARD_REVIEWED to 1), counts[dateStart.plusDays(2)])
        assertEquals(mapOf(CardAction.CARD_REVIEWED to 1), counts[dateEnd])
    }

    @Test
    fun test_dropAllData() {
        // given
        val logbook = createNonEmptyLogbookWithMockData()

        // when
        logbook.dropAllData()

        // then
        assertTrue(logbook.exportAllData(0, 500).isEmpty())

        // when
        (logbook as Logbook).incrementCardActions(today, exerciseId, 1L, CardAction.CARD_RELEARNED)

        // then
        assertEquals(1, logbook.exportAllData(0, 500).size)
    }

    private fun recordCardRelearned(date: LearningDay, deckId: Long = 1L) {
        logbook.incrementCardActions(date, exerciseId, deckId, CardAction.CARD_RELEARNED)
    }

    private fun recordReviewStudied(date: LearningDay, deckId: Long = 1L) {
        logbook.incrementCardActions(date, exerciseId, deckId, CardAction.CARD_REVIEWED)
    }

    private fun recordNewCardStudied(date: LearningDay, deckId: Long = 1L) {
        logbook.incrementCardActions(date, exerciseId, deckId, CardAction.NEW_CARD_FIRST_SEEN)
    }

    private fun undoCardRelearned(date: LearningDay, deckId: Long = 1L) {
        logbook.decrementCardActions(date, exerciseId, deckId, CardAction.CARD_RELEARNED)
    }

    private fun undoReviewStudied(date: LearningDay, deckId: Long = 1L) {
        logbook.decrementCardActions(date, exerciseId, deckId, CardAction.CARD_REVIEWED)
    }

    private fun undoNewCardStudied(date: LearningDay, deckId: Long = 1L) {
        logbook.decrementCardActions(date, exerciseId, deckId, CardAction.NEW_CARD_FIRST_SEEN)
    }
}

private fun assertValues(expectedNew: Int, expectedReview: Int, expectedRelearn: Int, counts: Map<CardAction, Int>) {
    assertEquals(expectedNew, counts[CardAction.NEW_CARD_FIRST_SEEN].orZero())
    assertEquals(expectedReview, counts[CardAction.CARD_REVIEWED].orZero())
    assertEquals(expectedRelearn, counts[CardAction.CARD_RELEARNED].orZero())
}
private fun assertEmpty(counts: Map<CardAction, Int>) {
    assertValues(0, 0, 0, counts)
}