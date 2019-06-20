package com.ashalmawia.coriolan.learning

import android.content.Context
import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.learning.assignment.AssignmentFactory
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Deck

interface DeckCountsProvider {

    companion object {

        private var instance: DeckCountsProvider? = null

        fun get(context: Context): DeckCountsProvider {
            return instance ?: DeckCountsProviderImpl(AssignmentFactory.get(context))
        }

    }

    fun <S: State, R> peekCounts(exercise: Exercise<S, R>, deck: Deck): Counts
}

class DeckCountsProviderImpl(
        private val assignmentFactory: AssignmentFactory
) : DeckCountsProvider {

    override fun <S : State, R> peekCounts(exercise: Exercise<S, R>, deck: Deck): Counts {
        return assignmentFactory.createAssignment(StudyOrder.RANDOM, exercise, deck).counts()
    }
}