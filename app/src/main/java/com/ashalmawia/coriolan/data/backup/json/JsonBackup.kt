package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.Backup
import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.backup.CardStateInfo
import com.ashalmawia.coriolan.data.backup.TermInfo
import com.ashalmawia.coriolan.model.Extras
import com.fasterxml.jackson.core.*
import java.io.InputStream
import java.io.OutputStream
import java.lang.IllegalArgumentException

private const val PAGE_SIZE_DEFAULT = 20

private const val FIELD_LANGUAGES = "languages"
private const val FIELD_DOMAINS = "domains"
private const val FIELD_TERMS = "terms"
private const val FIELD_TERMS_LEGACY = "expressions"
private const val FIELD_TERM_EXTRAS_LEGACY = "expression_extras"
private const val FIELD_CARDS = "cards"
private const val FIELD_DECKS = "decks"
private const val FIELD_CARD_STATES = "card_states"
private const val FIELD_CARD_STATES_LEGACY = "sr_state"

class JsonBackup(private val pageSize: Int = PAGE_SIZE_DEFAULT) : Backup {

    override fun create(repository: BackupableRepository, stream: OutputStream) {
        write(repository, stream, JacksonSerializer.instance())
    }

    override fun restoreFrom(stream: InputStream, repository: BackupableRepository) {
        repository.overrideRepositoryData {
            restoreFrom(stream, repository, JacksonDeserializer.instance())
        }
    }

    private fun restoreFrom(stream: InputStream, repository: BackupableRepository, deserializer: JacksonDeserializer) {
        val factory = JsonFactory()
        val json = factory.createParser(stream)
        var isEmpty = true

        val terms = mutableListOf<TermInfo>()
        val legacyExtras = mutableMapOf<Long, Extras>()

        while (true) {
            val token = json.nextToken()
            if (token == null || token == JsonToken.END_OBJECT) break

            isEmpty = false

            when (json.currentName) {
                FIELD_LANGUAGES -> read(json, deserializer::readLanguage, repository::writeLanguages)
                FIELD_DOMAINS -> read(json, deserializer::readDomain, repository::writeDomains)
                FIELD_TERMS, FIELD_TERMS_LEGACY -> read(json, deserializer::readTerm) {
                    terms.addAll(it)
                }
                FIELD_TERM_EXTRAS_LEGACY -> read(json, deserializer::readTermExtra) { list ->
                    legacyExtras.putAll(
                            list.associateBy { it.termId }.mapValues { (_, value) -> Extras(value.value) }
                    )
                }
                FIELD_CARDS -> read(json, deserializer::readCard, repository::writeCards)
                FIELD_DECKS -> read(json, deserializer::readDeck, repository::writeDecks)
                FIELD_CARD_STATES, FIELD_CARD_STATES_LEGACY -> readSRStates(json, deserializer::readCardStateSR, repository)
            }
        }

        if (isEmpty) {
            throw IllegalArgumentException("backup stream was empty, nothing to restore")
        }

        writeTerms(repository, terms, legacyExtras)
    }

    private fun writeTerms(repository: BackupableRepository, terms: List<TermInfo>, legacyExtras: Map<Long, Extras>) {
        if (legacyExtras.isEmpty()) {
            repository.writeTerms(terms)
        } else {
            val withExtras = terms.map { it.copy(extras = legacyExtras[it.id] ?: Extras.empty()) }
            repository.writeTerms(withExtras)
        }
    }

    private fun write(
            repository: BackupableRepository,
            stream: OutputStream,
            serializer: JacksonSerializer
    ) {
        val factory = JsonFactory()
        val json = factory.createGenerator(stream, JsonEncoding.UTF8)

        json.writeStartObject()

        write(FIELD_LANGUAGES, repository::allLanguages, json, serializer::writeLanguage)
        write(FIELD_DOMAINS, repository::allDomains, json, serializer::writeDomain)
        write(FIELD_TERMS, repository::allTerms, json, serializer::writeTerm)
        write(FIELD_DECKS, repository::allDecks, json, serializer::writeDeck)
        write(FIELD_CARDS, repository::allCards, json, serializer::writeCard)
        writeSRStates(repository, json, serializer::writeCardState)

        json.writeEndObject()
        json.close()
    }

    private fun <T> write(tag: String, retriever: (Int, Int) -> List<T>, json: JsonGenerator, serializer: (T, JsonGenerator) -> Unit) {
        json.writeFieldName(tag)
        json.writeStartArray()

        var offset = 0

        while (true) {
            val items = retriever(offset, pageSize)
            if (items.isEmpty()) {
                break
            }

            items.forEach { serializer(it, json) }
            offset += items.size
        }

        json.writeEndArray()
    }

    private fun <T> read(json: JsonParser, deserializer: (JsonParser) -> T, writer: (List<T>) -> Unit) {
        val items = mutableListOf<T>()
        while (json.nextToken() != JsonToken.END_ARRAY) {
            if (json.currentToken == JsonToken.START_OBJECT) {
                items.add(deserializer(json))
                if (items.size == pageSize) {
                    writer(items)
                    items.clear()
                }
            }
        }
        if (items.isNotEmpty()) {
            writer(items)
        }
    }

    private fun writeSRStates(
            repository: BackupableRepository,
            json: JsonGenerator,
            serializer: (CardStateInfo, JsonGenerator) -> Unit
    ) {
        json.writeFieldName(FIELD_CARD_STATES)
        json.writeStartObject()

        write("dummy", { offset, limit -> repository.allCardStates(offset, limit) }, json, serializer)

        json.writeEndObject()
    }

    private fun readSRStates(
            json: JsonParser,
            deserializer: (JsonParser) -> CardStateInfo,
            repository: BackupableRepository
    ) {
        json.nextToken() // "{"

        while (json.nextToken() != JsonToken.END_OBJECT) {
            read(json, deserializer) { list -> repository.writeCardStates(list) }
        }
    }
}

private operator fun Regex.contains(regex: String): Boolean = matches(regex)