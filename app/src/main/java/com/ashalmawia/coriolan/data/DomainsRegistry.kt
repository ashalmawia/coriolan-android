package com.ashalmawia.coriolan.data

import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.Language

interface DomainsRegistry {

    fun createDomain(originalLangName: String, translationsLangName: String, defaultDeckName: String): Pair<Domain, Deck>
    fun updateDomain(domain: Domain, originalLangName: String, translationsLangName: String): Domain
    fun deleteDomain(domain: Domain)
}

class DomainsRegistryImpl(private val repository: Repository, private val decksRegistry: DecksRegistry) : DomainsRegistry {

    override fun createDomain(
            originalLangName: String, translationsLangName: String, defaultDeckName: String
    ): Pair<Domain, Deck> {
        val langOriginal = repository.findOrAddLanguage(originalLangName)
        val langTranslations = repository.findOrAddLanguage(translationsLangName)

        val domain = repository.createDomain("", langOriginal, langTranslations)
        val deck = addDefaultDeck(domain, defaultDeckName)
        return Pair(domain, deck)
    }

    override fun updateDomain(domain: Domain, originalLangName: String, translationsLangName: String): Domain {
        var langOriginal = domain.langOriginal()
        if (langOriginal.value != originalLangName) {
            langOriginal = repository.updateLanguage(langOriginal, originalLangName)
        }
        var langTranslation = domain.langTranslations()
        if (langTranslation.value != translationsLangName) {
            langTranslation = repository.updateLanguage(langTranslation, translationsLangName)
        }
        return domain.copy(langOriginal = langOriginal, langTranslations = langTranslation)
    }

    private fun addDefaultDeck(domain: Domain, defaultDeckName: String): Deck {
        return repository.addDeck(domain, defaultDeckName)
    }

    override fun deleteDomain(domain: Domain) {
        val domainCards = repository.allCards(domain)
        val allDomainTerms = domainCards.flatMap { it.translations.plus(it.original) }
        repository.deleteDomain(domain.id)
        decksRegistry.deleteOrphanTerms(allDomainTerms)
    }

    private fun Repository.findOrAddLanguage(name: String): Language {
        val found = languageByName(name)
        return found ?: addLanguage(name)
    }
}