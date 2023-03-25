package com.ashalmawia.coriolan.data.journal.sqlite

import android.content.ContentValues
import android.content.Context
import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.util.timespamp
import org.joda.time.DateTime

class SqliteJournal(context: Context) : Journal {

    private val helper = SqliteJornalOpenHelper(context)

    override fun cardsStudiedOnDate(date: DateTime): Counts {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $SQLITE_TABLE_JOURNAL
            |   WHERE $SQLITE_COLUMN_DATE = ?
        """.trimMargin(), arrayOf(date.timespamp.toString()))

        cursor.use {
            var new = 0
            var review = 0
            var relearn = 0
            while (it.moveToNext()) {
                new += it.getCardsNew()
                review += it.getCardsReview()
                relearn += it.getCardsRelearn()
            }
            return Counts(new, review, relearn, new + review + relearn)
        }
    }

    override fun cardsStudiedOnDate(date: DateTime, exercise: ExerciseId): Counts {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $SQLITE_TABLE_JOURNAL
            |   WHERE $SQLITE_COLUMN_DATE = ? AND $SQLITE_COLUMN_EXERCISE = ?
        """.trimMargin(), arrayOf(date.timespamp.toString(), exercise.value))

        cursor.use {
            return if (it.moveToNext()) {
                val new = it.getCardsNew()
                val review = it.getCardsReview()
                val relearn = it.getCardsRelearn()
                Counts(
                        new,
                        review,
                        relearn,
                        new + review + relearn
                )
            } else {
                Counts.empty()
            }
        }
    }

    override fun incrementCardStudied(date: DateTime, targetStatus: Status, exercise: ExerciseId) {
        incrementColumn(columnByStatus(targetStatus), date, exercise)
    }

    override fun decrementCardStudied(date: DateTime, targetStatus: Status, exercise: ExerciseId) {
        decrementColumn(columnByStatus(targetStatus), date, exercise)
    }

    private fun columnByStatus(status: Status): String {
        return when (status) {
            Status.NEW -> SQLITE_COLUMN_CARDS_NEW
            Status.RELEARN -> SQLITE_COLUMN_CARDS_RELEARN
            Status.IN_PROGRESS -> SQLITE_COLUMN_CARDS_REVIEW
            Status.LEARNT -> SQLITE_COLUMN_CARDS_LEARNT
        }
    }

    private fun incrementColumn(columnName: String, date: DateTime, exerciseId: ExerciseId) {
        changeColumn(columnName, date, exerciseId, "+ 1")
    }

    private fun decrementColumn(columnName: String, date: DateTime, exerciseId: ExerciseId) {
        changeColumn(columnName, date, exerciseId, "- 1")
    }

    private fun changeColumn(columnName: String, date: DateTime, exerciseId: ExerciseId, modifier: String) {
        val db = helper.writableDatabase
        db.beginTransaction()

        try {
            val statement = db.compileStatement("""
                |UPDATE $SQLITE_TABLE_JOURNAL
                |   SET $columnName = $columnName $modifier
                |   WHERE $SQLITE_COLUMN_DATE = ? AND $SQLITE_COLUMN_EXERCISE = ?
            """.trimMargin())
            statement.bindLong(1, date.timespamp)
            statement.bindString(2, exerciseId.value)
            val rowsUpdated = statement.executeUpdateDelete()

            if (rowsUpdated == 0) {
                val cv = newCountsContentValues(columnName, date, exerciseId)
                val result = db.insert(SQLITE_TABLE_JOURNAL, null, cv)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}

private fun newCountsContentValues(columnName: String, date: DateTime, exerciseId: ExerciseId): ContentValues {
    val cv = ContentValues()
    cv.put(SQLITE_COLUMN_DATE, date.timespamp)
    cv.put(SQLITE_COLUMN_EXERCISE, exerciseId.value)
    cv.put(SQLITE_COLUMN_CARDS_NEW, 0)
    cv.put(SQLITE_COLUMN_CARDS_REVIEW, 0)
    cv.put(SQLITE_COLUMN_CARDS_RELEARN, 0)
    cv.put(SQLITE_COLUMN_CARDS_LEARNT, 0)
    cv.put(columnName, 1)
    return cv
}