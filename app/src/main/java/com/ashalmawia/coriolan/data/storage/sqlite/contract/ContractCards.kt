package com.ashalmawia.coriolan.data.storage.sqlite.contract

import android.content.ContentValues
import android.database.Cursor
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.term
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.Term
import com.ashalmawia.coriolan.data.storage.sqlite.long
import com.ashalmawia.coriolan.data.storage.sqlite.payload.CardPayload
import com.ashalmawia.coriolan.data.storage.sqlite.payload.TermId
import com.ashalmawia.coriolan.data.storage.sqlite.string
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object ContractCards {

    const val CARDS = "Cards"

    const val CARDS_ID = "Cards_id"
    const val CARDS_FRONT_ID = "Cards_FrontId"
    const val CARDS_DECK_ID = "Cards_DeckId"
    const val CARDS_DOMAIN_ID = "Cards_DomainId"
    const val CARDS_TYPE = "Cards_Type"
    const val CARDS_PAYLOAD = "Cards_Payload"


    private val allColumns = arrayOf(
            CARDS_ID,
            CARDS_FRONT_ID,
            CARDS_DECK_ID,
            CARDS_DOMAIN_ID,
            CARDS_TYPE,
            CARDS_PAYLOAD
    )
    fun allColumnsCards(alias: String? = null) = SqliteUtils.allColumns(allColumns, alias)

    val createQuery = """
        CREATE TABLE $CARDS(
            $CARDS_ID INTEGER PRIMARY KEY,
            $CARDS_FRONT_ID INTEGER NOT NULL,
            $CARDS_DECK_ID INTEGER NOT NULL,
            $CARDS_DOMAIN_ID INTEGER NOT NULL,
            $CARDS_TYPE TEXT NOT NULL,
            $CARDS_PAYLOAD TEXT,
            
            FOREIGN KEY ($CARDS_FRONT_ID) REFERENCES ${ContractTerms.TERMS} (${ContractTerms.TERMS_ID})
               ON DELETE RESTRICT
               ON UPDATE CASCADE,
            FOREIGN KEY ($CARDS_DECK_ID) REFERENCES ${ContractDecks.DECKS} (${ContractDecks.DECKS_ID})
               ON DELETE RESTRICT
               ON UPDATE CASCADE,
            FOREIGN KEY ($CARDS_DOMAIN_ID) REFERENCES ${ContractDomains.DOMAINS} (${ContractDomains.DOMAINS_ID})
               ON DELETE CASCADE
               ON UPDATE CASCADE
        );
        
        CREATE INDEX idx_$CARDS_TYPE
        ON $CARDS ($CARDS_TYPE);
        
        """.trimMargin()


    private val objectMapper = jacksonObjectMapper()
    fun Cursor.cardsId(): Long { return long(CARDS_ID) }
    fun Cursor.cardsFrontId(): Long { return long(CARDS_FRONT_ID) }
    fun Cursor.cardsDeckId(): Long { return long(CARDS_DECK_ID) }
    fun Cursor.cardsDomainId(): Long { return long(CARDS_DOMAIN_ID) }
    fun Cursor.cardsCardType(): CardType {
        val type = string(CARDS_TYPE)
        return CardType.fromValue(type)
    }
    fun Cursor.cardsPayload(): CardPayload {
        val value = string(CARDS_PAYLOAD)
        return objectMapper.readValue(value, CardPayload::class.java)
    }
    fun Cursor.cardWihoutTranslations(domain: Domain): Card {
        val id = cardsId()
        return Card(
                id,
                cardsDeckId(),
                domain,
                cardsCardType(),
                term(),
                emptyList()
        )
    }


    fun createCardContentValues(domainId: Long, deckId: Long, original: Term, cardType: CardType, cardPayload: CardPayload, cardId: Long? = null) =
            createCardContentValues(domainId, deckId, original.id, cardType, cardPayload, cardId)

    fun createCardContentValues(domainId: Long, deckId: Long, originalId: Long, cardType: CardType, cardPayload: CardPayload, cardId: Long? = null): ContentValues {
        val cv = ContentValues()
        if (cardId != null) {
            cv.put(CARDS_ID, cardId)
        }
        cv.put(CARDS_FRONT_ID, originalId)
        cv.put(CARDS_DECK_ID, deckId)
        cv.put(CARDS_DOMAIN_ID, domainId)
        cv.put(CARDS_TYPE, cardType.value)
        cv.put(CARDS_PAYLOAD, objectMapper.writeValueAsString(cardPayload))
        return cv
    }

    @JvmName("createCardPayloadTerm")
    fun createCardPayload(translations: List<Term>): CardPayload {
        return CardPayload(translations.map { TermId(it.id) })
    }
    @JvmName("createCardPayloadLong")
    fun createCardPayload(translations: List<Long>): CardPayload {
        return CardPayload(translations.map { TermId(it) })
    }
}