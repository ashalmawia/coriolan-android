package com.ashalmawia.coriolan.learning

import android.content.Context
import com.ashalmawia.coriolan.model.Assignment
import com.ashalmawia.coriolan.model.Deck
import java.util.*

class LearningFlow(
        val deck: Deck,
        val exercise: Exercise) {

    private val assignment: Assignment

    var listener: FlowListener? = null

    init {
        assignment = createAssignment(exercise)
    }

    fun start(context: Context) {
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

    fun reschedule(context: Context) {
        assignment.reschedule(card())
        showNextOrComplete(context)
    }

    fun done(context: Context) {
        assignment.done(card())
        showNextOrComplete(context)
    }

    private fun createAssignment(exercise: Exercise): Assignment {
        // TODO: deck limits or custom options go here
        val cards = exercise.prefilter(deck.cards())
        return Assignment(Date(), cards)
    }

    companion object {
        var current: LearningFlow? = null

        fun initiateDefault(deck: Deck): LearningFlow {
            return initiate(deck, ExercisesRegistry.defaultExercise())
        }

        fun initiate(deck: Deck, exercise: Exercise): LearningFlow {
            val flow = LearningFlow(deck, exercise)
            current = flow
            return flow
        }
    }
}

interface FlowListener {

    fun onNext() {}

    fun onFinish() {}
}