package com.ashalmawia.coriolan

import com.ashalmawia.coriolan.data.importer.file.FileParser
import org.junit.Test

import org.junit.Assert.*

class FileParserTest {

    @Test
    fun testParsingCorrectString() {
        val line = "{shrimp} {креветка}"
        testParser(line, "shrimp", "креветка");
    }

    @Test
    fun testParsingCorrectStringWithFlexibleFormat() {
        val line = "{shrimp}               {креветка}"
        testParser(line, "shrimp", "креветка");
    }

    @Test
    fun testParsingExpression() {
        val line = "{Мне нужно идти.} {I should go!}"
        testParser(line, "Мне нужно идти.", "I should go!")
    }

    @Test
    fun testParsingEmptyString() {
        // given
        val line = ""
        val parser = FileParser

        // when
        val card = parser.parseLine(line)

        // then
        assertNull(card)

    }

    @Test
    fun testParsingBlankString() {
        // given
        val line = "       "
        val parser = FileParser

        // when
        val card = parser.parseLine(line)

        // then
        assertNull(card)

    }

    private fun testParser(line: String, expectedOriginal: String, expectedTranslation: String) {
        // given
        val parser = FileParser

        // when
        val card = parser.parseLine(line)

        // then
        assertNotNull(card)
        assertEquals(expectedOriginal, card!!.original)
        assertEquals(expectedTranslation, card.translation)
    }
}