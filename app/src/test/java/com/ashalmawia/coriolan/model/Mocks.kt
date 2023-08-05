package com.ashalmawia.coriolan.model

import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.CardWithProgress
import com.ashalmawia.coriolan.learning.ExerciseData
import com.ashalmawia.coriolan.learning.INTERVAL_NEVER_SCHEDULED
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.SchedulingState
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.exercise.MockExercise
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.util.asDeckId
import com.ashalmawia.coriolan.util.asDomainId
import org.joda.time.DateTime

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
        deck: Deck,
        transcription: String? = "[ɪɡˌzædʒəˈreɪʃən]"
) = CardData(original, transcription, translations, deck)

fun mockCardData(
        original: String = "shrimp",
        translation: String = "креветка",
        deck: Deck
) = mockCardData(original, listOf(translation), deck)

private var termId = 1L
fun mockTerm(value: String = "mock value", language: Language = mockLanguage(), transcription: String? = null)
        = Term(termId++, value, language, transcription)

private var domainId = 1L
fun mockDomain(value: String = "Mock Domain") = Domain(domainId++.asDomainId(), value, langOriginal(), langTranslations())

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
            deckId.asDeckId(),
            domain,
            type,
            mockTerm(front, language = domain.langOriginal(type)),
            listOf(mockTerm(back, language = domain.langTranslations(type)), mockTerm(language = domain.langTranslations(type))),
            mockToday()
    )
}

fun mockExercise() = MockExercise()

fun mockTask(
        card: Card = mockCard(),
        learningProgress: LearningProgress = mockLearningProgress(),
        exercise: Exercise = mockExercise()
): Task {
    return Task(card, learningProgress, exercise)
}
fun mockTask(
        learningProgress: LearningProgress = mockLearningProgress(),
        domain: Domain = mockDomain(),
        id: Long = cardId++,
        type: CardType = CardType.FORWARD,
        exercise: Exercise = mockExercise()
): Task {
    return Task(mockCard(domain, id, type), learningProgress, exercise)
}

fun mockCardWithProgress(
        learningProgress: LearningProgress = mockLearningProgress(),
        domain: Domain = mockDomain(),
        id: Long = cardId++,
        type: CardType = CardType.FORWARD
): CardWithProgress {
    val mockTask = mockTask(learningProgress, domain, id, type)
    return CardWithProgress(mockTask.card, mockTask.learningProgress)
}

private var deckId = 1L
fun mockDeck(name: String = "My deck", domain: Domain = mockDomain(), id: DeckId = deckId++.asDeckId()) = Deck(id, domain, name)

fun mockState(interval: Int = 0) = SchedulingState(mockToday(), interval)
fun mockStateNew() = SchedulingState(mockToday(), INTERVAL_NEVER_SCHEDULED)
fun mockStateInProgress() = SchedulingState(mockToday(), 5)
fun mockStateRelearn() = SchedulingState(mockToday(), 0)
fun mockStateLearnt() = SchedulingState(mockToday(), 200)
fun mockLearningProgressNew(): LearningProgress = mockLearningProgress(mockStateNew())
fun mockLearningProgressRelearn(): LearningProgress = mockLearningProgress(mockStateRelearn())
fun mockLearningProgressInProgress(): LearningProgress = mockLearningProgress(mockStateInProgress())
fun mockLearningProgressLearnt(): LearningProgress = mockLearningProgress(mockStateLearnt())
fun mockLearningProgress(): LearningProgress = LearningProgress.empty()
fun mockLearningProgress(due: DateTime = mockToday(), interval: Int = 0): LearningProgress =
        mockLearningProgress(SchedulingState(due, interval))
fun mockLearningProgress(exerciseState: SchedulingState): LearningProgress =
        LearningProgress(exerciseState, ExerciseData())