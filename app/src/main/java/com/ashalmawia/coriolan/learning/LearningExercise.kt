package com.ashalmawia.coriolan.learning

import android.content.Context
import android.support.annotation.StringRes
import com.ashalmawia.coriolan.CardActivity
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.assignment.Assignment
import com.ashalmawia.coriolan.learning.mutation.Mutation
import com.ashalmawia.coriolan.learning.mutation.Mutations
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.learning.scheduler.*
import com.ashalmawia.coriolan.learning.scheduler.sr.SRState
import com.ashalmawia.coriolan.learning.scheduler.sr.MultiplierBasedScheduler
import com.ashalmawia.coriolan.learning.scheduler.sr.emptyState
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import org.joda.time.DateTime

/**
 * Simple learning exercise which shows all the cards in the assignment: given front, guess back.
 * After seeing the back side, user asserts themselves.
 *
 * If the card is answered correctly, it removes it from the queue.
 * Otherwise, adds it to the end of the queue.
 */
class LearningExercise(private val context: Context) : Exercise<SRState, LearningAnswer> {

    private val scheduler = MultiplierBasedScheduler()

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

    override fun processReply(repository: Repository, card: CardWithState<SRState>, answer: LearningAnswer, assignment: Assignment<SRState>): CardWithState<SRState> {
        val updated = updateCardState(repository, card, scheduler.processAnswer(answer, card.state))
        rescheduleIfNeeded(updated, assignment)
        return updated
    }

    private fun answers(state: SRState): Array<LearningAnswer> = scheduler.answers(state)

    override fun pendingCards(repository: Repository, deck: Deck, date: DateTime): List<CardWithState<SRState>> {
        return repository.cardsDueDate(stableId, deck, date)
    }

    override fun showCard(card: CardWithState<SRState>) {
        val intent = CardActivity.intent(context, answers(card.state))
        context.startActivity(intent)
    }

    override fun updateCardState(repository: Repository, card: CardWithState<SRState>, newState: SRState): CardWithState<SRState> {
        repository.updateSRCardState(card.card, newState, stableId)
        return CardWithState(card.card, newState)
    }

    override fun getCardWithState(repository: Repository, card: Card): CardWithState<SRState> {
        return CardWithState(card, repository.getSRCardState(card, stableId))
    }

    private fun rescheduleIfNeeded(card: CardWithState<SRState>, assignment: Assignment<SRState>) {
        if (isPending(card)) {
            assignment.reschedule(card)
        }
    }

    override fun isPending(card: CardWithState<SRState>): Boolean = card.state.due <= today()

    override fun mutations(preferences: Preferences, journal: Journal, date: DateTime, order: StudyOrder, deck: Deck): Mutations<SRState> {
        return Mutations(listOf(
                Mutation.CardTypeFilter.from(preferences),
                Mutation.SplitDeck(deck),
                Mutation.SortReviewsByPeriod(),
                Mutation.NewCardsOrder.from(order),
                Mutation.LimitCount(preferences, journal, date),
                Mutation.Shuffle(order == StudyOrder.RANDOM)
        ))
    }

    override fun onTranslationAdded(repository: Repository, card: Card) {
        repository.updateSRCardState(card, emptyState(), stableId)
    }
}