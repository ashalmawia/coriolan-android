package com.ashalmawia.coriolan.data.storage.sqlite.contract

import android.content.ContentValues
import android.database.Cursor
import com.ashalmawia.coriolan.model.Language
import com.ashalmawia.coriolan.data.storage.sqlite.long
import com.ashalmawia.coriolan.data.storage.sqlite.stringOrNull
import com.ashalmawia.coriolan.model.DomainId
import com.ashalmawia.coriolan.model.LanguageId
import com.ashalmawia.coriolan.util.asLanguageId

object ContractDomains {

    const val DOMAINS = "Domains"

    const val DOMAINS_ID = "Domains_id"
    const val DOMAINS_NAME = "Domains_Name"
    const val DOMAINS_LANG_ORIGINAL = "Domains_LangOrig"
    const val DOMAINS_LANG_TRANSLATIONS = "Domains_LangTran"
    const val DOMAINS_PAYLOAD = "Domains_Payload"

    private val allColumns = arrayOf(
            DOMAINS_ID,
            DOMAINS_NAME,
            DOMAINS_LANG_ORIGINAL,
            DOMAINS_LANG_TRANSLATIONS,
            DOMAINS_PAYLOAD
    )

    val createQuery = """
        CREATE TABLE $DOMAINS(
            $DOMAINS_ID INTEGER PRIMARY KEY,
            $DOMAINS_NAME TEXT,
            $DOMAINS_LANG_ORIGINAL INTEGER NOT NULL,
            $DOMAINS_LANG_TRANSLATIONS INTEGER NOT NULL,
            $DOMAINS_PAYLOAD TEXT,
            
            FOREIGN KEY ($DOMAINS_LANG_ORIGINAL) REFERENCES ${ContractLanguages.LANGUAGES} (${ContractLanguages.LANGUAGES_ID})
               ON DELETE RESTRICT
               ON UPDATE CASCADE,
            FOREIGN KEY ($DOMAINS_LANG_TRANSLATIONS) REFERENCES ${ContractLanguages.LANGUAGES} (${ContractLanguages.LANGUAGES_ID})
               ON DELETE RESTRICT
               ON UPDATE CASCADE,
               
            UNIQUE ($DOMAINS_LANG_ORIGINAL, $DOMAINS_LANG_TRANSLATIONS)
        );""".trimMargin()

    fun allColumnsDomains(alias: String? = null): String = SqliteUtils.allColumns(allColumns, alias)

    fun Cursor.domainsId(): DomainId { return DomainId(long(DOMAINS_ID)) }
    fun Cursor.domainsName(): String? { return stringOrNull(DOMAINS_NAME) }
    fun Cursor.domainsOriginalLangId(): LanguageId { return long(DOMAINS_LANG_ORIGINAL).asLanguageId() }
    fun Cursor.domainsTranslationsLangId(): LanguageId { return long(DOMAINS_LANG_TRANSLATIONS).asLanguageId() }

    fun createDomainContentValues(name: String?, langOriginal: Language, langTranslations: Language) =
            createDomainContentValues(name, langOriginal.id, langTranslations.id)

    fun createDomainContentValues(name: String?, langOriginalId: LanguageId, langTranslationsId: LanguageId, id: DomainId? = null): ContentValues {
        val cv = ContentValues()
        if (id != null) {
            cv.put(DOMAINS_ID, id.value)
        }
        cv.put(DOMAINS_NAME, name)
        cv.put(DOMAINS_LANG_ORIGINAL, langOriginalId.value)
        cv.put(DOMAINS_LANG_TRANSLATIONS, langTranslationsId.value)
        return cv
    }
}