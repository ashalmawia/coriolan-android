package com.ashalmawia.coriolan.model

import org.junit.Assert.*

fun assertLanguageCorrect(language: Language?, value: String) {
    assertNotNull("language is found", language)
    assertEquals("language has correct values", value, language!!.value)
}

fun assertExpressionCorrect(expected: Expression, actual: Expression) {
    assertExpressionCorrect(actual, expected.value, expected.language)
}

fun assertExpressionCorrect(expression: Expression?, value: String, language: Language) {
    assertNotNull("expression is found", expression)
    assertEquals("expression has correct values", value, expression!!.value)
    assertLanguageCorrect(expression.language, language.value)
}

fun assertExtrasCorrect(extras: ExpressionExtras, transcription: String?) {
    assertEquals("transcription extra is correct", transcription, extras.transcription)
}

fun assertDomainCorrect(domain: Domain?, name: String, langOriginal: Language, langTranslations: Language) {
    assertNotNull("domain exists", domain)
    assertEquals("domain name is correct", name, domain!!.name)
    assertEquals("original language is correct", langOriginal, domain.langOriginal())
    assertEquals("translations language is correct", langTranslations, domain.langTranslations())
}

fun assertCardCorrect(card: Card?, data: CardData, domain: Domain) {
    _assertCardCorrect(card, data, domain.langOriginal(), domain.langTranslations())
}
fun assertCardCorrectReverse(card: Card?, data: CardData, domain: Domain) {
    _assertCardCorrect(card, data, domain.langTranslations(), domain.langOriginal())
}
private fun _assertCardCorrect(card: Card?, data: CardData, langOriginal: Language, langTranslations: Language) {
    assertNotNull("card is created", card)
    assertExpressionCorrect(card!!.original, data.original, langOriginal)
    assertEquals("translations count is correct", data.translations.size, card.translations.size)
    for (i in 0 until card.translations.size) {
        assertExpressionCorrect(card.translations[i], data.translations[i], langTranslations)
    }
}
fun assertCardCorrect(card: Card?, original: Expression, translations: List<Expression>, deckId: Long, domain: Domain) {
    assertNotNull(card)
    card!!
    assertEquals(original, card.original)
    assertEquals(translations, card.translations)
    assertEquals(deckId, card.deckId)
    assertEquals(domain, card.domain)
}

fun assertDeckCorrect(deck: Deck?, name: String, domain: Domain) {
    assertNotNull("deck is created", deck)
    assertEquals("deck name is correct", name, deck!!.name)
    assertEquals("domain id is correct", domain, deck.domain)
}

fun assertDeckCardsCorrect(cards: List<Card>, data: List<CardData>, domain: Domain) {
    assertEquals("cards count is correct", data.size, cards.size)
    for (i in 0 until cards.size) {
        assertCardCorrect(cards[i], data[i], domain)
    }
}