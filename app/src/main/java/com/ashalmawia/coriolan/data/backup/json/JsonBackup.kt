package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.Backup
import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.backup.SRStateInfo
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.StateType
import com.fasterxml.jackson.core.*
import java.io.InputStream
import java.io.OutputStream

private const val PAGE_SIZE_DEFAULT = 20

private const val FIELD_LANGUAGES = "languages"
private const val FIELD_DOMAINS = "domains"
private const val FIELD_EXPRESSIONS = "expressions"
private const val FIELD_CARDS = "cards"
private const val FIELD_DECKS = "decks"
private const val FIELD_SR_STATES = "sr_state"

class JsonBackup(private val pageSize: Int = PAGE_SIZE_DEFAULT) : Backup {

    override fun create(repository: BackupableRepository, exercises: List<Exercise<*, *>>, stream: OutputStream) {
        write(repository, exercises, stream, JacksonSerializer.instance())
    }

    override fun restoreFrom(stream: InputStream, repository: BackupableRepository) {
        repository.beginTransaction()
        try {
            repository.clearAll()
            restoreFrom(stream, repository, JacksonDeserializer.instance())
            repository.commitTransaction()
        } catch (e: Throwable) {
            repository.rollbackTransaction()
            throw e
        }
    }

    private fun restoreFrom(stream: InputStream, repository: BackupableRepository, deserializer: JacksonDeserializer) {
        val factory = JsonFactory()
        val json = factory.createParser(stream)

        while (json.nextToken() != JsonToken.END_OBJECT) {
            when (json.currentName) {
                FIELD_LANGUAGES -> read(json, deserializer::readLanguage, repository::writeLanguages)
                FIELD_DOMAINS -> read(json, deserializer::readDomain, repository::writeDomains)
                FIELD_EXPRESSIONS -> read(json, deserializer::readExpression, repository::writeExpressions)
                FIELD_CARDS -> read(json, deserializer::readCard, repository::writeCards)
                FIELD_DECKS -> read(json, deserializer::readDeck, repository::writeDecks)
                FIELD_SR_STATES -> readSRStates(json, deserializer::readCardStateSR, repository)
            }
        }
    }

    private fun write(
            repository: BackupableRepository,
            exercises: List<Exercise<*, *>>,
            stream: OutputStream,
            serializer: JacksonSerializer
    ) {
        val factory = JsonFactory()
        val json = factory.createGenerator(stream, JsonEncoding.UTF8)

        json.writeStartObject()

        write(FIELD_LANGUAGES, repository::allLanguages, json, serializer::writeLanguage)
        write(FIELD_DOMAINS, repository::allDomains, json, serializer::writeDomain)
        write(FIELD_EXPRESSIONS, repository::allExpressions, json, serializer::writeExpression)
        write(FIELD_DECKS, repository::allDecks, json, serializer::writeDeck)
        write(FIELD_CARDS, repository::allCards, json, serializer::writeCard)
        writeSRStates(repository, exercises, json, serializer::writeCardStateSR)

        json.writeEndObject()
        json.close()
    }

    private fun <T> write(tag: String, retriever: (Int, Int) -> List<T>, json: JsonGenerator, serializer: (T, JsonGenerator) -> Unit) {
        json.writeFieldName(tag)
        json.writeStartArray()

        var offset = 0

        while (true) {
            val items = retriever.invoke(offset, pageSize)
            if (items.isEmpty()) {
                break
            }

            items.forEach { serializer.invoke(it, json) }
            offset += items.size
        }

        json.writeEndArray()
    }

    private fun <T> read(json: JsonParser, deserializer: (JsonParser) -> T, writer: (List<T>) -> Unit) {
        val items = mutableListOf<T>()
        while (json.nextToken() != JsonToken.END_ARRAY) {
            if (json.currentToken == JsonToken.START_OBJECT) {
                items.add(deserializer.invoke(json))
                if (items.size == pageSize) {
                    writer.invoke(items)
                    items.clear()
                }
            }
        }
        if (items.isNotEmpty()) {
            writer.invoke(items)
        }
    }

    private fun writeSRStates(
            repository: BackupableRepository,
            exercises: List<Exercise<*, *>>,
            json: JsonGenerator,
            serializer: (SRStateInfo, JsonGenerator) -> Unit
    ) {
        json.writeFieldName(FIELD_SR_STATES)
        json.writeStartObject()

        exercises.filter { it.stateType == StateType.SR_STATE }.forEach {
            write(it.stableId, { offset, limit -> repository.allSRStates(it.stableId, offset, limit) }, json, serializer)
        }

        json.writeEndObject()
    }

    private fun readSRStates(
            json: JsonParser,
            deserializer: (JsonParser) -> SRStateInfo,
            repository: BackupableRepository
    ) {
        json.nextToken() // "{"

        while (json.nextToken() != JsonToken.END_OBJECT) {
            val exerciseId = json.currentName
            read(json, deserializer, { list -> repository.writeSRStates(exerciseId, list) })
        }
    }
}

private operator fun Regex.contains(regex: String): Boolean = matches(regex)