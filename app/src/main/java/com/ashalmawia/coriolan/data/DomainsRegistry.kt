package com.ashalmawia.coriolan.data

import android.content.Context
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.Language

interface DomainsRegistry {

    fun createDomain(context: Context, originalLangName: String, translationsLangName: String): Pair<Domain, Deck>
}

class DomainsRegistryImpl(private val repository: Repository) : DomainsRegistry {

    override fun createDomain(
            context: Context, originalLangName: String, translationsLangName: String
    ): Pair<Domain, Deck> {
        val langOriginal = repository.findOrAddLanguage(originalLangName)
        val langTranslations = repository.findOrAddLanguage(translationsLangName)

        val domain = repository.createDomain("", langOriginal, langTranslations)
        val deck = addDefaultDeck(context, domain)
        return Pair(domain, deck)
    }

    private fun addDefaultDeck(context: Context, domain: Domain): Deck {
        return repository.addDeck(domain, context.getString(R.string.decks_default))
    }

    private fun Repository.findOrAddLanguage(name: String): Language {
        val found = languageByName(name)
        return found ?: addLanguage(name)
    }
}