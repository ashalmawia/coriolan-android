package com.ashalmawia.coriolan.data

import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Domain

object DomainsRegistry {

    private var domain: Domain? = null

    fun preinitialize(repository: Repository) {
        val domains = repository.allDomains()
        if (!domains.isEmpty()) {
            domain = domains[0]
        }
    }

    fun domain(): Domain? {
        return domain
    }

    fun createDomain(repository: Repository, originalLangName: String, translationsLangName: String): Domain? {
        val langOriginal = repository.addLanguage(originalLangName)
        val langTranslations = repository.addLanguage(translationsLangName)
        // todo: do not create if domain like this already exists: https://trello.com/c/81UJeTRp
        val domain = repository.createDomain("", langOriginal, langTranslations)

        if (this.domain == null) {
            this.domain = domain
        }

        return domain
    }
}