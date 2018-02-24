package com.ashalmawia.coriolan.ui

import com.ashalmawia.coriolan.model.mockDeck
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CardValidatorTest {

    private var errorReported = false

    @Before
    fun before() {
        errorReported = false
    }

    @Test
    fun `test__validateDeckSelected__nothingSelected`() {
        // given
        val decks = listOf(mockDeck())

        // when
        val result = CardValidator.validateDeckSelected(-1, decks, this::onError)

        // then
        assertFalse(result)
        assertTrue(errorReported)
    }

    @Test
    fun `test__validateDeckSelected__indexTooLarge`() {
        // given
        val decks = listOf(mockDeck())

        // when
        val result = CardValidator.validateDeckSelected(5, decks, this::onError)

        // then
        assertFalse(result)
        assertTrue(errorReported)
    }

    @Test
    fun `test__validateDeckSelected__indexCorrect`() {
        // given
        val decks = listOf(mockDeck(), mockDeck(), mockDeck())

        for (i in 0 until decks.size) {
            // when
            val result = CardValidator.validateDeckSelected(i, decks, this::onError)

            // then
            assertTrue(result)
            assertFalse(errorReported)
        }
    }

    @Test
    fun `test__validateOriginalNotEmpty__empty`() {
        // given

        // when
        val result = CardValidator.validateOriginalNotEmpty("", this::onError)

        // then
        assertFalse(result)
        assertTrue(errorReported)
    }

    @Test
    fun `test__validateOriginalNotEmpty__nonEmpty`() {
        // given

        // when
        val result = CardValidator.validateOriginalNotEmpty("asdfs", this::onError)

        // then
        assertTrue(result)
        assertFalse(errorReported)
    }

    @Test
    fun `test__validateHasTranslations__noTranslations`() {
        // given
        val translations = listOf<String>()

        // when
        val result = CardValidator.validateHasTranslations(translations, this::onError)

        // then
        assertFalse(result)
        assertTrue(errorReported)
    }

    @Test
    fun `test__validateHasTranslations__allTranslationsEmpty`() {
        // given
        val translations = listOf("")

        // when
        val result = CardValidator.validateHasTranslations(translations, this::onError)

        // then
        assertFalse(result)
        assertTrue(errorReported)
    }

    @Test
    fun `test__validateHasTranslations__multiple__allTranslationsEmpty`() {
        // given
        val translations = listOf("", "")

        // when
        val result = CardValidator.validateHasTranslations(translations, this::onError)

        // then
        assertFalse(result)
        assertTrue(errorReported)
    }

    @Test
    fun `test__validateHasTranslations__oneTranslation`() {
        // given
        val translations = listOf("shrimp")

        // when
        val result = CardValidator.validateHasTranslations(translations, this::onError)

        // then
        assertTrue(result)
        assertFalse(errorReported)
    }

    @Test
    fun `test__validateHasTranslations__oneTranslationWithEmpty`() {
        // given
        val translations = listOf("", "shrimp")

        // when
        val result = CardValidator.validateHasTranslations(translations, this::onError)

        // then
        assertTrue(result)
        assertFalse(errorReported)
    }

    @Test
    fun `test__validateHasTranslations__multipleTranslations`() {
        // given
        val translations = listOf("sleep", "doze")

        // when
        val result = CardValidator.validateHasTranslations(translations, this::onError)

        // then
        assertTrue(result)
        assertFalse(errorReported)
    }

    @Test
    fun `test__validateHasTranslations__multipleTranslationsWithEmpty`() {
        // given
        val translations = listOf("sleep", "", "doze")

        // when
        val result = CardValidator.validateHasTranslations(translations, this::onError)

        // then
        assertTrue(result)
        assertFalse(errorReported)
    }

    private fun onError(error: String) {
        errorReported = true
    }
}