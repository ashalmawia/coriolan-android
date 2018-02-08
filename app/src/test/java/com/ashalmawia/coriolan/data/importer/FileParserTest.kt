package com.ashalmawia.coriolan.data.importer

import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.LanguagesRegistry
import com.ashalmawia.coriolan.data.importer.file.FileParser
import com.ashalmawia.coriolan.data.importer.file.ParsingException
import com.ashalmawia.coriolan.model.mockDeck
import com.ashalmawia.coriolan.model.mockLanguage
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.io.File

@RunWith(JUnit4::class)
class FileParserTest {

    private lateinit var parser: FileParser

    @Before
    fun before() {
        val mockDecksRegistry = mock(DecksRegistry::class.java)
        `when`(mockDecksRegistry.default()).thenReturn(mockDeck("Default"))

        val mockLangsRegistry = mock(LanguagesRegistry::class.java)
        `when`(mockLangsRegistry.original()).thenReturn(mockLanguage(value = "English"))
        `when`(mockLangsRegistry.translations()).thenReturn(mockLanguage(value = "Nederlands"))

        parser = FileParser(mockDecksRegistry, mockLangsRegistry)
    }

    @Test
    fun testParsingCorrectString() {
        val line = "{shrimp} {креветка}"
        testParser(line, "shrimp", listOf("креветка"))
    }

    @Test
    fun testParsingCorrectStringWithFlexibleFormat() {
        val line = "{shrimp}               {креветка}"
        testParser(line, "shrimp", listOf("креветка"))
    }

    @Test
    fun testParsingExpression() {
        val line = "{Мне нужно идти.} {I should go!}"
        testParser(line, "Мне нужно идти.", listOf("I should go!"))
    }

    @Test
    fun testParsingWithMultipleTranslations() {
        val line = "{нравиться} {like|please|appeal|fancy}"
        testParser(line, "нравиться", listOf(
                "like", "please", "appeal", "fancy"
        ))
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

    @Test(expected = ParsingException::class)
    fun testWrongFormatTooManyEntries() {
        val line = "{нравиться} {like} {please}"
        parser.parseLine(line)
    }

    @Test(expected = ParsingException::class)
    fun testWrongFormatTooFewEntries() {
        val line = "{нравиться}"
        parser.parseLine(line)
    }

    @Test(expected = ParsingException::class)
    fun testWrongFormatWrongFormatOriginal() {
        val line = "нравиться {like|please|appeal|fancy}"
        parser.parseLine(line)
    }

    @Test(expected = ParsingException::class)
    fun testWrongFormatWrongFormatTranslations() {
        val line = "{нравиться} like|please|appeal|fancy"
        parser.parseLine(line)
    }

    @Test
    fun testParseFile() {
        // given
        val url = javaClass.classLoader.getResource("import_from_file")
        val file = File(url.path)

        // when
        val result = parser.parseFile(file)

        // then
        assertEquals("number of cards is correct", 4, result.size)
        verifyCard("summer", listOf("лето"), result[0])
        verifyCard("winter", listOf("зима"), result[1])
        verifyCard("autumn", listOf("осень"), result[2])
        verifyCard("spring", listOf("весна", "источник"), result[3])
    }

    private fun testParser(line: String, expectedOriginal: String, expectedTranslations: List<String>) {
        // when
        val card = parser.parseLine(line)

        // then
        assertNotNull("card is created", card)
        verifyCard(expectedOriginal, expectedTranslations, card)
    }

    private fun verifyCard(expectedOriginal: String, expectedTranslations: List<String>, result: CardData?) {
        assertEquals("original is correct", expectedOriginal, result!!.original)
        assertEquals("number of translations is correct", expectedTranslations.size, result.translations.size)
        for (i in 0 until expectedTranslations.size) {
            assertEquals("translations are correct", expectedTranslations[i], result.translations[i])
        }
    }
}