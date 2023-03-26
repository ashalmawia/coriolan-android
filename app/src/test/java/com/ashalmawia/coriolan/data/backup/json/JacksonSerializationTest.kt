package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.*
import com.ashalmawia.coriolan.learning.mockToday
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@RunWith(JUnit4::class)
class JacksonSerializationTest {

    private val serializer: JacksonSerializer = JacksonSerializerImpl()
    private val deserializer: JacksonDeserializer = JacksonDeserializerImpl()

    private val today = mockToday()

    @Test
    fun testLanguageInfo() {
        // given
        val info = LanguageInfo(7L, "some value")

        // then
        test(info, serializer::writeLanguage, deserializer::readLanguage)
    }

    @Test
    fun testDomainInfo() {
        // given
        val info = DomainInfo(333L, "My cool domain", 5L, 13L)

        // then
        test(info, serializer::writeDomain, deserializer::readDomain)
    }

    @Test
    fun testTermInfo() {
        // given
        val info = TermInfo(13L, "My term value.", 5L)

        // then
        test(info, serializer::writeTerm, deserializer::readTerm)
    }

    @Test
    fun testCardInfo() {
        // given
        val info = CardInfo(8L, 17L, 2L, 888L, listOf(55L, 31L, 3L))

        // then
        test(info, serializer::writeCard, deserializer::readCard)
    }

    @Test
    fun testDeckInfo() {
        // given
        val info = DeckInfo(57L, 3L, "some name")

        // then
        test(info, serializer::writeDeck, deserializer::readDeck)
    }

    @Test
    fun testSRStateInfo() {
        // given
        val info = CardStateInfo(23L, today.minusDays(5), 7)

        // then
        test(info, serializer::writeCardState, deserializer::readCardStateSR)
    }
}

private fun <T> test(info: T, writer: (T, JsonGenerator) -> Unit, reader: (JsonParser) -> T) {
    // when
    val output = ByteArrayOutputStream()

    val jsonFactory = JsonFactory()
    val jsonGenerator = jsonFactory.createGenerator(output)

    // when
    writer(info, jsonGenerator)

    jsonGenerator.close()

    // given
    val input = ByteArrayInputStream(output.toByteArray())
    val jsonParser = jsonFactory.createParser(input)

    // when
    val read = reader(jsonParser)

    jsonParser.close()

    // then
    assertEquals(info, read)
}