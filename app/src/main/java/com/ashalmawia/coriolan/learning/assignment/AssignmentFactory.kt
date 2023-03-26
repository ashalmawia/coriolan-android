package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.learning.TodayProvider
import com.ashalmawia.coriolan.learning.exercise.ExercisesRegistry
import com.ashalmawia.coriolan.learning.mutation.Mutations
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck

interface AssignmentFactory {

    fun createAssignment(
            order: StudyOrder,
            deck: Deck,
            cardType: CardType
    ): Assignment
}

class AssignmentFactoryImpl(
        private val preferences: Preferences,
        private val exercisesRegistry: ExercisesRegistry,
        private val journal: Journal,
        private val todayProvider: TodayProvider,
        private val historyFactory: HistoryFactory
) : AssignmentFactory {

    override fun createAssignment(
            order: StudyOrder,
            deck: Deck,
            cardType: CardType
    ): Assignment {
        val date = todayProvider.today()
        val cards = exercisesRegistry.enabledExercises().flatMap { it.pendingCards(deck, date) }
        val history = historyFactory.create()
        val mutationList = exercisesRegistry.enabledExercises().flatMap {
            it.mutations(preferences, journal, date, order, deck, cardType)
        }
        val mutations = Mutations(mutationList)
        return Assignment(date, history, mutations.apply(cards))
    }
}