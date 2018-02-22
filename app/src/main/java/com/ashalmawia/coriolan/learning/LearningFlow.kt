package com.ashalmawia.coriolan.learning

import android.content.Context
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.assignment.Assignment
import com.ashalmawia.coriolan.learning.assignment.Counts
import com.ashalmawia.coriolan.learning.assignment.RandomAssignment
import com.ashalmawia.coriolan.learning.assignment.StraightForwardAssignment
import com.ashalmawia.coriolan.learning.scheduler.Scheduler
import com.ashalmawia.coriolan.learning.scheduler.State
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
        assignment = createAssignment(repository(context), random, exercise, deck)
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
        val state = scheduler.wrong(card.state)
        updateAndRescheduleIfNeeded(context, card, state)
        assignment.pendingCounter.value.onCardWrong(card)
    }

    fun correct(context: Context) {
        val card = card()
        val state = scheduler.correct(card.state)
        updateAndRescheduleIfNeeded(context, card, state)
        assignment.pendingCounter.value.onCardCorrect(card)
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
            val date = today()
            val repository = repository(context)
            return repository.cardsDueDateCount(exercise, deck, date)
        }
    }
}

private fun createAssignment(repository: Repository, random: Boolean, exercise: Exercise, deck: Deck): Assignment {
    // TODO: deck limits or custom options go here
    val date = today()
    val cards = repository.cardsDueDate(exercise, deck, date)
    return if (random) RandomAssignment(date, cards) else StraightForwardAssignment(date, cards)
}

private fun repository(context: Context) = Repository.get(context)

interface FlowListener {

    fun onNext() {}

    fun onFinish() {}
}