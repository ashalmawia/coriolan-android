package com.ashalmawia.coriolan.learning

import android.content.Context
import com.ashalmawia.coriolan.CardActivity
import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.assignment.Assignment
import com.ashalmawia.coriolan.learning.scheduler.Answer
import com.ashalmawia.coriolan.learning.scheduler.CardWithState
import com.ashalmawia.coriolan.learning.scheduler.sr.SRState
import com.ashalmawia.coriolan.learning.scheduler.sr.MultiplierBasedScheduler
import com.ashalmawia.coriolan.learning.scheduler.Status
import com.ashalmawia.coriolan.learning.scheduler.today
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck

/**
 * Simple learning exercise which shows all the cards in the assignment: given front, guess back.
 * After seeing the back side, user asserts themselves.
 *
 * If the card is answered correctly, it removes it from the queue.
 * Otherwise, adds it to the end of the queue.
 */
class LearningExercise(
        private val context: Context,
        private val stableId: String,
        private val deck: Deck,
        private val assignment: Assignment<SRState>,
        private val finishListener: FinishListener
) : Exercise {

    private val repository = Repository.get(context)
    private val journal = Journal.get(context)

    private val scheduler = MultiplierBasedScheduler()

    override fun refetchCard(card: Card) {
        val updated = repository.cardById(card.id, card.domain)!!
        val state = repository.getSRCardState(card, stableId)
        if (updated.deckId == deck.id && isPending(state)) {
            assignment.replace(card, CardWithState(updated, state))
        } else {
            dropCard(card)
        }
    }

    override fun dropCard(card: Card) {
        val removingCurrent = assignment.current?.card == card
        assignment.delete(card)
        if (removingCurrent) {
            showNextOrComplete()
        }
    }

    override fun showNextOrComplete() {
        if (assignment.hasNext()) {
            assignment.next()

            val intent = CardActivity.intent(context)
            context.startActivity(intent)
        } else {
            finish()
        }
    }

    override fun canUndo(): Boolean = assignment.canUndo()

    override fun undo() {
        val newState = assignment.current!!.state
        val undone = assignment.undo()
        updateCardState(undone, undone.state)
        undoCardStudied(undone.state, journal, newState.status != Status.RELEARN)
    }

    private fun finish() {
        finishListener.onFinish()
    }

    val counts: Counts
        get() = assignment.counts()

    fun card() = assignment.current!!

    fun easy() = onCardAnsweredCorrectly { card -> scheduler.easy(card) }

    fun correct() = onCardAnsweredCorrectly { card -> scheduler.correct(card) }

    fun hard() = onCardAnsweredCorrectly { card -> scheduler.hard(card) }

    fun wrong() = onCardAnswered(false, { card -> scheduler.wrong(card) })

    fun answers(state: SRState): Array<Answer> = scheduler.answers(state)

    private fun onCardAnsweredCorrectly(update: (SRState) -> SRState) {
        onCardAnswered(true, update)
    }

    private fun onCardAnswered(correct: Boolean, update: (SRState) -> SRState) {
        val card = assignment.current!!

        recordCardStudied(card.state, journal, correct)
        val updated = updateCardState(card, update.invoke(card.state))
        rescheduleIfNeeded(updated)

        showNextOrComplete()
    }

    private fun updateCardState(card: CardWithState<SRState>, newState: SRState): CardWithState<SRState> {
        repository.updateSRCardState(card.card, newState, stableId)
        return CardWithState(card.card, newState)
    }

    private fun rescheduleIfNeeded(card: CardWithState<SRState>) {
        if (isPending(card.state)) {
            assignment.reschedule(card)
        }
    }

    private fun isPending(state: SRState) = state.due <= today()

    private fun recordCardStudied(state: SRState, journal: Journal, correct: Boolean) {
        val date = assignment.date
        when (state.status) {
            Status.NEW -> {
                journal.recordNewCardStudied(date)
            }

            Status.IN_PROGRESS, Status.LEARNT -> {
                journal.recordReviewStudied(date)
                if (!correct) {
                    journal.recordCardRelearned(date)
                }
            }

            Status.RELEARN -> {
            } // ignore all relearns as if they appear they have been already counted somehow
        }
    }

    private fun undoCardStudied(state: SRState, journal: Journal, correct: Boolean) {
        val date = assignment.date
        when (state.status) {
            Status.NEW -> {
                journal.undoNewCardStudied(date)
            }

            Status.IN_PROGRESS, Status.LEARNT -> {
                if (correct) {
                    journal.undoReviewStudied(date)
                } else {
                    journal.undoCardRelearned(date)
                }
            }

            Status.RELEARN -> {
            } // ignore all relearns as if they appear they have been already counted somehow
        }
    }
}

interface FinishListener {

    fun onFinish() {}
}