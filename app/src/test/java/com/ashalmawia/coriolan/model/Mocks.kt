package com.ashalmawia.coriolan.model

import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.LearningDay
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.exercise.MockExercise
import com.ashalmawia.coriolan.learning.exercise.sr.ExerciseState
import com.ashalmawia.coriolan.learning.exercise.sr.PERIOD_NEVER_SCHEDULED
import com.ashalmawia.coriolan.learning.mockToday
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
        = Term(termId++, value, language, Extras(transcription))

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

private var deckId = 1L
fun mockDeck(name: String = "My deck", domain: Domain = mockDomain(), id: Long = deckId++) = Deck(id, domain, name)

fun mockState(period: Int = 0) = ExerciseState(mockToday(), period)
fun mockLearningProgressNew(): LearningProgress = mockLearningProgress(mockToday(), PERIOD_NEVER_SCHEDULED)
fun mockLearningProgressRelearn(): LearningProgress = mockLearningProgress(mockToday(), 0)
fun mockLearningProgressInProgress(): LearningProgress = mockLearningProgress(mockToday(), 5)
fun mockLearningProgressLearnt(): LearningProgress = mockLearningProgress(mockToday(), 200)
fun mockLearningProgress(): LearningProgress = LearningProgress(emptyMap())
fun mockLearningProgress(due: DateTime = mockToday(), period: Int = 0): LearningProgress = LearningProgress(
        mapOf(ExerciseId.FLASHCARDS to ExerciseState(due, period))
)

fun mockEmptyExerciseState(today: LearningDay) = ExerciseState(today, PERIOD_NEVER_SCHEDULED)