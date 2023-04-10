package com.ashalmawia.coriolan.learning

import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.learning.assignment.AssignmentFactory
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.learning.CardTypeFilter

interface DeckCountsProvider {

    fun peekCounts(exercise: Exercise, deck: Deck, cardTypeFilter: CardTypeFilter): Counts
}

class DeckCountsProviderImpl(
        private val assignmentFactory: AssignmentFactory
) : DeckCountsProvider {

    override fun peekCounts(exercise: Exercise, deck: Deck, cardTypeFilter: CardTypeFilter): Counts {
        return assignmentFactory.createAssignment(StudyOrder.RANDOM, deck, cardTypeFilter).counts()
    }
}