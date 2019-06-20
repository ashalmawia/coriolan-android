package com.ashalmawia.coriolan.data

import android.content.Context
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.Language

interface DomainsRegistry {

    companion object {

        private var instance: DomainsRegistry? = null

        fun get(context: Context): DomainsRegistry {
            val repository = Repository.get(context)
            return instance ?: DomainsRegistryImpl(repository).also { instance = it }
        }
    }

    fun defaultDomain(): Domain?

    fun createDomain(originalLangName: String, translationsLangName: String): Domain
}

class DomainsRegistryImpl(private val repository: Repository) : DomainsRegistry {

    override fun defaultDomain(): Domain? {
        val domains = repository.allDomains()
        return if (domains.isNotEmpty()) {
            domains[0]
        } else {
            null
        }
    }

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