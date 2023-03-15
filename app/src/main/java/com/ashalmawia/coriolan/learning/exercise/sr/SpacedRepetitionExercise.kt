package com.ashalmawia.coriolan.learning.exercise.sr

import androidx.annotation.StringRes
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.StateType
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.TodayProvider
import com.ashalmawia.coriolan.learning.exercise.EmptyStateProvider
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.exercise.ExerciseRenderer
import com.ashalmawia.coriolan.learning.mutation.*
import com.ashalmawia.coriolan.model.Card
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
        private val scheduler: Scheduler
) : Exercise<SRState, SRAnswer> {

    override val stableId: String
        get() = "simple"

    override val stateType: StateType
        get() = StateType.SR_STATE

    @StringRes
    override fun name(): Int {
        return R.string.exercise_simple
    }

    override val canUndo: Boolean
        get() = true

    override fun processReply(repository: Repository, card: CardWithState<SRState>, answer: SRAnswer): CardWithState<SRState> {
        return updateCardState(repository, card, scheduler.processAnswer(answer, card.state))
    }

    override fun pendingCards(repository: Repository, deck: Deck, date: DateTime): List<CardWithState<SRState>> {
        return repository.cardsDueDate(stableId, deck, date)
    }

    private fun getStatesForCardsWithOriginals(repository: Repository, originals: List<Long>): Map<Long, SRState> {
        return repository.getStatesForCardsWithOriginals(originals, stableId)
    }

    override fun updateCardState(repository: Repository, card: CardWithState<SRState>, newState: SRState): CardWithState<SRState> {
        // todo: move to the LearningFlow
        repository.updateSRCardState(card.card, newState, stableId)
        return CardWithState(card.card, newState)
    }

    override fun getCardWithState(repository: Repository, card: Card): CardWithState<SRState> {
        return CardWithState(card, repository.getSRCardState(card, stableId))
    }

    override fun isPending(card: CardWithState<SRState>): Boolean = card.state.due <= todayProvider.today()

    override fun mutations(repository: Repository, preferences: Preferences, journal: Journal, date: DateTime, order: StudyOrder, deck: Deck): Mutations<SRState> {
        return Mutations(listOf(
                LearningModeMutation(this, repository),
                SplitDeckMutation(deck),
                SortReviewsByPeriodMutation,
                NewCardsOrderMutation.from(order),
                LimitCountMutation(preferences, journal, date),
                ShuffleMutation(order == StudyOrder.RANDOM)
        ))
    }

    override fun onTranslationAdded(repository: Repository, card: Card) {
        repository.updateSRCardState(card, emptyStateProvider.emptySRState(), stableId)
    }

    override fun createRenderer(listener: ExerciseRenderer.Listener<SRAnswer>): ExerciseRenderer<SRState, SRAnswer> {
        return SpacedRepetitionExerciseRenderer(scheduler, listener)
    }

    class LearningModeMutation(
            private val exercise: SpacedRepetitionExercise,
            private val repository: Repository
    ) : Mutation<SRState> {

        override fun apply(cards: List<CardWithState<SRState>>): List<CardWithState<SRState>> {
            val (forward, reverse) = cards.forwardAndReverseWithState()
            return forward.plus(reverse.filterReady())
        }

        private fun List<CardWithState<SRState>>.filterReady() : List<CardWithState<SRState>> {
            val translationIds = flatMap { it.card.translations }.map { it.id }

            val states = exercise.getStatesForCardsWithOriginals(repository, translationIds)

            return filter {
                it.state.status != Status.NEW || it.card.translations.all { exp -> exp.isReady(states) }
            }
        }

        private fun Expression.isReady(states: Map<Long, SRState>): Boolean {
            val srState = states[id]
            return srState != null && srState.period >= 4
        }
    }
}