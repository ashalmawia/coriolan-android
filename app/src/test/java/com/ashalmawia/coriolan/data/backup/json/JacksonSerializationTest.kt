package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.*
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.util.asCardId
import com.ashalmawia.coriolan.util.asTermId
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
        val info = domainInfo(333L, "My cool domain", 5L, 13L)

        // then
        test(info, serializer::writeDomain, deserializer::readDomain)
    }

    @Test
    fun testTermInfo() {
        // given
        val info = TermInfo(13L.asTermId(), "march", 5L, "/mɑːtʃ \$ mɑːrtʃ/")

        // then
        test(info, serializer::writeTerm, deserializer::readTerm)
    }

    @Test
    fun testCardInfo() {
        // given
        val info = cardInfo(8L, 17L, 2L, 888L, listOf(55L, 31L, 3L), null)

        // then
        test(info, serializer::writeCard, deserializer::readCard)
    }

    @Test
    fun testDeckInfo() {
        // given
        val info = deckInfo(57L, 3L, "some name")

        // then
        test(info, serializer::writeDeck, deserializer::readDeck)
    }

    @Test
    fun testExerciseStateInfo() {
        // given
        val info = LearningProgressInfo(23L.asCardId(), today.minusDays(5), 7)

        // then
        test(info, serializer::writeCardState, deserializer::readExerciseState)
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