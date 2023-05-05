package com.ashalmawia.coriolan.data.logbook.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.data.logbook.LogbookPayload
import com.ashalmawia.coriolan.data.storage.sqlite.insertOrUpdate
import com.ashalmawia.coriolan.learning.exercise.CardAction
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.data.storage.sqlite.string
import com.ashalmawia.coriolan.util.timespamp
import org.joda.time.DateTime

class SqliteLogbook(context: Context) : Logbook {

    private val helper = SqliteJornalOpenHelper(context)
    private val serializer = LogbookPayloadSerializer()

    override fun cardsStudiedOnDate(date: DateTime): Map<CardAction, Int> {
        val payload = readPayload(date)
        return payload.cardActions.total.unwrap()
    }

    override fun cardsStudiedOnDate(date: DateTime, exercise: ExerciseId): Map<CardAction, Int> {
        return readPayload(date).cardActions.byExercise(exercise).unwrap()
    }

    override fun cardsStudiedOnDate(date: DateTime, deckId: Long): Map<CardAction, Int> {
        return readPayload(date).cardActions.byDeck(deckId).unwrap()
    }

    private fun readPayload(date: DateTime): LogbookPayload {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $SQLITE_TABLE_JOURNAL
            |   WHERE $SQLITE_COLUMN_DATE = ?
        """.trimMargin(), arrayOf(date.timespamp.toString()))

        cursor.use {
            if (it.moveToNext()) {
                return serializer.deserializeLogbookPayload(it.getPayload())
            } else {
                return LogbookPayload.create()
            }
        }
    }

    override fun incrementCardActions(date: DateTime, exercise: ExerciseId, deckId: Long, cardAction: CardAction) {
        updatePayload(date) {
            cardActions.increment(exercise, deckId, cardAction)
        }
    }

    override fun decrementCardActions(date: DateTime, exercise: ExerciseId, deckId: Long, cardAction: CardAction) {
        updatePayload(date) {
            cardActions.decrement(exercise, deckId, cardAction)
        }
    }

    private fun updatePayload(
            date: DateTime,
            update: LogbookPayload.() -> Unit
    ) {
        val payload = readPayload(date)
        payload.update()
        savePayload(date, payload)
    }

    private fun savePayload(date: DateTime, payload: LogbookPayload) {
        val cv = createContentValues(date, payload)

        val db = helper.writableDatabase
        db.beginTransaction()

        try {
            db.insertOrUpdate(SQLITE_TABLE_JOURNAL, cv)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun createContentValues(date: DateTime, payload: LogbookPayload): ContentValues {
        val cv = ContentValues()
        cv.put(SQLITE_COLUMN_DATE, date.timespamp)
        cv.put(SQLITE_COLUMN_PAYLOAD, serializer.serializeLogbookPayload(payload))
        return cv
    }
}

private fun Cursor.getPayload(): String { return string(SQLITE_COLUMN_PAYLOAD, null) }
