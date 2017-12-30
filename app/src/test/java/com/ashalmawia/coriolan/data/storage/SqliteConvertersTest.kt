package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.storage.sqlite.*
import com.ashalmawia.coriolan.model.Expression
import com.ashalmawia.coriolan.model.ExpressionType
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SqliteConvertersTest {

    @Test
    fun createExpressionContentValuesTest() {
        // given
        val value = "some value"
        val type = ExpressionType.WORD

        // when
        val cv = createExpressionContentValues(value, type)

        // then
        assertEquals("values count is correct", 2, cv.size())
        assertEquals("$SQLITE_COLUMN_VALUE is correct", value, cv.get(SQLITE_COLUMN_VALUE))
        assertEquals("$SQLITE_COLUMN_TYPE is correct", type.value, cv.get(SQLITE_COLUMN_TYPE))
    }

    @Test
    fun cardDataToContentValuesTest() {
        // given
        val deckId = 5L
        val original = Expression(1L, "some original expression", ExpressionType.WORD)
        val translation = Expression(99L, "some translated expression", ExpressionType.WORD)

        // when
        val cv = toContentValues(deckId, original, translation)

        // then
        assertEquals("values count is correct", 3, cv.size())
        assertEquals("$SQLITE_COLUMN_FRONT_ID is correct", original.id, cv.get(SQLITE_COLUMN_FRONT_ID))
        assertEquals("$SQLITE_COLUMN_REVERSE_ID is correct", translation.id, cv.get(SQLITE_COLUMN_REVERSE_ID))
        assertEquals("$SQLITE_COLUMN_DECK_ID is correct", deckId, cv.get(SQLITE_COLUMN_DECK_ID))
    }

    @Test
    fun createDeckContentValuesTest() {
        // given
        val name = "New Deck"

        // when
        val cv = createDeckContentValues(name)

        // then
        assertEquals("values count is correct", 1, cv.size())
        assertEquals("$SQLITE_COLUMN_NAME is correct", name, cv.get(SQLITE_COLUMN_NAME))
    }
}