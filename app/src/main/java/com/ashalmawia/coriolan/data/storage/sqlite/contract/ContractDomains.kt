package com.ashalmawia.coriolan.data.storage.sqlite.contract

import android.content.ContentValues
import android.database.Cursor
import com.ashalmawia.coriolan.model.Language
import com.ashalmawia.coriolan.data.storage.sqlite.long
import com.ashalmawia.coriolan.data.storage.sqlite.stringOrNull

object ContractDomains {

    const val DOMAINS = "Domains"

    const val DOMAINS_ID = "Domains_id"
    const val DOMAINS_NAME = "Domains_Name"
    const val DOMAINS_LANG_ORIGINAL = "Domains_LangOrig"
    const val DOMAINS_LANG_TRANSLATIONS = "Domains_LangTran"

    private val allColumns = arrayOf(
            DOMAINS_ID,
            DOMAINS_NAME,
            DOMAINS_LANG_ORIGINAL,
            DOMAINS_LANG_TRANSLATIONS
    )

    fun allColumnsDomains(alias: String? = null): String = SqliteUtils.allColumns(allColumns, alias)

    fun Cursor.domainsId(): Long { return long(DOMAINS_ID) }
    fun Cursor.domainsName(): String? { return stringOrNull(DOMAINS_NAME) }
    fun Cursor.domainsOriginalLangId(): Long { return long(DOMAINS_LANG_ORIGINAL) }
    fun Cursor.domainsTranslationsLangId(): Long { return long(DOMAINS_LANG_TRANSLATIONS) }

    fun createDomainContentValues(name: String?, langOriginal: Language, langTranslations: Language) =
            createDomainContentValues(name, langOriginal.id, langTranslations.id)

    fun createDomainContentValues(name: String?, langOriginalId: Long, langTranslationsId: Long, id: Long? = null): ContentValues {
        val cv = ContentValues()
        if (id != null) {
            cv.put(DOMAINS_ID, id)
        }
        cv.put(DOMAINS_NAME, name)
        cv.put(DOMAINS_LANG_ORIGINAL, langOriginalId)
        cv.put(DOMAINS_LANG_TRANSLATIONS, langTranslationsId)
        return cv
    }
}