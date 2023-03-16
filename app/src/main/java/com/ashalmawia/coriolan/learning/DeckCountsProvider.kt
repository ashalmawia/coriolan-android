package com.ashalmawia.coriolan.learning

import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.learning.assignment.AssignmentFactory
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck

interface DeckCountsProvider {

    fun <S: State, R> peekCounts(exercise: Exercise<S, R>, deck: Deck, cardType: CardType): Counts
}

class DeckCountsProviderImpl(
        private val assignmentFactory: AssignmentFactory
) : DeckCountsProvider {

    override fun <S : State, R> peekCounts(exercise: Exercise<S, R>, deck: Deck, cardType: CardType): Counts {
        return assignmentFactory.createAssignment(StudyOrder.RANDOM, exercise, deck, cardType).counts()
    }
}