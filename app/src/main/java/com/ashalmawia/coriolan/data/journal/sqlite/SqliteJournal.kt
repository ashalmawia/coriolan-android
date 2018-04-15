package com.ashalmawia.coriolan.data.journal.sqlite

import android.content.ContentValues
import android.content.Context
import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.data.SimpleCounts
import com.ashalmawia.coriolan.data.emptyCounts
import com.ashalmawia.coriolan.data.journal.Journal
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
            return if (it.moveToNext()) {
                SimpleCounts(
                        it.getCardsNew(),
                        it.getCardsReview(),
                        it.getCardsRelearn()
                )
            } else {
                emptyCounts()
            }
        }
    }

    override fun recordNewCardStudied(date: DateTime) {
        incrementColumn(SQLITE_COLUMN_CARDS_NEW, date)
    }

    override fun recordReviewStudied(date: DateTime) {
        incrementColumn(SQLITE_COLUMN_CARDS_REVIEW, date)
    }

    override fun recordCardRelearned(date: DateTime) {
        incrementColumn(SQLITE_COLUMN_CARDS_RELEARN, date)
    }

    override fun undoNewCardStudied(date: DateTime) {
        decrementColumn(SQLITE_COLUMN_CARDS_NEW, date)
    }

    override fun undoReviewStudied(date: DateTime) {
        decrementColumn(SQLITE_COLUMN_CARDS_REVIEW, date)
    }

    override fun undoCardRelearned(date: DateTime) {
        decrementColumn(SQLITE_COLUMN_CARDS_RELEARN, date)
    }

    private fun incrementColumn(columnName: String, date: DateTime) {
        changeColumn(columnName, date, "+ 1")
    }

    private fun decrementColumn(columnName: String, date: DateTime) {
        changeColumn(columnName, date, "- 1")
    }

    private fun changeColumn(columnName: String, date: DateTime, modifier: String) {
        val db = helper.writableDatabase

        // todo: use unique constraint on date
        db.beginTransaction()

        try {
            val statement = db.compileStatement("""
                |UPDATE $SQLITE_TABLE_JOURNAL
                |   SET $columnName = $columnName $modifier
                |   WHERE $SQLITE_COLUMN_DATE = ?
            """.trimMargin())
            statement.bindLong(1, date.timespamp)
            val rowsUpdated = statement.executeUpdateDelete()

            if (rowsUpdated == 0) {
                val cv = newCountsContentValues(columnName, date)
                db.insert(SQLITE_TABLE_JOURNAL, null, cv)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}

private fun newCountsContentValues(columnName: String, date: DateTime): ContentValues {
    val cv = ContentValues()
    cv.put(SQLITE_COLUMN_DATE, date.timespamp)
    cv.put(SQLITE_COLUMN_CARDS_NEW, 0)
    cv.put(SQLITE_COLUMN_CARDS_REVIEW, 0)
    cv.put(SQLITE_COLUMN_CARDS_RELEARN, 0)
    cv.put(columnName, 1)
    return cv
}