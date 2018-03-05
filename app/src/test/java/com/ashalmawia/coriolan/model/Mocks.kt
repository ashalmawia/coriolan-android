package com.ashalmawia.coriolan.model

import com.ashalmawia.coriolan.data.LanguagesRegistry
import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.scheduler.State
import org.joda.time.DateTime

// TODO: go over it's default usages and consider adding params and checking them
fun mockLanguage(id: Long = 1L, value: String = "English") = Language(id, value)

fun langOriginal() = LanguagesRegistry.original()
fun langTranslations() = LanguagesRegistry.translations()

fun addMockLanguages(storage: Repository) {
    storage.addLanguage(langOriginal().value)
    storage.addLanguage(langTranslations().value)
}

fun mockCardData(
        original: String,
        translations: List<String>,
        deckId: Long = 1L,
        contentType: ExpressionType = ExpressionType.WORD
) = CardData(original, langOriginal(), translations, langTranslations(), deckId, contentType)

fun mockCardData(
        original: String = "shrimp",
        translation: String = "креветка",
        deckId: Long = 1L,
        type: ExpressionType = ExpressionType.WORD
) = mockCardData(original, listOf(translation), deckId, type)

private var expressionId = 1L
fun mockExpression(value: String = "mock value", type: ExpressionType = ExpressionType.WORD, language: Language = mockLanguage())
        = Expression(expressionId++, value, type, language)

private var domainId = 1L
fun mockDomain(value: String = "Mock Domain") = Domain(domainId++, value, langOriginal(), langTranslations())

private var cardId = 1L
fun mockCard(state: State = mockState()): Card {
    return Card(
            cardId++,
            deckId,
            1L,
            mockExpression(language = LanguagesRegistry.original()),
            listOf(mockExpression(language = LanguagesRegistry.translations()), mockExpression(language = LanguagesRegistry.translations())),
            state
    )
}

private var deckId = 1L
fun mockDeck(name: String = "My deck") = Deck(deckId++, 1L, name)

fun mockState(period: Int = 0): State {
    return State(DateTime.now(), period)
}