package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.storage.sqlite.*
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.model.Expression
import com.ashalmawia.coriolan.model.ExpressionType
import com.ashalmawia.coriolan.model.mockLanguage
import org.joda.time.DateTime
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SqliteConvertersTest {

    @Test
    fun createLanguageContentValuesTest() {
        // given
        val value = "Russian"

        // when
        val cv = createLanguageContentValues(value)

        // then
        assertEquals("values count is correct", 1, cv.size())
        assertEquals("$SQLITE_COLUMN_LANG_VALUE is correct", value, cv.get(SQLITE_COLUMN_LANG_VALUE))
    }

    @Test
    fun createExpressionContentValuesTest() {
        // given
        val value = "some value"
        val type = ExpressionType.WORD
        val lang = mockLanguage(777L, "Russian")

        // when
        val cv = createExpressionContentValues(value, type, lang)

        // then
        assertEquals("values count is correct", 3, cv.size())
        assertEquals("$SQLITE_COLUMN_VALUE is correct", value, cv.get(SQLITE_COLUMN_VALUE))
        assertEquals("$SQLITE_COLUMN_TYPE is correct", type.value, cv.get(SQLITE_COLUMN_TYPE))
        assertEquals("$SQLITE_COLUMN_LANGUAGE_ID is correct", lang.id, cv.get(SQLITE_COLUMN_LANGUAGE_ID))
    }

    @Test
    fun cardDataToContentValuesTest() {
        // given
        val deckId = 5L
        val lang = mockLanguage()
        val original = Expression(1L, "some original expression", ExpressionType.WORD, lang)

        // when
        val cv = createCardContentValues(deckId, original)

        // then
        assertEquals("values count is correct", 2, cv.size())
        assertEquals("$SQLITE_COLUMN_FRONT_ID is correct", original.id, cv.get(SQLITE_COLUMN_FRONT_ID))
        assertEquals("$SQLITE_COLUMN_DECK_ID is correct", deckId, cv.get(SQLITE_COLUMN_DECK_ID))
    }

    @Test
    fun `cardDataToContentValuesTest__hasCardId`() {
        // given
        val deckId = 5L
        val lang = mockLanguage()
        val original = Expression(1L, "some original expression", ExpressionType.WORD, lang)
        val cardId = 7L

        // when
        val cv = createCardContentValues(deckId, original, cardId)

        // then
        assertEquals("values count is correct", 3, cv.size())
        assertEquals("$SQLITE_COLUMN_ID is correct", cardId, cv.get(SQLITE_COLUMN_ID))
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
        val lang = mockLanguage()
        val translations = listOf(
                Expression(1L, "firework", ExpressionType.WORD, lang),
                Expression(2L, "rocket", ExpressionType.WORD, lang),
                Expression(8L, "missile", ExpressionType.WORD, lang)
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
        val due = DateTime(1519529781000)
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