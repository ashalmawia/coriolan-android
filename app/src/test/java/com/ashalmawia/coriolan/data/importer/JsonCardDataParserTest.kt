package com.ashalmawia.coriolan.data.importer

import com.ashalmawia.coriolan.data.importer.file.JsonCardDataParser
import com.ashalmawia.coriolan.data.importer.file.ParsingException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class JsonCardDataParserTest {

    private lateinit var parser: JsonCardDataParser

    @Before
    fun before() {
        parser = JsonCardDataParser()
    }

    @Test
    fun `empty string parsed`() {
        testParser("", listOf())
    }

    @Test
    fun `blank string parsed`() {
        testParser("""




        """, listOf())
    }

    @Test
    fun `non-empty string parsed`() {
        testParser(JsonCartDataParserTestData.wordsJson, JsonCartDataParserTestData.wordsExpected)
    }

    @Test
    fun `terms parsed`() {
        testParser(JsonCartDataParserTestData.termsJson, JsonCartDataParserTestData.termsExpected)
    }

    @Test(expected = ParsingException::class)
    fun `test with typo`() {
        parser.parse(JsonCartDataParserTestData.jsonWithTypo)
    }

    @Test(expected = ParsingException::class)
    fun `test no original`() {
        parser.parse(JsonCartDataParserTestData.jsonNoOriginal)
    }

    @Test(expected = ParsingException::class)
    fun `test no translations`() {
        parser.parse(JsonCartDataParserTestData.jsonNoTranslations)
    }

    @Test(expected = ParsingException::class)
    fun `test no root element`() {
        parser.parse(JsonCartDataParserTestData.jsonNoRoot)
    }

    private fun testParser(text: String, expected: List<JsonCardData>) {
        // when
        val cards = parser.parse(text)

        // then
        assertNotNull(cards)
        assertEquals(expected, cards)
    }
}