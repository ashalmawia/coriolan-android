package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.TodayManager
import com.ashalmawia.coriolan.learning.exercise.ExercisesRegistry
import com.ashalmawia.coriolan.learning.mutation.Mutations
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.learning.StudyTargets
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.learning.CardTypeFilter

interface AssignmentFactory {

    fun createAssignment(
            order: StudyOrder,
            deck: Deck,
            cardTypeFilter: CardTypeFilter,
            studyTargets: StudyTargets
    ): Assignment
}

class AssignmentFactoryImpl(
        private val repository: Repository,
        private val exercisesRegistry: ExercisesRegistry,
        private val historyFactory: HistoryFactory
) : AssignmentFactory {

    override fun createAssignment(
            order: StudyOrder,
            deck: Deck,
            cardTypeFilter: CardTypeFilter,
            studyTargets: StudyTargets
    ): Assignment {
        val date = TodayManager.today()
        val cards = repository.pendingCards(deck, date)
        val tasks = exercisesRegistry.enabledExercises().flatMap { it.generateTasks(cards) }
        val history = historyFactory.create()
        val mutationList = exercisesRegistry.enabledExercises().flatMap {
            it.mutations(repository, order, deck, cardTypeFilter, studyTargets)
        }
        val mutations = Mutations(mutationList)
        return Assignment(date, history, mutations.apply(tasks))
    }
}