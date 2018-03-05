package com.ashalmawia.coriolan.data

import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Domain

object DomainsRegistry {

    private lateinit var domain: Domain

    fun preinitialize(repository: Repository) {
        val domains = repository.allDomains()
        if (domains.isEmpty()) {
            domain = repository.createDomain("Default", LanguagesRegistry.original(), LanguagesRegistry.translations())
        } else {
            domain = domains[0]
        }
    }

    fun domain(): Domain {
        return domain
    }
}