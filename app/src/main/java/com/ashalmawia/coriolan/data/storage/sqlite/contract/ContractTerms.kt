package com.ashalmawia.coriolan.data.storage.sqlite.contract

import android.content.ContentValues
import android.database.Cursor
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractLanguages.language
import com.ashalmawia.coriolan.model.Language
import com.ashalmawia.coriolan.model.Term
import com.ashalmawia.coriolan.data.storage.sqlite.long
import com.ashalmawia.coriolan.data.storage.sqlite.payload.TermPayload
import com.ashalmawia.coriolan.data.storage.sqlite.string
import com.ashalmawia.coriolan.data.storage.sqlite.stringOrNull
import com.ashalmawia.coriolan.model.LanguageId
import com.ashalmawia.coriolan.model.TermId
import com.ashalmawia.coriolan.util.asLanguageId
import com.ashalmawia.coriolan.util.asTermId
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object ContractTerms {

    private val objectMapper = jacksonObjectMapper()

    const val TERMS = "Terms"

    const val TERMS_ID = "Terms_id"
    const val TERMS_VALUE = "Terms_Value"
    const val TERMS_LANGUAGE_ID = "Terms_Lang"
    const val TERMS_PAYLOAD = "Terms_Payload"

    private val allColumns = arrayOf(
            TERMS_ID,
            TERMS_VALUE,
            TERMS_LANGUAGE_ID,
            TERMS_PAYLOAD
    )
    fun allColumnsTerms(alias: String? = null) = SqliteUtils.allColumns(allColumns, alias)

    val createQuery = """
        CREATE TABLE $TERMS(
            $TERMS_ID INTEGER PRIMARY KEY,
            $TERMS_VALUE TEXT NOT NULL,
            $TERMS_LANGUAGE_ID INTEGER NOT NULL,
            $TERMS_PAYLOAD TEXT,
            
            FOREIGN KEY ($TERMS_LANGUAGE_ID) REFERENCES ${ContractLanguages.LANGUAGES} (${ContractLanguages.LANGUAGES_ID})
               ON DELETE RESTRICT
               ON UPDATE CASCADE,
               
            UNIQUE ($TERMS_VALUE, $TERMS_LANGUAGE_ID)
               ON CONFLICT ABORT
        );""".trimMargin()


    fun Cursor.termsId(): TermId { return long(TERMS_ID).asTermId() }
    fun Cursor.termsValue(): String { return string(TERMS_VALUE) }
    fun Cursor.termsLanguageId(): LanguageId { return long(TERMS_LANGUAGE_ID).asLanguageId() }
    fun Cursor.termsPayload(): TermPayload {
        val serialized = stringOrNull(TERMS_PAYLOAD)
        return deserialize(serialized)
    }
    fun Cursor.term(): Term {
        return Term(
                termsId(),
                termsValue(),
                language(),
                termsPayload().transcription
        )
    }


    fun createTermContentValues(value: String, language: Language, payload: TermPayload) =
            createTermContentValues(value, language.id, payload)

    fun createTermContentValues(value: String, languageId: LanguageId, payload: TermPayload, id: TermId? = null): ContentValues {
        val cv = ContentValues()
        if (id != null) {
            cv.put(TERMS_ID, id.value)
        }
        cv.put(TERMS_VALUE, value)
        cv.put(TERMS_PAYLOAD, serialize(payload))
        cv.put(TERMS_LANGUAGE_ID, languageId.value)
        return cv
    }

    private fun serialize(payload: TermPayload): String {
        return payload.run { objectMapper.writeValueAsString(payload) }
    }

    private fun deserialize(value: String?): TermPayload {
        return if (value.isNullOrBlank()) {
            TermPayload(null)
        } else {
            objectMapper.readValue(value, TermPayload::class.java)
        }
    }

    fun createTermPayload(transcription: String?) = TermPayload(transcription)
}