package com.ashalmawia.coriolan.data.journal

import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.data.journal.sqlite.SqliteJournal
import com.ashalmawia.coriolan.learning.LearningDay
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.mockToday
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.SQLiteMode

@RunWith(RobolectricTestRunner::class)
@SQLiteMode(SQLiteMode.Mode.LEGACY)
class SqliteJournalTest {

    private val journal = SqliteJournal(RuntimeEnvironment.application)

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
        assertValues(counts.new - 1, counts.review + 2, 2, counts2)
    }

    @Test
    fun test__multipleExercises() {
        // given
        val date = today
        val anotherExerciseId = ExerciseId.TEST

        // when
        journal.incrementCardStudied(date, Status.NEW, exerciseId)
        journal.incrementCardStudied(date, Status.NEW, exerciseId)
        journal.incrementCardStudied(date, Status.IN_PROGRESS, exerciseId)
        journal.incrementCardStudied(date, Status.RELEARN, exerciseId)
        journal.incrementCardStudied(date, Status.IN_PROGRESS, exerciseId)

        journal.incrementCardStudied(date, Status.NEW, anotherExerciseId)
        journal.incrementCardStudied(date, Status.IN_PROGRESS, anotherExerciseId)
        journal.incrementCardStudied(date, Status.NEW, anotherExerciseId)
        journal.incrementCardStudied(date, Status.NEW, anotherExerciseId)
        journal.incrementCardStudied(date, Status.NEW, anotherExerciseId)
        journal.decrementCardStudied(date, Status.NEW, anotherExerciseId)

        val countsA = journal.cardsStudiedOnDate(date, exerciseId)
        val countsB = journal.cardsStudiedOnDate(date, anotherExerciseId)
        val countsTotal = journal.cardsStudiedOnDate(date)

        // then
        assertValues(2, 2, 1, countsA)
        assertValues(3, 1, 0, countsB)
        assertValues(5, 3, 1, countsTotal)
    }

    private fun recordCardRelearned(date: LearningDay) {
        journal.incrementCardStudied(date, Status.RELEARN, exerciseId)
    }

    private fun recordReviewStudied(date: LearningDay) {
        journal.incrementCardStudied(date, Status.IN_PROGRESS, exerciseId)
    }

    private fun recordNewCardStudied(date: LearningDay) {
        journal.incrementCardStudied(date, Status.NEW, exerciseId)
    }

    private fun undoCardRelearned(date: LearningDay) {
        journal.decrementCardStudied(date, Status.RELEARN, exerciseId)
    }

    private fun undoReviewStudied(date: LearningDay) {
        journal.decrementCardStudied(date, Status.IN_PROGRESS, exerciseId)
    }

    private fun undoNewCardStudied(date: LearningDay) {
        journal.decrementCardStudied(date, Status.NEW, exerciseId)
    }
}

private fun assertValues(expectedNew: Int, expectedReview: Int, expectedRelearn: Int, counts: Counts) {
    assertEquals(Counts(expectedNew, expectedReview, expectedRelearn, expectedNew + expectedReview + expectedRelearn), counts)
}
private fun assertEmpty(counts: Counts) {
    assertValues(0, 0, 0, counts)
}