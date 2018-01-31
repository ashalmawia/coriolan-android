package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.storage.sqlite.*
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.model.Expression
import com.ashalmawia.coriolan.model.ExpressionType
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

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

        // when
        val cv = toContentValues(deckId, original)

        // then
        assertEquals("values count is correct", 2, cv.size())
        assertEquals("$SQLITE_COLUMN_FRONT_ID is correct", original.id, cv.get(SQLITE_COLUMN_FRONT_ID))
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

    @Test
    fun generateCardsReverseContentValuesTest() {
        // given
        val cardId = 99L
        val translations = listOf(
                Expression(1L, "firework", ExpressionType.WORD),
                Expression(2L, "rocket", ExpressionType.WORD),
                Expression(8L, "missile", ExpressionType.WORD)
        )

        // when
        val cvList = generateCardsReverseContentValues(cardId, translations)

        // then
        assertEquals("entries count is correct", 3, cvList.size)
        for (i in 0 until 3) {
            val cv = cvList[i]
            assertEquals("values count is correct", 2, cv.size())
            assertEquals("$SQLITE_COLUMN_CARD_ID is correct", cardId, cv.get(SQLITE_COLUMN_CARD_ID))
            assertEquals("$SQLITE_COLUMN_EXPRESSION_ID is correct", translations[i].id, cv.get(SQLITE_COLUMN_EXPRESSION_ID))
        }
    }

    @Test
    fun createStateContentValuesTest() {
        // given
        val cardId = 1L
        val due = Date(1519529781000)
        val period = 16
        val state = State(due, period)

        // when
        val cv = createStateContentValues(cardId, state)

        // then
        assertEquals("values count is correct", 3, cv.size())
        assertEquals("$SQLITE_COLUMN_CARD_ID is correct", cardId, cv.get(SQLITE_COLUMN_CARD_ID))
        assertEquals("$SQLITE_COLUMN_DUE is correct", due, cv.getAsDate(SQLITE_COLUMN_DUE))
        assertEquals("$SQLITE_COLUMN_PERIOD is correct", period, cv.get(SQLITE_COLUMN_PERIOD))
    }
}