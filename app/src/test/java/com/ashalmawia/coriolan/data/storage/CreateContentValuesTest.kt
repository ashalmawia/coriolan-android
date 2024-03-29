package com.ashalmawia.coriolan.data.storage

import android.content.ContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.*
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS_DECK_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS_DOMAIN_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS_FRONT_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS_PAYLOAD
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.CARDS_TYPE
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractCards.createCardContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.DECKS_DOMAIN_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.DECKS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.DECKS_NAME
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDecks.createDeckContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.DOMAINS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.DOMAINS_LANG_ORIGINAL
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.DOMAINS_LANG_TRANSLATIONS
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.DOMAINS_NAME
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractDomains.createDomainContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractLanguages.LANGUAGES_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractLanguages.LANGUAGES_VALUE
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractLanguages.createLanguageContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.STATES_IS_ACTIVE
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.STATES_CARD_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.STATES_DUE_DATE
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.STATES_INTERVAL
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.STATES__CARD_ACTIVE
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractStates.createCardStateContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.TERMS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.TERMS_LANGUAGE_ID
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.TERMS_PAYLOAD
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.TERMS_VALUE
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.createTermContentValues
import com.ashalmawia.coriolan.data.storage.sqlite.contract.ContractTerms.createTermPayload
import com.ashalmawia.coriolan.data.storage.sqlite.payload.CardPayload
import com.ashalmawia.coriolan.data.storage.sqlite.payload.TermId
import com.ashalmawia.coriolan.data.storage.sqlite.payload.TermPayload
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.mockLanguage
import com.ashalmawia.coriolan.model.mockLearningProgress
import com.ashalmawia.coriolan.model.mockTerm
import com.ashalmawia.coriolan.util.asCardId
import com.ashalmawia.coriolan.util.asDeckId
import com.ashalmawia.coriolan.util.asDomainId
import com.ashalmawia.coriolan.util.asLanguageId
import com.ashalmawia.coriolan.util.asTermId
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.joda.time.DateTime
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreateContentValuesTest {

    @Test
    fun createLanguageContentValuesTest() {
        // given
        val value = "Russian"

        // when
        val cv = createLanguageContentValues(value)

        // then
        assertEquals("values count is correct", 1, cv.size())
        assertEquals("$LANGUAGES_VALUE is correct", value, cv.get(LANGUAGES_VALUE))
    }

    @Test
    fun createLanguageContentValuesTest__hasId() {
        // given
        val value = "Russian"
        val id = 5L

        // when
        val cv = createLanguageContentValues(value, id.asLanguageId())

        // then
        assertEquals("values count is correct", 2, cv.size())
        assertEquals("$LANGUAGES_ID is correct", id, cv.get(LANGUAGES_ID))
        assertEquals("$LANGUAGES_VALUE is correct", value, cv.get(LANGUAGES_VALUE))
    }

    @Test
    fun createTermContentValuesTest__emptyPayload() {
        // given
        val value = "some value"
        val lang = mockLanguage(777L, "Russian")
        val payload = createTermPayload(null)
        val payloadString = jacksonObjectMapper().writeValueAsString(payload)

        // when
        val cv = createTermContentValues(value, lang, payload)

        // then
        assertEquals("values count is correct", 3, cv.size())
        assertEquals("$TERMS_VALUE is correct", value, cv.get(TERMS_VALUE))
        assertEquals("$TERMS_LANGUAGE_ID is correct", lang.id.value, cv.get(TERMS_LANGUAGE_ID))
        assertEquals("$TERMS_PAYLOAD is correct", payloadString, cv.get(TERMS_PAYLOAD))

        // when
        val id = 7L
        val cv1 = createTermContentValues(value, lang.id, payload, id.asTermId())

        // then
        assertEquals("values count is correct", 4, cv1.size())
        assertEquals("$TERMS_ID is correct", id, cv1.get(TERMS_ID))
        assertEquals("$TERMS_VALUE is correct", value, cv1.get(TERMS_VALUE))
        assertEquals("$TERMS_LANGUAGE_ID is correct", lang.id.value, cv1.get(TERMS_LANGUAGE_ID))
        assertEquals("$TERMS_PAYLOAD is correct", payloadString, cv.get(TERMS_PAYLOAD))
    }

    @Test
    fun createTermContentValuesTest__nonEmtpyPayload() {
        // given
        val value = "some value"
        val lang = mockLanguage(777L, "Russian")
        val payload = TermPayload("transcription")
        val payloadString = jacksonObjectMapper().writeValueAsString(payload)

        // when
        val cv = createTermContentValues(value, lang, payload)

        // then
        assertEquals("values count is correct", 3, cv.size())
        assertEquals("$TERMS_VALUE is correct", value, cv.get(TERMS_VALUE))
        assertEquals("$TERMS_LANGUAGE_ID is correct", lang.id.value, cv.get(TERMS_LANGUAGE_ID))
        assertEquals("$TERMS_PAYLOAD is correct", payloadString, cv.get(TERMS_PAYLOAD))

        // when
        val id = 7L
        val cv1 = createTermContentValues(value, lang.id, payload, id.asTermId())

        // then
        assertEquals("values count is correct", 4, cv1.size())
        assertEquals("$TERMS_ID is correct", id, cv1.get(TERMS_ID))
        assertEquals("$TERMS_VALUE is correct", value, cv1.get(TERMS_VALUE))
        assertEquals("$TERMS_LANGUAGE_ID is correct", lang.id.value, cv1.get(TERMS_LANGUAGE_ID))
        assertEquals("$TERMS_PAYLOAD is correct", payloadString, cv.get(TERMS_PAYLOAD))
    }

    @Test
    fun createDomainContentValuesTest() {
        // given
        val name = "Some name"
        val langOriginal = mockLanguage(11L, "Russian")
        val langTranslations = mockLanguage(12L, "French")

        // when
        val cv = createDomainContentValues(name, langOriginal, langTranslations)

        // then
        assertEquals("values count is correct", 3, cv.size())
        assertEquals("$DOMAINS_NAME is correct", name, cv.get(DOMAINS_NAME))
        assertEquals("$DOMAINS_LANG_ORIGINAL is correct", langOriginal.id.value, cv.get(DOMAINS_LANG_ORIGINAL))
        assertEquals("$DOMAINS_LANG_TRANSLATIONS is correct", langTranslations.id.value, cv.get(DOMAINS_LANG_TRANSLATIONS))

        // when
        val id = 5L
        val cv1 = createDomainContentValues(name, langOriginal.id, langTranslations.id, id.asDomainId())

        // then
        assertEquals("values count is correct", 4, cv1.size())
        assertEquals("$DOMAINS_ID is correct", id, cv1.get(DOMAINS_ID))
        assertEquals("$DOMAINS_NAME is correct", name, cv1.get(DOMAINS_NAME))
        assertEquals("$DOMAINS_LANG_ORIGINAL is correct", langOriginal.id.value, cv1.get(DOMAINS_LANG_ORIGINAL))
        assertEquals("$DOMAINS_LANG_TRANSLATIONS is correct", langTranslations.id.value, cv1.get(DOMAINS_LANG_TRANSLATIONS))
    }

    @Test
    fun createCardContentValuesTest() {
        // given
        val deckId = 5L
        val domainId = 2L
        val type = CardType.FORWARD
        val lang = mockLanguage()
        val original = mockTerm("some original term", lang)
        val payload = CardPayload(listOf(TermId(1L), TermId(2L)))
        val payloadString = jacksonObjectMapper().writeValueAsString(payload)

        // when
        val cv = createCardContentValues(domainId.asDomainId(), deckId.asDeckId(), original, type, payload)

        // then
        assertEquals("values count is correct", 5, cv.size())
        assertEquals("$CARDS_FRONT_ID is correct", original.id.value, cv.get(CARDS_FRONT_ID))
        assertEquals("$CARDS_DECK_ID is correct", deckId, cv.get(CARDS_DECK_ID))
        assertEquals("$CARDS_DOMAIN_ID is correct", domainId, cv.get(CARDS_DOMAIN_ID))
        assertEquals("$CARDS_TYPE is correct", type.value, cv.get(CARDS_TYPE))
        assertEquals("$CARDS_PAYLOAD is correct", payloadString, cv.get(CARDS_PAYLOAD))
    }

    @Test
    fun createCardContentValuesTest__hasCardId() {
        // given
        val deckId = 5L
        val domainId = 1L
        val lang = mockLanguage()
        val type = CardType.FORWARD
        val original = mockTerm("some original term", lang)
        val payload = CardPayload(listOf(1L, 2L).map { TermId(it) })
        val payloadString = jacksonObjectMapper().writeValueAsString(payload)
        val cardId = 7L

        // when
        val cv = createCardContentValues(domainId.asDomainId(), deckId.asDeckId(), original, type, payload, cardId.asCardId())

        // then
        cv.run {
            assertEquals("values count is correct", 6, size())
            assertEquals("$CARDS_ID is correct", cardId, get(CARDS_ID))
            assertEquals("$CARDS_FRONT_ID is correct", original.id.value, get(CARDS_FRONT_ID))
            assertEquals("$CARDS_DECK_ID is correct", deckId, get(CARDS_DECK_ID))
            assertEquals("$CARDS_DOMAIN_ID is correct", domainId, get(CARDS_DOMAIN_ID))
            assertEquals("$CARDS_TYPE is correct", type.value, get(CARDS_TYPE))
            assertEquals("$CARDS_PAYLOAD is correct", payloadString, get(CARDS_PAYLOAD))
        }

        // when
        val cv1 = createCardContentValues(domainId.asDomainId(), deckId.asDeckId(), original.id, type, payload, cardId.asCardId())

        // then
        cv1.run {
            assertEquals("values count is correct", 6, size())
            assertEquals("$CARDS_ID is correct", cardId, get(CARDS_ID))
            assertEquals("$CARDS_FRONT_ID is correct", original.id.value, get(CARDS_FRONT_ID))
            assertEquals("$CARDS_DECK_ID is correct", deckId, get(CARDS_DECK_ID))
            assertEquals("$CARDS_DOMAIN_ID is correct", domainId, get(CARDS_DOMAIN_ID))
            assertEquals("$CARDS_TYPE is correct", type.value, get(CARDS_TYPE))
            assertEquals("$CARDS_PAYLOAD is correct", payloadString, get(CARDS_PAYLOAD))
        }
    }

    @Test
    fun createDeckContentValuesTest() {
        // given
        val name = "New Deck"
        val domainId = 3L

        // when
        val cv = createDeckContentValues(domainId.asDomainId(), name)

        // then
        assertEquals("values count is correct", 2, cv.size())
        assertEquals("$DECKS_NAME is correct", name, cv.get(DECKS_NAME))
        assertEquals("$DECKS_DOMAIN_ID is correct", domainId, cv.get(DECKS_DOMAIN_ID))
    }

    @Test
    fun createDeckContentValuesTest__hasId() {
        // given
        val name = "New Deck"
        val domainId = 3L
        val deckId = 5L

        // when
        val cv = createDeckContentValues(domainId.asDomainId(), name, deckId.asDeckId())

        // then
        assertEquals("values count is correct", 3, cv.size())
        assertEquals("$DECKS_ID is correct", deckId, cv.get(DECKS_ID))
        assertEquals("$DECKS_NAME is correct", name, cv.get(DECKS_NAME))
        assertEquals("$DECKS_DOMAIN_ID is correct", domainId, cv.get(DECKS_DOMAIN_ID))
    }

    @Test
    fun createStateContentValuesTest() {
        // given
        val cardId = 1L
        val due = DateTime(1519529781000)
        val interval = 16
        val learningProgress = mockLearningProgress(due, interval)

        // when
        val cv = createCardStateContentValues(cardId.asCardId(), learningProgress)

        // then
        assertEquals("values count is correct", 5, cv.size())
        assertEquals("$STATES_CARD_ID is correct", cardId, cv.get(STATES_CARD_ID))
        assertEquals("$STATES_DUE_DATE is correct", due, cv.getAsDate(STATES_DUE_DATE))
        assertEquals("$STATES_INTERVAL is correct", interval, cv.get(STATES_INTERVAL))
        assertEquals("$STATES_IS_ACTIVE is correct", STATES__CARD_ACTIVE, cv.get(STATES_IS_ACTIVE))

        // when
        val state = learningProgress.state
        val cv1 = createCardStateContentValues(cardId.asCardId(), state.due, state.interval)

        // then
        assertEquals("values count is correct", 5, cv1.size())
        assertEquals("$STATES_CARD_ID is correct", cardId, cv1.get(STATES_CARD_ID))
        assertEquals("$STATES_DUE_DATE is correct", due, cv1.getAsDate(STATES_DUE_DATE))
        assertEquals("$STATES_INTERVAL is correct", interval, cv1.get(STATES_INTERVAL))
        assertEquals("$STATES_IS_ACTIVE is correct", STATES__CARD_ACTIVE, cv1.get(STATES_IS_ACTIVE))
    }
}

private fun ContentValues.getAsDate(key: String): DateTime? {
    val timestamp = getAsLong(key)
    return if (timestamp == null) null else DateTime(timestamp)
}