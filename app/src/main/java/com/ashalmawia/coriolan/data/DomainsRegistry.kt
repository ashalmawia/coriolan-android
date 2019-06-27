package com.ashalmawia.coriolan.data

import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.Language

interface DomainsRegistry {

    fun createDomain(originalLangName: String, translationsLangName: String): Domain
}

class DomainsRegistryImpl(private val repository: Repository) : DomainsRegistry {

    override fun createDomain(originalLangName: String, translationsLangName: String): Domain {
        val langOriginal = repository.findOrAddLanguage(originalLangName)
        val langTranslations = repository.findOrAddLanguage(translationsLangName)

        return repository.createDomain("", langOriginal, langTranslations)
    }

    private fun Repository.findOrAddLanguage(name: String): Language {
        val found = languageByName(name)
        return found ?: addLanguage(name)
    }
}