package com.ashalmawia.coriolan.learning

import android.content.Context
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.learning.assignment.Assignment
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck

class LearningFlow<S : State, R>(
        private val repository: Repository,
        preferences: Preferences,
        val deck: Deck,
        studyOrder: StudyOrder,
        val exercise: Exercise<S, R>,
        val journal: Journal
) {

    private val assignment: Assignment<S> = createAssignment(
            repository,
            preferences,
            journal,
            studyOrder,
            exercise,
            deck
    )

    var finishListener: FinishListener? = null

    val card
        get() = assignment.current!!

    val counts: Counts
        get() = assignment.counts()

    fun showNextOrComplete() {
        if (assignment.hasNext()) {
            val card = assignment.next()
            exercise.showCard(card)
        } else {
            finish()
        }
    }

    fun replyCurrent(reply: R) {
        val updated = exercise.processReply(repository, card, reply, assignment)
        recordCardStudied(card.state.status, updated.state.status, journal)
        showNextOrComplete()
    }

    private fun finish() {
        current = null
        finishListener?.onFinish()
    }

    fun canUndo() = exercise.canUndo

    fun undo() {
        val newState = assignment.current!!.state
        val undone = assignment.undo()
        exercise.updateCardState(repository, undone, undone.state)
        undoCardStudied(undone.state.status, journal, newState.status != Status.RELEARN)
        exercise.showCard(undone)
    }

    private fun recordCardStudied(oldStatus: Status, newStatus: Status, journal: Journal) {
        val date = assignment.date
        when (oldStatus) {
            Status.NEW -> {
                journal.recordNewCardStudied(date)
            }

            Status.IN_PROGRESS, Status.LEARNT -> {
                journal.recordReviewStudied(date)
                if (newStatus == Status.RELEARN) {
                    journal.recordCardRelearned(date)
                }
            }

            Status.RELEARN -> {
            } // ignore all relearns as if they appear they have been already counted somehow
        }
    }

    private fun undoCardStudied(status: Status, journal: Journal, correct: Boolean) {
        val date = assignment.date
        when (status) {
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

    fun refetchCard(cardWithState: CardWithState<S>) {
        val card = cardWithState.card
        val updated = repository.cardById(card.id, card.domain)!!
        val updatedWithState = exercise.getCardWithState(repository, updated)
        if (updated.deckId == deck.id && exercise.isPending(updatedWithState)) {
            assignment.replace(card, updatedWithState)
            if (isCurrent(card)) {
                // make exercise pre-present the card with the changes
                exercise.showCard(updatedWithState)
            }
        } else {
            dropCard(card)
        }
    }

    fun dropCard(card: Card) {
        val removingCurrent = isCurrent(card)
        assignment.delete(card)
        if (removingCurrent) {
            showNextOrComplete()
        }
    }

    private fun isCurrent(card: Card) = this.card.card.id == card.id

    companion object {
        var current: LearningFlow<*, *>? = null

        fun <S: State, R> initiate(
                repository: Repository,
                preferences: Preferences,
                deck: Deck,
                studyOrder: StudyOrder = StudyOrder.RANDOM,
                exercise: Exercise<S, R>,
                journal: Journal
        ) {
            val flow = LearningFlow(repository, preferences, deck, studyOrder, exercise, journal)
            current = flow
            flow.showNextOrComplete()
        }

        fun <S: State, R> peekCounts(context: Context, exercise: Exercise<S, R>, deck: Deck): Counts {
            return createAssignment(
                    Repository.get(context), Preferences.get(context), Journal.get(context), StudyOrder.ORDER_ADDED, exercise, deck
            ).counts()
        }
    }
}

private fun <S: State, R> createAssignment(
        repository: Repository,
        preferences: Preferences,
        journal: Journal,
        order: StudyOrder,
        exercise: Exercise<S, R>,
        deck: Deck
): Assignment<S> {
    val date = today()
    val cards = exercise.pendingCards(repository, deck, date)
    val mutations = exercise.mutations(preferences, journal, date, order, deck)
    return Assignment(date, mutations.apply(cards))
}

interface FinishListener {

    fun onFinish() {}
}