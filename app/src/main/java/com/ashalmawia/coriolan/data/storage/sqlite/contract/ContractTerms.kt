package com.ashalmawia.coriolan.data.storage.sqlite.contract

import android.content.ContentValues
import android.database.Cursor
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractLanguages.language
import com.ashalmawia.coriolan.model.Extras
import com.ashalmawia.coriolan.model.Language
import com.ashalmawia.coriolan.model.Term
import com.ashalmawia.coriolan.data.storage.sqlite.long
import com.ashalmawia.coriolan.data.storage.sqlite.string
import com.ashalmawia.coriolan.data.storage.sqlite.stringOrNull
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object ContractTerms {

    private val objectMapper = jacksonObjectMapper()

    const val TERMS = "Terms"

    const val TERMS_ID = "Terms_id"
    const val TERMS_VALUE = "Terms_Value"
    const val TERMS_LANGUAGE_ID = "Terms_Lang"
    const val TERMS_EXTRAS = "Terms_Extras"

    private val allColumns = arrayOf(
            TERMS_ID,
            TERMS_VALUE,
            TERMS_LANGUAGE_ID,
            TERMS_EXTRAS
    )
    fun allColumnsTerms(alias: String? = null) = SqliteUtils.allColumns(allColumns, alias)

    val createQuery = """
        CREATE TABLE $TERMS(
            $TERMS_ID INTEGER PRIMARY KEY,
            $TERMS_VALUE TEXT NOT NULL,
            $TERMS_LANGUAGE_ID INTEGER NOT NULL,
            $TERMS_EXTRAS TEXT,
            
            FOREIGN KEY ($TERMS_LANGUAGE_ID) REFERENCES ${ContractLanguages.LANGUAGES} (${ContractLanguages.LANGUAGES_ID})
               ON DELETE RESTRICT
               ON UPDATE CASCADE,
               
            UNIQUE ($TERMS_VALUE, $TERMS_LANGUAGE_ID)
               ON CONFLICT ABORT
        );""".trimMargin()


    fun Cursor.termsId(): Long { return long(TERMS_ID) }
    fun Cursor.termsValue(): String { return string(TERMS_VALUE) }
    fun Cursor.termsLanguageId(): Long { return long(TERMS_LANGUAGE_ID) }
    fun Cursor.termsExtras(): Extras {
        val serialized = stringOrNull(TERMS_EXTRAS)
        return deserialize(serialized)
    }
    fun Cursor.term(): Term {
        return Term(
                termsId(),
                termsValue(),
                language(),
                termsExtras()
        )
    }


    fun createTermContentValues(value: String, language: Language, extras: Extras?) = createTermContentValues(value, language.id, extras)

    fun createTermContentValues(value: String, languageId: Long, extras: Extras?, id: Long? = null): ContentValues {
        val cv = ContentValues()
        if (id != null) {
            cv.put(TERMS_ID, id)
        }
        cv.put(TERMS_VALUE, value)
        cv.put(TERMS_EXTRAS, serialize(extras))
        cv.put(TERMS_LANGUAGE_ID, languageId)
        return cv
    }

    private fun serialize(extras: Extras?): String? {
        return extras?.run { objectMapper.writeValueAsString(extras) }
    }

    private fun deserialize(value: String?): Extras {
        return if (value.isNullOrBlank()) {
            Extras.empty()
        } else {
            objectMapper.readValue(value, Extras::class.java)
        }
    }
}