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
        registry = DomainsRegistryImpl(repository)
    }

    @Test
    fun test__preinitialize__domainAdded() {
        // then
        assertEquals(0, repository.allDomains().size)
        assertNull(registry.defaultDomain())
    }

    @Test
    fun test__preinitialize__domainReused() {
        // given
        val domain = repository.createDomain("Some domain", langTranslations(), langOriginal())

        // when
        val default = registry.defaultDomain()

        // then
        assertNotNull(default)
        assertEquals(domain, default)
        assertEquals(1, repository.allDomains().size)
    }
}