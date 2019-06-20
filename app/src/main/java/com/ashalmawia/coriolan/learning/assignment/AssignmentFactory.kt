package com.ashalmawia.coriolan.learning.assignment

import android.content.Context
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.learning.today
import com.ashalmawia.coriolan.model.Deck

interface AssignmentFactory {

    companion object {

        private var instance: AssignmentFactory? = null

        fun get(context: Context): AssignmentFactory {
            return instance ?: AssignmentFactoryImpl(
                    Repository.get(context),
                    Preferences.get(context),
                    Journal.get(context)
            ).also { instance = it }
        }
    }

    fun <S: State, R> createAssignment(
            order: StudyOrder,
            exercise: Exercise<S, R>,
            deck: Deck
    ): Assignment<S>
}

class AssignmentFactoryImpl(
        private val repository: Repository,
        private val preferences: Preferences,
        private val journal: Journal
) : AssignmentFactory {

    override fun <S : State, R> createAssignment(
            order: StudyOrder,
            exercise: Exercise<S, R>,
            deck: Deck
    ): Assignment<S> {
        val date = today()
        val cards = exercise.pendingCards(repository, deck, date)
        val mutations = exercise.mutations(preferences, journal, date, order, deck)
        return Assignment(date, mutations.apply(cards))
    }
}