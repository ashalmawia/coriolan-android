package com.ashalmawia.coriolan.data

import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.Language

object DomainsRegistry {

    private var domain: Domain? = null

    fun preinitialize(repository: Repository) {
        val domains = repository.allDomains()
        if (!domains.isEmpty()) {
            domain = domains[0]
        }
    }

    fun domain(): Domain {
        return domainIfExists() ?: throw IllegalStateException("domain expected to have beeen initialized was not")
    }

    fun domainIfExists(): Domain? {
        return domain
    }

    fun setCurrentDomain(domain: Domain) {
        this.domain = domain
    }

    fun createDomain(repository: Repository, originalLangName: String, translationsLangName: String): Domain {
        val langOriginal = repository.findOrAddLanguage(originalLangName)
        val langTranslations = repository.findOrAddLanguage(translationsLangName)

        val domain = repository.createDomain("", langOriginal, langTranslations)

        if (this.domain == null) {
            this.domain = domain
        }

        return domain
    }

    private fun Repository.findOrAddLanguage(name: String): Language {
        val found = languageByName(name)
        return found ?: addLanguage(name)
    }
}