package com.ashalmawia.coriolan.learning

import android.content.Context
import android.view.ViewGroup
import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.assignment.AssignmentFactory
import com.ashalmawia.coriolan.learning.exercise.ExercisesRegistry
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.learning.CardTypeFilter

class LearningFlowFactory(
        private val repository: Repository,
        private val exercisesRegistry: ExercisesRegistry,
        private val assignmentFactory: AssignmentFactory,
        private val logbook: Logbook
) : LearningFlow.Factory {
    override fun createLearningFlow(
            context: Context,
            uiContainer: ViewGroup,
            deck: Deck,
            cardTypeFilter: CardTypeFilter,
            studyOrder: StudyOrder,
            listener: LearningFlow.Listener
    ): LearningFlow {
        val assignment = assignmentFactory.createAssignment(studyOrder, deck, cardTypeFilter)
        return LearningFlow(context, repository, assignment, deck, exercisesRegistry, logbook, uiContainer, listener)
    }
}