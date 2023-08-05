package com.ashalmawia.coriolan.data.logbook.sqlite

import android.content.ContentValues
import android.database.Cursor
import com.ashalmawia.coriolan.data.logbook.BackupableLogbook
import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.data.logbook.LogbookCardActionsPayloadEntry
import com.ashalmawia.coriolan.data.logbook.LogbookEntryInfo
import com.ashalmawia.coriolan.data.logbook.LogbookPayload
import com.ashalmawia.coriolan.data.storage.sqlite.date
import com.ashalmawia.coriolan.data.storage.sqlite.insertOrUpdate
import com.ashalmawia.coriolan.data.storage.sqlite.long
import com.ashalmawia.coriolan.learning.exercise.CardAction
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.data.storage.sqlite.string
import com.ashalmawia.coriolan.data.util.dropAllTables
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.DeckId
import com.ashalmawia.coriolan.util.orZero
import com.ashalmawia.coriolan.util.timespamp
import org.joda.time.DateTime

class SqliteLogbook(private val helper: SqliteLogbookOpenHelper) : Logbook, BackupableLogbook {

    private val serializer = LogbookPayloadSerializer()

    override fun cardsStudiedOnDate(date: DateTime): Map<CardAction, Int> {
        val payload = readPayload(date)
        return payload.cardActions.total.unwrap()
    }

    override fun cardsStudiedOnDate(date: DateTime, exercise: ExerciseId): Map<CardAction, Int> {
        return readPayload(date).cardActions.byExercise(exercise).unwrap()
    }

    override fun cardsStudiedOnDate(date: DateTime, deckId: DeckId): Map<CardAction, Int> {
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

    override fun cardsStudiedOnDateRange(from: DateTime, to: DateTime, decks: List<Deck>): Map<DateTime, Map<CardAction, Int>> {
        val payloads = readPayloads(from, to)
        val set = decks.map { it.id.value }.toSet()
        return payloads.mapValues {
            val byDeck: Map<Long, LogbookCardActionsPayloadEntry> = it.value.cardActions.byDeck
            val filtered: Map<Long, LogbookCardActionsPayloadEntry> = byDeck.filterKeys { set.contains(it) }
            val unwrapped: Map<Long, Map<CardAction, Int>> = filtered.mapValues { it.value.unwrap() }
            val result = mutableMapOf<CardAction, Int>()
            unwrapped.values.forEach { map ->
                map.forEach { entry ->
                    result[entry.key] = result[entry.key].orZero() + entry.value
                }
            }
            result
        }
    }

    private fun readPayloads(from: DateTime, to: DateTime): Map<DateTime, LogbookPayload> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $SQLITE_TABLE_JOURNAL
            |   WHERE $SQLITE_COLUMN_DATE BETWEEN ? AND ?
        """.trimMargin(), arrayOf(from.timespamp.toString(), to.timespamp.toString()))

        return cursor.use { it.extractAllData() }
    }

    override fun incrementCardActions(date: DateTime, exercise: ExerciseId, deckId: DeckId, cardAction: CardAction) {
        updatePayload(date) {
            cardActions.increment(exercise, deckId, cardAction)
        }
    }

    override fun decrementCardActions(date: DateTime, exercise: ExerciseId, deckId: DeckId, cardAction: CardAction) {
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
        return createContentValues(date.timespamp, serializer.serializeLogbookPayload(payload))
    }

    private fun createContentValues(timestamp: Long, rawPayload: String): ContentValues {
        val cv = ContentValues()
        cv.put(SQLITE_COLUMN_DATE, timestamp)
        cv.put(SQLITE_COLUMN_PAYLOAD, rawPayload)
        return cv
    }

    override fun overrideAllData(data: List<LogbookEntryInfo>) {
        val db = helper.writableDatabase

        data.forEach { (date, payload) ->
            val cv = createContentValues(date, payload)
            db.insertOrUpdate(SQLITE_TABLE_JOURNAL, cv)
        }
    }

    override fun exportAllData(offset: Int, limit: Int): List<LogbookEntryInfo> {
        val db = helper.readableDatabase

        val cursor = db.rawQuery("""
            |SELECT *
            |   FROM $SQLITE_TABLE_JOURNAL
            |   ORDER BY $SQLITE_COLUMN_DATE ASC
            |   LIMIT $limit OFFSET $offset
        """.trimMargin(), arrayOf())

        return cursor.use {
            val list = mutableListOf<LogbookEntryInfo>()
            while (it.moveToNext()) {
                val date = it.long(SQLITE_COLUMN_DATE)
                val rawPayload = it.getPayload()
                list.add(LogbookEntryInfo(date, rawPayload))
            }
            list
        }
    }

    private fun Cursor.extractAllData(): Map<DateTime, LogbookPayload> {
        val map = mutableMapOf<DateTime, LogbookPayload>()
        while (moveToNext()) {
            val date = date(SQLITE_COLUMN_DATE)
            val payload = serializer.deserializeLogbookPayload(getPayload())
            map[date] = payload
        }
        return map
    }

    override fun beginTransaction() {
        helper.writableDatabase.beginTransaction()
    }

    override fun endTransaction() {
        helper.writableDatabase.endTransaction()
    }

    override fun setTransactionSuccessful() {
        helper.writableDatabase.setTransactionSuccessful()
    }

    override fun dropAllData() {
        val db = helper.writableDatabase

        dropAllTables(db)
        helper.initializeSchema(db)
    }
}

private fun Cursor.getPayload(): String { return string(SQLITE_COLUMN_PAYLOAD, null) }
