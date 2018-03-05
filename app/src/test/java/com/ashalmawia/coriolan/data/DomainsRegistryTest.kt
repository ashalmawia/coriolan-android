package com.ashalmawia.coriolan.data

import com.ashalmawia.coriolan.data.storage.MockRepository
import com.ashalmawia.coriolan.model.langOriginal
import com.ashalmawia.coriolan.model.langTranslations
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DomainsRegistryTest {

    private lateinit var repository: MockRepository
    private lateinit var registry: DomainsRegistry

    @Before
    fun before() {
        repository = MockRepository()
        registry = DomainsRegistry
    }

    @Test
    fun `test__preinitialize__domainAdded`() {
        // given

        // when
        registry.preinitialize(repository)

        // then
        assertEquals(1, repository.allDomains().size)

        // when
        val domain = repository.domains[0]

        // then
        assertEquals(domain, registry.domain())
    }

    @Test
    fun `test__preinitialize__domainReused`() {
        // given
        val domain = repository.createDomain("Some domain", langTranslations(), langOriginal())

        // when
        registry.preinitialize(repository)
        val default = registry.domain()

        // then
        assertEquals(domain, default)
        assertEquals(1, repository.allDomains().size)
    }
}