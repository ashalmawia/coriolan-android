package com.ashalmawia.coriolan.data.storage.sqlite.contract

import android.content.ContentValues
import android.database.Cursor
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.data.storage.sqlite.long
import com.ashalmawia.coriolan.data.storage.sqlite.string
import com.ashalmawia.coriolan.model.DomainId
import com.ashalmawia.coriolan.util.asDomainId

object ContractDecks {

    const val DECKS = "Decks"

    const val DECKS_ID = "Decks_id"
    const val DECKS_NAME = "Decks_Name"
    const val DECKS_DOMAIN_ID = "Decks_Domain"
    const val DECKS_PAYLOAD = "Decks_Payload"

    private val allColumns = arrayOf(
            DECKS_ID,
            DECKS_NAME,
            DECKS_DOMAIN_ID,
            DECKS_PAYLOAD
    )
    fun allColumnsDecks(alias: String? = null) = SqliteUtils.allColumns(allColumns, alias)

    val createQuery = """
        CREATE TABLE $DECKS(
            $DECKS_ID INTEGER PRIMARY KEY,
            $DECKS_NAME TEXT NOT NULL,
            $DECKS_DOMAIN_ID INTEGER NOT NULL,
            $DECKS_PAYLOAD TEXT,
            
            FOREIGN KEY ($DECKS_DOMAIN_ID) REFERENCES ${ContractDomains.DOMAINS} (${ContractDomains.DOMAINS_ID})
               ON DELETE CASCADE
               ON UPDATE CASCADE,
               
            UNIQUE ($DECKS_NAME, $DECKS_DOMAIN_ID)
               ON CONFLICT ABORT
        );""".trimMargin()

    fun Cursor.decksId(): Long { return long(DECKS_ID) }
    fun Cursor.decksName(): String { return string(DECKS_NAME) }
    fun Cursor.decksDomainId(): DomainId { return long(DECKS_DOMAIN_ID).asDomainId() }
    fun Cursor.deck(domain: Domain): Deck {
        return Deck(decksId(), domain, decksName())
    }

    fun createDeckContentValues(domainId: DomainId, name: String, id: Long? = null): ContentValues {
        val cv = ContentValues()
        if (id != null) {
            cv.put(DECKS_ID, id)
        }
        cv.put(DECKS_NAME, name)
        cv.put(DECKS_DOMAIN_ID, domainId.value)
        return cv
    }
}