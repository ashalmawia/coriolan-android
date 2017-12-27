package com.ashalmawia.coriolan.data.importer

import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.importer.file.FileParser
import com.ashalmawia.coriolan.model.Deck
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class FileParserTest {

    private lateinit var parser: FileParser

    @Before
    fun before() {
        val mockRegistry = mock(DecksRegistry::class.java)
        `when`(mockRegistry.default()).thenReturn(Deck(1, "Default", listOf()))
        parser = FileParser(mockRegistry)
    }

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

        // when
        val card = parser.parseLine(line)

        // then
        assertNull(card)

    }

    @Test
    fun testParsingBlankString() {
        // given
        val line = "       "

        // when
        val card = parser.parseLine(line)

        // then
        assertNull(card)

    }

    private fun testParser(line: String, expectedOriginal: String, expectedTranslation: String) {
        // when
        val card = parser.parseLine(line)

        // then
        assertNotNull(card)
        assertEquals(expectedOriginal, card!!.original)
        assertEquals(expectedTranslation, card.translation)
    }
}