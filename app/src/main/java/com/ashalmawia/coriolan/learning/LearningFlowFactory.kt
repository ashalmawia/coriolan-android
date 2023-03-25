package com.ashalmawia.coriolan.learning

import android.content.Context
import android.view.ViewGroup
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.assignment.AssignmentFactory
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck

class LearningFlowFactory(
        private val repository: Repository,
        private val assignmentFactory: AssignmentFactory,
        private val journal: Journal
) : LearningFlow.Factory {
    override fun createLearningFlow(
            context: Context,
            uiContainer: ViewGroup,
            deck: Deck,
            cardType: CardType,
            studyOrder: StudyOrder,
            exercise: Exercise,
            listener: LearningFlow.Listener
    ): LearningFlow {
        val assignment = assignmentFactory.createAssignment(studyOrder, exercise, deck, cardType)
        return LearningFlow(context, repository, assignment, deck, exercise, journal, uiContainer, listener)
    }
}