package com.ashalmawia.coriolan.learning

import android.content.Context
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.assignment.Assignment
import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.learning.mutation.MutationRegistry
import com.ashalmawia.coriolan.learning.scheduler.Scheduler
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.learning.scheduler.Status
import com.ashalmawia.coriolan.learning.scheduler.today
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck

class LearningFlow(
        val deck: Deck,
        private val random: Boolean,
        private val exercise: Exercise,
        val scheduler: Scheduler) {

    private lateinit var assignment: Assignment

    val counts: Counts
        get() = assignment.pendingCounter.value

    var listener: FlowListener? = null

    fun start(context: Context) {
        assignment = createAssignment(repository(context), Preferences.get(context), Journal.get(context), random, exercise, deck)
        showNextOrComplete(context)
    }

    private fun showNext(context: Context) {
        listener?.onNext()

        val card = assignment.next()
        exercise.show(context, card)
    }

    private fun showNextOrComplete(context: Context) {
        if (assignment.hasNext()) {
            showNext(context)
        } else {
            finish()
        }
    }

    private fun finish() {
        listener?.onFinish()
        listener = null

        LearningFlow.current = null
    }

    fun card() = assignment.current!!

    fun wrong(context: Context) {
        val card = card()
        assignment.pendingCounter.value.onCardWrong(card)

        recordCardStudied(card.state, journal(context), false)

        val state = scheduler.wrong(card.state)
        updateAndRescheduleIfNeeded(context, card, state)
    }

    fun correct(context: Context) {
        val card = card()
        assignment.pendingCounter.value.onCardCorrect(card)

        recordCardStudied(card.state, journal(context), true)

        val state = scheduler.correct(card.state)
        updateAndRescheduleIfNeeded(context, card, state)
    }

    private fun recordCardStudied(state: State, journal: Journal, correct: Boolean) {
        val date = assignment.date
        when (state.status) {
            Status.NEW -> {
                journal.recordNewCardStudied(date)
            }

            Status.IN_PROGRESS, Status.LEARNT -> {
                if (correct) {
                    journal.recordReviewStudied(date)
                } else {
                    journal.recordCardRelearned(date)
                }
            }

            Status.RELEARN -> {} // ignore all relearns as if they appear they have been already counted somehow
        }
    }

    fun onCurrentCardUpdated(context: Context) {
        val old = card()
        val updated = repository(context).cardById(old.id, deck.domain)!!
        assignment.onCardUpdated(old, updated)
    }

    fun deleteCurrent(context: Context) {
        val card = card()
        assignment.delete(card)
        showNextOrComplete(context)
    }

    private fun updateAndRescheduleIfNeeded(context: Context, card: Card, state: State) {
        repository(context).updateCardState(card, state, exercise)
        if (state.due <= today()) {
            assignment.reschedule(card)
        }
        showNextOrComplete(context)
    }

    companion object {
        var current: LearningFlow? = null

        fun initiate(
                deck: Deck,
                random: Boolean = true,
                exercise: Exercise
        ): LearningFlow {
            val flow = LearningFlow(deck, random, exercise, Scheduler.default())
            current = flow
            return flow
        }

        fun peekCounts(context: Context, exercise: Exercise, deck: Deck): Counts {
            val repository = repository(context)
            val preferences = Preferences.get(context)
            return createAssignment(repository, preferences, journal(context), false, exercise, deck).pendingCounter.value
        }
    }
}

private fun createAssignment(
        repository: Repository, preferences: Preferences, journal: Journal, random: Boolean, exercise: Exercise, deck: Deck): Assignment {
    val date = today()
    val cards = repository.cardsDueDate(exercise, deck, date)

    val mutations = MutationRegistry.mutations(preferences, journal, date, random)
    return Assignment(date, mutations.apply(cards))
}

private fun repository(context: Context) = Repository.get(context)

private fun journal(context: Context) = Journal.get(context)

interface FlowListener {

    fun onNext() {}

    fun onFinish() {}
}