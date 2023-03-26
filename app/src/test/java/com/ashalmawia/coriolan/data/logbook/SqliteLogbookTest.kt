package com.ashalmawia.coriolan.data.logbook

import com.ashalmawia.coriolan.data.logbook.sqlite.SqliteLogbook
import com.ashalmawia.coriolan.learning.LearningDay
import com.ashalmawia.coriolan.learning.exercise.CardAction
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.util.orZero
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.SQLiteMode

@RunWith(RobolectricTestRunner::class)
@SQLiteMode(SQLiteMode.Mode.LEGACY)
class SqliteLogbookTest {

    private val journal = SqliteLogbook(RuntimeEnvironment.application)

    private val today = mockToday()
    private val exerciseId = ExerciseId.SPACED_REPETITION

    @Test
    fun test__cardsStudiedOnDate__noEntry() {
        // given
        val date = today

        // when
        val counts = journal.cardsStudiedOnDate(date)

        // then
        assertEmpty(counts)
    }

    @Test
    fun test__recordNewCardStudied() {
        // given
        val date = today

        // when
        recordNewCardStudied(date)
        val counts = journal.cardsStudiedOnDate(date, exerciseId)

        // then
        assertValues(1, 0, 0, counts)
        assertEquals(counts, journal.cardsStudiedOnDate(date))

        // when
        recordNewCardStudied(date)
        val counts2 = journal.cardsStudiedOnDate(date, exerciseId)

        // then
        assertValues(2, 0, 0, counts2)
        assertEquals(counts2, journal.cardsStudiedOnDate(date))

        // when
        undoNewCardStudied(date)
        val counts3 = journal.cardsStudiedOnDate(date, exerciseId)

        // then
        assertValues(1, 0, 0, counts3)
        assertEquals(counts3, journal.cardsStudiedOnDate(date))
    }

    @Test
    fun test__recordReviewStudied() {
        // given
        val date = today

        // when
        recordReviewStudied(date)
        val counts = journal.cardsStudiedOnDate(date, exerciseId)

        // then
        assertValues(0, 1, 0, counts)
        assertEquals(counts, journal.cardsStudiedOnDate(date))

        // when
        recordReviewStudied(date)
        val counts2 = journal.cardsStudiedOnDate(date, exerciseId)

        // then
        assertValues(0, 2, 0, counts2)
        assertEquals(counts2, journal.cardsStudiedOnDate(date))

        // when
        undoReviewStudied(date)
        val counts3 = journal.cardsStudiedOnDate(date, exerciseId)

        // then
        assertValues(0, 1, 0, counts3)
        assertEquals(counts3, journal.cardsStudiedOnDate(date))
    }

    @Test
    fun test__recordCardRelearned() {
        // given
        val date = today

        // when
        recordCardRelearned(date)
        val counts = journal.cardsStudiedOnDate(date, exerciseId)

        // then
        assertValues(0, 0, 1, counts)
        assertEquals(counts, journal.cardsStudiedOnDate(date))

        // when
        recordCardRelearned(date)
        val counts2 = journal.cardsStudiedOnDate(date, exerciseId)

        // then
        assertValues(0, 0, 2, counts2)
        assertEquals(counts2, journal.cardsStudiedOnDate(date))

        // when
        undoCardRelearned(date)
        val counts3 = journal.cardsStudiedOnDate(date, exerciseId)

        // then
        assertValues(0, 0, 1, counts3)
        assertEquals(counts3, journal.cardsStudiedOnDate(date))
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
        val counts = journal.cardsStudiedOnDate(date, exerciseId)

        // then
        assertValues(4, 3, 0, counts)
        assertEquals(counts, journal.cardsStudiedOnDate(date))

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
        val counts2 = journal.cardsStudiedOnDate(date, exerciseId)
        assertEquals(counts2, journal.cardsStudiedOnDate(date))

        // then
        assertValues(3, 5, 2, counts2)
    }

    @Test
    fun test__multipleExercises() {
        // given
        val date = today
        val anotherExerciseId = ExerciseId.TEST

        // when
        journal.incrementCardActions(date, exerciseId, CardAction.NEW_CARD_FIRST_SEEN)
        journal.incrementCardActions(date, exerciseId, CardAction.NEW_CARD_FIRST_SEEN)
        journal.incrementCardActions(date, exerciseId, CardAction.CARD_REVIEWED)
        journal.incrementCardActions(date, exerciseId, CardAction.CARD_RELEARNED)
        journal.incrementCardActions(date, exerciseId, CardAction.CARD_REVIEWED)

        journal.incrementCardActions(date, anotherExerciseId, CardAction.NEW_CARD_FIRST_SEEN)
        journal.incrementCardActions(date, anotherExerciseId, CardAction.CARD_REVIEWED)
        journal.incrementCardActions(date, anotherExerciseId, CardAction.NEW_CARD_FIRST_SEEN)
        journal.incrementCardActions(date, anotherExerciseId, CardAction.NEW_CARD_FIRST_SEEN)
        journal.incrementCardActions(date, anotherExerciseId, CardAction.NEW_CARD_FIRST_SEEN)
        journal.decrementCardActions(date, anotherExerciseId, CardAction.NEW_CARD_FIRST_SEEN)

        val countsA = journal.cardsStudiedOnDate(date, exerciseId)
        val countsB = journal.cardsStudiedOnDate(date, anotherExerciseId)
        val countsTotal = journal.cardsStudiedOnDate(date)

        // then
        assertValues(2, 2, 1, countsA)
        assertValues(3, 1, 0, countsB)
        assertValues(5, 3, 1, countsTotal)
    }

    private fun recordCardRelearned(date: LearningDay) {
        journal.incrementCardActions(date, exerciseId, CardAction.CARD_RELEARNED)
    }

    private fun recordReviewStudied(date: LearningDay) {
        journal.incrementCardActions(date, exerciseId, CardAction.CARD_REVIEWED)
    }

    private fun recordNewCardStudied(date: LearningDay) {
        journal.incrementCardActions(date, exerciseId, CardAction.NEW_CARD_FIRST_SEEN)
    }

    private fun undoCardRelearned(date: LearningDay) {
        journal.decrementCardActions(date, exerciseId, CardAction.CARD_RELEARNED)
    }

    private fun undoReviewStudied(date: LearningDay) {
        journal.decrementCardActions(date, exerciseId, CardAction.CARD_REVIEWED)
    }

    private fun undoNewCardStudied(date: LearningDay) {
        journal.decrementCardActions(date, exerciseId, CardAction.NEW_CARD_FIRST_SEEN)
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