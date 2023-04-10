package com.ashalmawia.coriolan.data

import com.ashalmawia.coriolan.data.storage.MockRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
    fun `languages added`() {
        // given
        val english = "English"
        val russian = "Russian"
        val defaultDeckName = "Default"

        // when
        val (domain, deck) = registry.createDomain(english, russian, defaultDeckName)

        // then
        assertEquals(english, domain.langOriginal().value)
        assertEquals(russian, domain.langTranslations().value)
        assertEquals(2, repository.langs.size)
        assertEquals(domain.langOriginal(), repository.langs[0])
        assertEquals(domain.langTranslations(), repository.langs[1])

        assertEquals(defaultDeckName, deck.name)
        assertEquals(domain, deck.domain)
    }

    @Test
    fun `languages reused`() {
        // given
        val english = repository.addLanguage("English")
        val russian = repository.addLanguage("Russian")
        val defaultDeckName = "Default"

        // when
        val (domain, deck) = registry.createDomain(english.value, russian.value, defaultDeckName)

        // then
        assertNotNull(domain)
        assertEquals(english, domain.langOriginal())
        assertEquals(russian, domain.langTranslations())

        assertEquals(defaultDeckName, deck.name)
        assertEquals(domain, deck.domain)
    }

    @Test
    fun `languages partly reused`() {
        // given
        val englishValue = "English"
        val russian = repository.addLanguage("Russian")
        val defaultDeckName = "Default"

        // when
        val (domain, deck) = registry.createDomain(englishValue, russian.value, defaultDeckName)

        // then
        assertNotNull(domain)
        assertEquals(englishValue, domain.langOriginal().value)
        assertEquals(russian, domain.langTranslations())
        assertEquals(domain.langOriginal(), repository.langs[1])
        assertEquals(domain.langTranslations(), repository.langs[0])

        assertEquals(defaultDeckName, deck.name)
        assertEquals(domain, deck.domain)
    }
}