package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.Backup
import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.backup.CardInfo
import com.ashalmawia.coriolan.data.backup.DomainInfo
import com.ashalmawia.coriolan.data.backup.TermExtraInfo
import com.ashalmawia.coriolan.data.backup.TermInfo
import com.ashalmawia.coriolan.data.logbook.BackupableLogbook
import com.ashalmawia.coriolan.model.CardType
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
private const val FIELD_LOGBOOK = "logbook"

class JsonBackup(private val pageSize: Int = PAGE_SIZE_DEFAULT) : Backup {

    override fun create(repository: BackupableRepository, logbook: BackupableLogbook, stream: OutputStream) {
        write(repository, logbook, stream, JacksonSerializer.instance())
    }

    override fun restoreFrom(stream: InputStream, repository: BackupableRepository, logbook: BackupableLogbook) {
        repository.beginTransaction()
        logbook.beginTransaction()

        try {
            repository.dropAllData()
            logbook.dropAllData()

            restoreFrom(stream, repository, logbook, JacksonDeserializer.instance())

            repository.setTransactionSuccessful()
            logbook.setTransactionSuccessful()
        } finally {
            logbook.endTransaction()
            repository.endTransaction()
        }
    }

    private fun restoreFrom(
            stream: InputStream,
            repository: BackupableRepository,
            logbook: BackupableLogbook,
            deserializer: JacksonDeserializer
    ) {
        val factory = JsonFactory()
        val json = factory.createParser(stream)
        var isEmpty = true

        val domains = mutableMapOf<Long, DomainInfo>()
        val terms = mutableMapOf<Long, TermInfo>()
        val cards = mutableListOf<CardInfo>()
        val legacyExtras = mutableMapOf<Long, TermExtraInfo>()

        while (true) {
            val token = json.nextToken()
            if (token == null || token == JsonToken.END_OBJECT) break

            isEmpty = false

            when (json.currentName) {
                FIELD_LANGUAGES -> read(json, deserializer::readLanguage, repository::writeLanguages)
                FIELD_DOMAINS -> read(json, deserializer::readDomain) {
                    domains.putAll(it.associateBy { it.id })
                    repository.writeDomains(it)
                }
                FIELD_TERMS, FIELD_TERMS_LEGACY -> read(json, deserializer::readTerm) { list ->
                    terms.putAll(list.associateBy { it.id })
                }
                FIELD_TERM_EXTRAS_LEGACY -> read(json, deserializer::readTermExtra) { list ->
                    legacyExtras.putAll(list.associateBy { it.termId })
                }
                FIELD_CARDS -> read(json, deserializer::readCard) { list ->
                    cards.addAll(list)
                }
                FIELD_DECKS -> read(json, deserializer::readDeck, repository::writeDecks)
                FIELD_CARD_STATES, FIELD_CARD_STATES_LEGACY -> read(json, deserializer::readExerciseState, repository::writeExerciseStates)
                FIELD_LOGBOOK -> read(json, deserializer::readLogbookEntry, logbook::overrideAllData)
            }
        }

        if (isEmpty) {
            throw IllegalArgumentException("backup stream was empty, nothing to restore")
        }

        writeTerms(repository, terms.values.toList(), legacyExtras)

        cards.forEach { it.cardType = resolveCardType(it, terms, domains) }
        repository.writeCards(cards)
    }

    private fun writeTerms(repository: BackupableRepository, terms: List<TermInfo>, legacyExtras: Map<Long, TermExtraInfo>) {
        if (legacyExtras.isEmpty()) {
            repository.writeTerms(terms)
        } else {
            val withExtras = terms.map { it.copy(transcription = legacyExtras[it.id]?.value) }
            repository.writeTerms(withExtras)
        }
    }

    private fun write(
            repository: BackupableRepository,
            logbook: BackupableLogbook,
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
        write(FIELD_CARD_STATES, repository::allExerciseStates, json, serializer::writeCardState)
        write(FIELD_LOGBOOK, logbook::exportAllData, json, serializer::writeLogbookEntry)

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
}

private operator fun Regex.contains(regex: String): Boolean = matches(regex)

private fun resolveCardType(cardInfo: CardInfo, terms: Map<Long, TermInfo>, domains: Map<Long, DomainInfo>): CardType {
    val domain = domains[cardInfo.domainId]!!
    val original = terms[cardInfo.originalId]!!
    return if (domain.origLangId == original.languageId) CardType.FORWARD else CardType.REVERSE
}