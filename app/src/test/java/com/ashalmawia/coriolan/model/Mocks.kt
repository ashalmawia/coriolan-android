package com.ashalmawia.coriolan.model

import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.exercise.MockExercise
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
        deckId: Long = 1L,
        transcription: String? = "[ɪɡˌzædʒəˈreɪʃən]"
) = CardData(original, transcription, translations, deckId)

fun mockCardData(
        original: String = "shrimp",
        translation: String = "креветка",
        deckId: Long = 1L
) = mockCardData(original, listOf(translation), deckId)

private var termId = 1L
fun mockTerm(value: String = "mock value", language: Language = mockLanguage(), transcription: String? = null)
        = Term(termId++, value, language)

private var extraId = 1L
fun mockExtra(value: String, type: ExtraType = ExtraType.TRANSCRIPTION, id: Long = extraId++)
        = TermExtra(id, type, value)

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
            mockTerm(front, language = domain.langOriginal(type)),
            listOf(mockTerm(back, language = domain.langTranslations(type)), mockTerm(language = domain.langTranslations(type)))
    )
}

fun mockExercise() = MockExercise()

fun mockTask(
        card: Card = mockCard(),
        state: State = mockState(),
        exercise: Exercise = mockExercise()
): Task {
    return Task(card, state, exercise)
}
fun mockTask(
        state: State = mockState(),
        domain: Domain = mockDomain(),
        id: Long = cardId++,
        type: CardType = CardType.FORWARD,
        exercise: Exercise = mockExercise()
): Task {
    return Task(mockCard(domain, id, type), state, exercise)
}

private var deckId = 1L
fun mockDeck(name: String = "My deck", domain: Domain = mockDomain(), id: Long = deckId++) = Deck(id, domain, name)

fun mockState(period: Int = 0) = State(SRState(mockToday(), period))
fun mockStateNew() = mockState(PERIOD_NEVER_SCHEDULED)
fun mockStateRelearn() = mockState(0)
fun mockStateInProgress() = mockState(5)
fun mockStateLearnt() = mockState(200)