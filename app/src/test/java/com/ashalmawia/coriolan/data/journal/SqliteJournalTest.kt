package com.ashalmawia.coriolan.data.journal

import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.data.journal.sqlite.SqliteJournal
import com.ashalmawia.coriolan.learning.today
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class SqliteJournalTest {

    private val journal = SqliteJournal(RuntimeEnvironment.application)

    @Test
    fun test__cardsStudiedOnDate__noEntry() {
        // given
        val date = today()

        // when
        val counts = journal.cardsStudiedOnDate(date)

        // then
        assertEmpty(counts)
    }

    @Test
    fun test__recordNewCardStudied() {
        // given
        val date = today()

        // when
        journal.recordNewCardStudied(date)
        val counts = journal.cardsStudiedOnDate(date)

        // then
        assertValues(1, 0, 0, counts)

        // when
        journal.recordNewCardStudied(date)
        val counts2 = journal.cardsStudiedOnDate(date)

        // then
        assertValues(2, 0, 0, counts2)

        // when
        journal.undoNewCardStudied(date)
        val counts3 = journal.cardsStudiedOnDate(date)

        // then
        assertValues(1, 0, 0, counts3)
    }

    @Test
    fun test__recordReviewStudied() {
        // given
        val date = today()

        // when
        journal.recordReviewStudied(date)
        val counts = journal.cardsStudiedOnDate(date)

        // then
        assertValues(0, 1, 0, counts)

        // when
        journal.recordReviewStudied(date)
        val counts2 = journal.cardsStudiedOnDate(date)

        // then
        assertValues(0, 2, 0, counts2)

        // when
        journal.undoReviewStudied(date)
        val counts3 = journal.cardsStudiedOnDate(date)

        // then
        assertValues(0, 1, 0, counts3)
    }

    @Test
    fun test__recordCardRelearned() {
        // given
        val date = today()

        // when
        journal.recordCardRelearned(date)
        val counts = journal.cardsStudiedOnDate(date)

        // then
        assertValues(0, 0, 1, counts)

        // when
        journal.recordCardRelearned(date)
        val counts2 = journal.cardsStudiedOnDate(date)

        // then
        assertValues(0, 0, 2, counts2)

        // when
        journal.undoCardRelearned(date)
        val counts3 = journal.cardsStudiedOnDate(date)

        // then
        assertValues(0, 0, 1, counts3)
    }

    @Test
    fun test__recordsCombination() {
        // given
        val date = today()

        // when
        journal.recordReviewStudied(date)
        journal.recordNewCardStudied(date)
        journal.recordNewCardStudied(date)
        journal.recordNewCardStudied(date)
        journal.recordReviewStudied(date)
        journal.recordReviewStudied(date)
        journal.recordNewCardStudied(date)
        val counts = journal.cardsStudiedOnDate(date)

        // then
        assertValues(4, 3, 0, counts)

        // when
        journal.recordCardRelearned(date)
        journal.recordReviewStudied(date)
        journal.recordReviewStudied(date)
        journal.undoNewCardStudied(date)
        journal.recordNewCardStudied(date)
        journal.recordCardRelearned(date)
        journal.undoReviewStudied(date)
        journal.recordReviewStudied(date)
        journal.undoNewCardStudied(date)
        val counts2 = journal.cardsStudiedOnDate(date)

        // then
        assertValues(counts.new - 1, counts.review + 2, 2, counts2)
    }
}

private fun assertValues(expectedNew: Int, expectedReview: Int, expectedRelearn: Int, counts: Counts) {
    assertEquals(expectedNew, counts.new)
    assertEquals(expectedReview, counts.review)
    assertEquals(expectedRelearn, counts.relearn)
}
private fun assertEmpty(counts: Counts) {
    assertValues(0, 0, 0, counts)
}