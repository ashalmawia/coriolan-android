package com.ashalmawia.coriolan.data.merger

import com.ashalmawia.coriolan.data.storage.MockRepository
import com.ashalmawia.coriolan.learning.ExercisesRegistry
import com.ashalmawia.coriolan.learning.exercise.MockExercise
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.learning.scheduler.emptyState
import com.ashalmawia.coriolan.learning.scheduler.today
import com.ashalmawia.coriolan.model.assertCardCorrect
import com.ashalmawia.coriolan.model.mockDomain
import com.ashalmawia.coriolan.model.mockExpression
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner

@RunWith(BlockJUnit4ClassRunner::class)
class CardsMergerTest {

    private val domain = mockDomain()
    private val repository = MockRepository()
    private val exersicesRegistry = ExercisesRegistry

    private val deckId = 5L
    private val mockState = State(today().plusDays(5), 8)
    private val exercise = MockExercise()

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
        val withState = repository.updateCardState(card, mockState, exercise)

        val original = original("shrimp")
        val translations = listOf(
                translation("креветка")
        )

        // when
        merger.mergeOrAdd(original, translations, deckId)

        // then
        assertEquals(2, repository.cards.size)
        assertEquals(withState, repository.cards[0])
        assertCardCorrect(repository.cards[1], original, translations, deckId, domain)
    }

    @Test
    fun `test__orignialsMatch__expectation_OldCardUpdated_ProgressReset`() {
        // given
        val original = original("spring")
        val translation1 = translation("весна")
        val translation2 = translation("источник")

        val card = repository.addCard(domain, deckId, original, listOf(translation1))
        val updated = repository.updateCardState(card, mockState, exercise)

        assertEquals(1, repository.cards.size)
        assertEquals(updated, repository.cards[0])

        // when
        merger.mergeOrAdd(original, listOf(translation2), deckId)

        // then
        System.out.println(repository.cards)
        assertEquals(1, repository.cards.size)
        assertCardCorrect(repository.cards[0], original, listOf(translation1, translation2), deckId, domain)
        assertEquals("progress is reset", emptyState(), repository.cards[0].state)
    }

    @Test
    fun `test__translationsMatch__expectation_NewCardIsAdded_OldCardPreserved`() {
        // given
        val card = repository.addCard(domain, deckId, original("rocket"), listOf(
                translation("ракета")
        ))
        val withState = repository.updateCardState(card, mockState, exercise)

        val original = original("missile")
        val translations = listOf(
                translation("ракета")
        )

        // when
        merger.mergeOrAdd(original, translations, deckId)

        // then
        assertEquals(2, repository.cards.size)
        assertEquals(withState, repository.cards[0])
        assertCardCorrect(repository.cards[1], original, translations, deckId, domain)
    }

    @Test
    fun `test__translationsPartlyMatch__expectation_NewCardIsAdded_OldCardPreserved`() {
        // given
        val card = repository.addCard(domain, deckId, original("rocket"), listOf(
                translation("ракета"), translation("космический корабль")
        ))
        val withState = repository.updateCardState(card, mockState, exercise)

        val original = original("missile")
        val translations = listOf(
                translation("ракета")
        )

        // when
        merger.mergeOrAdd(original, translations, deckId)

        // then
        assertEquals(2, repository.cards.size)
        assertEquals(withState, repository.cards[0])
        assertCardCorrect(repository.cards[1], original, translations, deckId, domain)
    }

    @Test
    fun `test__bothMatchButNotDuplicate__newIsMore__expectation_OldCardUpdated_ProgressReset`() {
        // given
        val original = original("spring")
        val translation1 = translation("весна")
        val translation2 = translation("источник")

        val card = repository.addCard(domain, deckId, original, listOf(translation1))
        val updated = repository.updateCardState(card, mockState, exercise)

        assertEquals(1, repository.cards.size)
        assertEquals(updated, repository.cards[0])

        // when
        val translations = listOf(translation1, translation2)
        merger.mergeOrAdd(original, translations, deckId)

        // then
        assertEquals(1, repository.cards.size)
        assertCardCorrect(repository.cards[0], original, listOf(translation1, translation2), deckId, domain)
        assertEquals("progress is reset", emptyState(), repository.cards[0].state)
    }

    @Test
    fun `test__duplicate__newIsLessThanExisting__expectation_NoChange`() {
        // given
        val original = original("spring")
        val translation1 = translation("весна")
        val translation2 = translation("источник")

        val card = repository.addCard(domain, deckId, original, listOf(translation1, translation2))
        val withState = repository.updateCardState(card, mockState, exercise)

        // when
        val translations = listOf(translation2)
        merger.mergeOrAdd(original, translations, deckId)

        // then
        assertEquals(1, repository.cards.size)
        assertEquals(withState, repository.cards[0])
    }

    @Test
    fun `test__fullDuplicate__expectation_NoChange`() {
        // given
        val original = original("spring")
        val translation1 = translation("весна")
        val translation2 = translation("источник")

        val card = repository.addCard(domain, deckId, original, listOf(translation1, translation2))
        val withState = repository.updateCardState(card, mockState, exercise)

        // when
        val translations = listOf(translation1, translation2)
        merger.mergeOrAdd(original, translations, deckId)

        // then
        assertEquals(1, repository.cards.size)
        assertEquals(withState, repository.cards[0])
    }
}