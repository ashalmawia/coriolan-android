package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.TodayProvider
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Deck

interface AssignmentFactory {

    fun <S: State, R> createAssignment(
            order: StudyOrder,
            exercise: Exercise<S, R>,
            deck: Deck
    ): Assignment<S>
}

class AssignmentFactoryImpl(
        private val repository: Repository,
        private val preferences: Preferences,
        private val journal: Journal,
        private val todayProvider: TodayProvider,
        private val historyFactory: HistoryFactory
) : AssignmentFactory {

    override fun <S : State, R> createAssignment(
            order: StudyOrder,
            exercise: Exercise<S, R>,
            deck: Deck
    ): Assignment<S> {
        val date = todayProvider.today()
        val cards = exercise.pendingCards(repository, deck, date)
        val history = historyFactory.create<S>()
        val mutations = exercise.mutations(preferences, journal, date, order, deck)
        return Assignment(date, history, mutations.apply(cards))
    }
}