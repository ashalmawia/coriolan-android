package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.TodayProvider
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck

interface AssignmentFactory {

    fun createAssignment(
            order: StudyOrder,
            exercise: Exercise,
            deck: Deck,
            cardType: CardType
    ): Assignment
}

class AssignmentFactoryImpl(
        private val repository: Repository,
        private val preferences: Preferences,
        private val journal: Journal,
        private val todayProvider: TodayProvider,
        private val historyFactory: HistoryFactory
) : AssignmentFactory {

    override fun createAssignment(
            order: StudyOrder,
            exercise: Exercise,
            deck: Deck,
            cardType: CardType
    ): Assignment {
        val date = todayProvider.today()
        val cards = exercise.pendingCards(deck, date)
        val history = historyFactory.create()
        val mutations = exercise.mutations(preferences, journal, date, order, deck, cardType)
        return Assignment(date, history, mutations.apply(cards))
    }
}