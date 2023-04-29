package com.ashalmawia.coriolan.learning.exercise

import android.content.Context
import android.view.ViewGroup
import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.StudyTargets
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.mutation.Mutation
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.learning.CardTypeFilter

class MockExercise : Exercise {

    override val id: ExerciseId
        get() = ExerciseId.TEST

    override val canUndo: Boolean
        get() = true

    override fun mutations(repository: Repository, order: StudyOrder, deck: Deck, cardTypeFilter: CardTypeFilter, studyTargets: StudyTargets): List<Mutation> {
        return emptyList()
    }

    override fun createExecutor(context: Context, repository: Repository, uiContainer: ViewGroup, logbook: Logbook, listener: ExerciseListener): ExerciseExecutor {
        return MockExerciseExecutor(this)
    }

    override fun name(): Int = 0

    override fun generateTasks(cards: List<Pair<Card, LearningProgress>>): List<Task> {
        return emptyList()
    }

    override fun onTranslationAdded(repository: Repository, card: Card) {
    }

    override fun equals(other: Any?): Boolean {
        return other is MockExercise && id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}