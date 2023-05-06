package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.TodayManager
import com.ashalmawia.coriolan.learning.exercise.ExercisesRegistry
import com.ashalmawia.coriolan.learning.mutation.Mutations
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.learning.StudyTargets
import com.ashalmawia.coriolan.learning.mutation.CardTypeMutation
import com.ashalmawia.coriolan.learning.mutation.LearningModeMutation
import com.ashalmawia.coriolan.learning.mutation.LimitCountMutation
import com.ashalmawia.coriolan.learning.mutation.Mutation
import com.ashalmawia.coriolan.learning.mutation.NewCardsOrderMutation
import com.ashalmawia.coriolan.learning.mutation.ShuffleMutation
import com.ashalmawia.coriolan.learning.mutation.SortReviewsByIntervalMutation
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
        val mutations = mutations(repository, order, cardTypeFilter, studyTargets)
        val history = historyFactory.create()

        val cards = repository.pendingCards(deck, date)
        val cardsToLearn = mutations.apply(cards)
        val tasks = exercisesRegistry.enabledExercises().flatMap { it.generateTasks(cardsToLearn) }

        return Assignment(date, history, tasks)
    }

    private fun mutations(
            repository: Repository,
            order: StudyOrder,
            cardTypeFilter: CardTypeFilter,
            studyTargets: StudyTargets
    ): Mutations {
        val mutations = mutableListOf<Mutation>().apply {
            add(LearningModeMutation(repository))
            if (cardTypeFilter != CardTypeFilter.BOTH) {
                add(CardTypeMutation(cardTypeFilter.toCardType()))
            }
            add(SortReviewsByIntervalMutation)
            add(NewCardsOrderMutation.from(order))
            add(LimitCountMutation(studyTargets))
            add(ShuffleMutation(order == StudyOrder.RANDOM))
        }
        return Mutations(mutations)
    }
}