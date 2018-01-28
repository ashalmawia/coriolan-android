package com.ashalmawia.coriolan.learning

import android.content.Context
import com.ashalmawia.coriolan.learning.assignment.Assignment
import com.ashalmawia.coriolan.learning.assignment.RandomAssignment
import com.ashalmawia.coriolan.learning.assignment.StraightForwardAssignment
import com.ashalmawia.coriolan.model.Deck
import java.util.*

class LearningFlow(
        val deck: Deck,
        private val random: Boolean,
        private val exercise: Exercise) {

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
        val date = Date()
        return if (random) RandomAssignment(date, cards) else StraightForwardAssignment(date, cards)
    }

    companion object {
        var current: LearningFlow? = null

        fun initiate(deck: Deck, random: Boolean = true, exercise: Exercise = ExercisesRegistry.defaultExercise()): LearningFlow {
            val flow = LearningFlow(deck, random, exercise)
            current = flow
            return flow
        }
    }
}

interface FlowListener {

    fun onNext() {}

    fun onFinish() {}
}