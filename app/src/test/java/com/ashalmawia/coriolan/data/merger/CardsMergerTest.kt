package com.ashalmawia.coriolan.data.merger

import com.ashalmawia.coriolan.data.storage.MockRepository
import com.ashalmawia.coriolan.learning.MockExercisesRegistry
import com.ashalmawia.coriolan.learning.scheduler.sr.SRState
import com.ashalmawia.coriolan.learning.scheduler.sr.emptyState
import com.ashalmawia.coriolan.learning.scheduler.today
import com.ashalmawia.coriolan.model.assertCardCorrect
import com.ashalmawia.coriolan.model.mockDomain
import com.ashalmawia.coriolan.model.mockExpression
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CardsMergerTest {

    private val domain = mockDomain()
    private val repository = MockRepository()
    private val exersicesRegistry = MockExercisesRegistry()

    private val deckId = 5L
    private val mockState = SRState(today().plusDays(5), 8)
    private val exercise = exersicesRegistry.defaultExercise()

    private val merger = CardsMergerImpl(repository, domain, exersicesRegistry)

    private fun original(value: String) = mockExpression(value, language = domain.langOriginal())
    private fun translation(value: String) = mockExpression(value, language = domain.langTranslations())

    @Test
    fun `test__emptyRepository__expectation_NewCardIsAdded`() {
        // given
        val original = original("shrimp")
        val translations = listOf(
                translation("креветка")
        )

        // when
        merger.mergeOrAdd(original, translations, deckId)

        // then
        assertEquals(1, repository.cards.size)
        assertCardCorrect(repository.cards[0], original, translations, deckId, domain)
    }

    @Test
    fun `test__noMatches__expectation_NewCardIsAdded`() {
        // given
        val card = repository.addCard(domain, deckId, original("spring"), listOf(
                translation("весна"), translation("источник")
        ))
        repository.updateSRCardState(card, mockState, exercise.stableId)

        val original = original("shrimp")
        val translations = listOf(
                translation("креветка")
        )

        // when
        merger.mergeOrAdd(original, translations, deckId)

        // then
        assertEquals(2, repository.cards.size)
        assertEquals(card, repository.cards[0])
        assertEquals(mockState, repository.getSRCardState(repository.cards[0], exercise.stableId))
        assertCardCorrect(repository.cards[1], original, translations, deckId, domain)
    }

    @Test
    fun `test__orignialsMatch__expectation_OldCardUpdated_ProgressReset`() {
        // given
        val original = original("spring")
        val translation1 = translation("весна")
        val translation2 = translation("источник")

        val card = repository.addCard(domain, deckId, original, listOf(translation1))
        repository.updateSRCardState(card, mockState, exercise.stableId)

        assertEquals(1, repository.cards.size)
        assertEquals(card, repository.cards[0])
        assertEquals(mockState, repository.getSRCardState(repository.cards[0], exercise.stableId))

        // when
        merger.mergeOrAdd(original, listOf(translation2), deckId)

        // then
        System.out.println(repository.cards)
        assertEquals(1, repository.cards.size)
        assertCardCorrect(repository.cards[0], original, listOf(translation1, translation2), deckId, domain)
        assertEquals("progress is reset", emptyState(), repository.getSRCardState(repository.cards[0], exercise.stableId))
    }

    @Test
    fun `test__translationsMatch__expectation_NewCardIsAdded_OldCardPreserved`() {
        // given
        val card = repository.addCard(domain, deckId, original("rocket"), listOf(
                translation("ракета")
        ))
        repository.updateSRCardState(card, mockState, exercise.stableId)

        val original = original("missile")
        val translations = listOf(
                translation("ракета")
        )

        // when
        merger.mergeOrAdd(original, translations, deckId)

        // then
        assertEquals(2, repository.cards.size)
        assertEquals(card, repository.cards[0])
        assertEquals(mockState, repository.getSRCardState(repository.cards[0], exercise.stableId))
        assertCardCorrect(repository.cards[1], original, translations, deckId, domain)
    }

    @Test
    fun `test__translationsPartlyMatch__expectation_NewCardIsAdded_OldCardPreserved`() {
        // given
        val card = repository.addCard(domain, deckId, original("rocket"), listOf(
                translation("ракета"), translation("космический корабль")
        ))
        repository.updateSRCardState(card, mockState, exercise.stableId)

        val original = original("missile")
        val translations = listOf(
                translation("ракета")
        )

        // when
        merger.mergeOrAdd(original, translations, deckId)

        // then
        assertEquals(2, repository.cards.size)
        assertEquals(card, repository.cards[0])
        assertEquals(mockState, repository.getSRCardState(repository.cards[0], exercise.stableId))
        assertCardCorrect(repository.cards[1], original, translations, deckId, domain)
    }

    @Test
    fun `test__bothMatchButNotDuplicate__newIsMore__expectation_OldCardUpdated_ProgressReset`() {
        // given
        val original = original("spring")
        val translation1 = translation("весна")
        val translation2 = translation("источник")

        val card = repository.addCard(domain, deckId, original, listOf(translation1))
        repository.updateSRCardState(card, mockState, exercise.stableId)

        assertEquals(1, repository.cards.size)
        assertEquals(card, repository.cards[0])
        assertEquals(mockState, repository.getSRCardState(repository.cards[0], exercise.stableId))

        // when
        val translations = listOf(translation1, translation2)
        merger.mergeOrAdd(original, translations, deckId)

        // then
        assertEquals(1, repository.cards.size)
        assertCardCorrect(repository.cards[0], original, listOf(translation1, translation2), deckId, domain)
        assertEquals("progress is reset", emptyState(), repository.getSRCardState(repository.cards[0], exercise.stableId))
    }

    @Test
    fun `test__duplicate__newIsLessThanExisting__expectation_NoChange`() {
        // given
        val original = original("spring")
        val translation1 = translation("весна")
        val translation2 = translation("источник")

        val card = repository.addCard(domain, deckId, original, listOf(translation1, translation2))
        repository.updateSRCardState(card, mockState, exercise.stableId)

        // when
        val translations = listOf(translation2)
        merger.mergeOrAdd(original, translations, deckId)

        // then
        assertEquals(1, repository.cards.size)
        assertEquals(card, repository.cards[0])
        assertEquals(mockState, repository.getSRCardState(repository.cards[0], exercise.stableId))
    }

    @Test
    fun `test__fullDuplicate__expectation_NoChange`() {
        // given
        val original = original("spring")
        val translation1 = translation("весна")
        val translation2 = translation("источник")

        val card = repository.addCard(domain, deckId, original, listOf(translation1, translation2))
        repository.updateSRCardState(card, mockState, exercise.stableId)

        // when
        val translations = listOf(translation1, translation2)
        merger.mergeOrAdd(original, translations, deckId)

        // then
        assertEquals(1, repository.cards.size)
        assertEquals(card, repository.cards[0])
        assertEquals(mockState, repository.getSRCardState(repository.cards[0], exercise.stableId))
    }
}