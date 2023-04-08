package com.ashalmawia.coriolan.model

import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.LearningDay
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.exercise.MockExercise
import com.ashalmawia.coriolan.learning.exercise.flashcards.ExerciseState
import com.ashalmawia.coriolan.learning.exercise.flashcards.INTERVAL_NEVER_SCHEDULED
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
        = Term(termId++, value, language, transcription)

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
            type,
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

fun mockState(interval: Int = 0) = ExerciseState(mockToday(), interval)
fun mockStateNew() = ExerciseState(mockToday(), INTERVAL_NEVER_SCHEDULED)
fun mockStateInProgress() = ExerciseState(mockToday(), 5)
fun mockStateRelearn() = ExerciseState(mockToday(), 0)
fun mockStateLearnt() = ExerciseState(mockToday(), 200)
fun mockLearningProgressNew(): LearningProgress = mockLearningProgress(mockStateNew())
fun mockLearningProgressRelearn(): LearningProgress = mockLearningProgress(mockStateRelearn())
fun mockLearningProgressInProgress(): LearningProgress = mockLearningProgress(mockStateInProgress())
fun mockLearningProgressLearnt(): LearningProgress = mockLearningProgress(mockStateLearnt())
fun mockLearningProgress(): LearningProgress = LearningProgress(emptyMap())
fun mockLearningProgress(due: DateTime = mockToday(), interval: Int = 0): LearningProgress =
        mockLearningProgress(ExerciseState(due, interval))
fun mockLearningProgress(exerciseState: ExerciseState): LearningProgress = LearningProgress(
        mapOf(ExerciseId.TEST to exerciseState)
)

fun mockEmptyExerciseState(today: LearningDay) = ExerciseState(today, INTERVAL_NEVER_SCHEDULED)