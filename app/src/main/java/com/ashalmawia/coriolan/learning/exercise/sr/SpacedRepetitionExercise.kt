package com.ashalmawia.coriolan.learning.exercise.sr

import androidx.annotation.StringRes
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.TodayProvider
import com.ashalmawia.coriolan.learning.exercise.EmptyStateProvider
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.exercise.ExerciseRenderer
import com.ashalmawia.coriolan.learning.mutation.*
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Expression
import com.ashalmawia.coriolan.util.forwardAndReverseWithState
import org.joda.time.DateTime

/**
 * Simple learning exercise which shows all the cards in the assignment: given front, guess back.
 * After seeing the back side, user asserts themselves.
 *
 * If the card is answered correctly, it removes it from the queue.
 * Otherwise, adds it to the end of the queue.
 */
class SpacedRepetitionExercise(
        private val todayProvider: TodayProvider,
        private val emptyStateProvider: EmptyStateProvider,
        private val scheduler: SpacedRepetitionScheduler
) : Exercise {

    override val id: ExerciseId
        get() = ExerciseId.SPACED_REPETITION

    @StringRes
    override fun name(): Int {
        return R.string.exercise_simple
    }

    override val canUndo: Boolean
        get() = true

    override fun processReply(repository: Repository, card: CardWithState, answer: Any): CardWithState {
        val newSrState = scheduler.processAnswer(answer as SRAnswer, card.state.spacedRepetition)
        return updateCardState(repository, card, card.state.copy(spacedRepetition = newSrState))
    }

    override fun pendingCards(repository: Repository, deck: Deck, date: DateTime): List<CardWithState> {
        return repository.cardsDueDate(deck, date)
    }

    private fun getStatesForCardsWithOriginals(repository: Repository, originals: List<Long>): Map<Long, State> {
        return repository.getStatesForCardsWithOriginals(originals)
    }

    override fun updateCardState(repository: Repository, card: CardWithState, newState: State): CardWithState {
        // todo: move to the LearningFlow
        repository.updateCardState(card.card, newState)
        return CardWithState(card.card, newState)
    }

    override fun getCardWithState(repository: Repository, card: Card): CardWithState {
        return CardWithState(card, repository.getCardState(card))
    }

    override fun isPending(card: CardWithState): Boolean = card.state().due <= todayProvider.today()

    override fun mutations(
            repository: Repository,
            preferences: Preferences,
            journal: Journal,
            date: DateTime,
            order: StudyOrder,
            deck: Deck,
            cardType: CardType
    ): Mutations {
        return Mutations(listOf(
                LearningModeMutation(this, repository),
                CardTypeMutation(cardType),
                SortReviewsByPeriodMutation,
                NewCardsOrderMutation.from(order),
                LimitCountMutation(preferences, journal, date),
                ShuffleMutation(order == StudyOrder.RANDOM)
        ))
    }

    override fun onTranslationAdded(repository: Repository, card: Card) {
        repository.updateCardState(card, emptyStateProvider.emptyState())
    }

    override fun createRenderer(listener: ExerciseRenderer.Listener): ExerciseRenderer {
        return SpacedRepetitionExerciseRenderer(listener)
    }

    class LearningModeMutation(
            private val exercise: SpacedRepetitionExercise,
            private val repository: Repository
    ) : Mutation {

        override fun apply(cards: List<CardWithState>): List<CardWithState> {
            val (forward, reverse) = cards.forwardAndReverseWithState()
            return forward.plus(reverse.filterReady())
        }

        private fun List<CardWithState>.filterReady() : List<CardWithState> {
            val translationIds = flatMap { it.card.translations }.map { it.id }

            val states = exercise.getStatesForCardsWithOriginals(repository, translationIds)

            return filter {
                it.status() != Status.NEW || it.card.translations.all { exp -> exp.isReady(states) }
            }
        }

        private fun Expression.isReady(states: Map<Long, State>): Boolean {
            val state = states[id]
            return state != null && state.spacedRepetition.period >= 4
        }
    }
}

private fun CardWithState.state() = state.spacedRepetition
private fun CardWithState.status() = state.spacedRepetition.status