package com.ashalmawia.coriolan.model

import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.assignment.MockState
import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.exercise.sr.PERIOD_NEVER_SCHEDULED
import com.ashalmawia.coriolan.learning.exercise.sr.SRState
import com.ashalmawia.coriolan.learning.mockToday

// TODO: go over it's default usages and consider adding params and checking them
fun mockLanguage(id: Long = 1L, value: String = "English") = Language(id, value)

fun langOriginal() = Language(1L, "English")
fun langTranslations() = Language(2L, "Russian")

fun addMockLanguages(storage: Repository) {
    storage.addLanguage(langOriginal().value)
    storage.addLanguage(langTranslations().value)
}

fun mockCardData(
        original: String,
        translations: List<String>,
        deckId: Long = 1L
) = CardData(original, translations, deckId)

fun mockCardData(
        original: String = "shrimp",
        translation: String = "креветка",
        deckId: Long = 1L
) = mockCardData(original, listOf(translation), deckId)

private var expressionId = 1L
fun mockExpression(value: String = "mock value", language: Language = mockLanguage())
        = Expression(expressionId++, value, language)

private var domainId = 1L
fun mockDomain(value: String = "Mock Domain") = Domain(domainId++, value, langOriginal(), langTranslations())

private var cardId = 1L
fun mockCard(
        domain: Domain = mockDomain(),
        id: Long = cardId++,
        type: CardType = CardType.FORWARD,
        front: String = "mock front",
        back: String = "mock back"
): Card {
    return Card(
            id,
            deckId,
            domain,
            mockExpression(front, language = domain.langOriginal(type)),
            listOf(mockExpression(back, language = domain.langTranslations(type)), mockExpression(language = domain.langTranslations(type)))
    )
}
fun mockForwardCardWithState(): CardWithState<MockState> = mockCardWithState(MockState(), type = CardType.FORWARD)
fun mockReverseCardWithState(): CardWithState<MockState> = mockCardWithState(MockState(), type = CardType.REVERSE)

fun <T : State> mockCardWithState(
        state: T,
        domain: Domain = mockDomain(),
        id: Long = cardId++,
        type: CardType = CardType.FORWARD): CardWithState<T> {
    return CardWithState(mockCard(domain, id, type), state)
}

private var deckId = 1L
fun mockDeck(name: String = "My deck", domain: Domain = mockDomain(), id: Long = deckId++) = Deck(id, domain, name)

fun mockState(period: Int = 0) = SRState(mockToday(), period)
fun mockStateNew() = mockState(PERIOD_NEVER_SCHEDULED)
fun mockStateRelearn() = mockState(0)
fun mockStateInProgress() = mockState(5)
fun mockStateLearnt() = mockState(200)