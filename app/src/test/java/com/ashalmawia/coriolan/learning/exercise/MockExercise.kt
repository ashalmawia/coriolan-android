package com.ashalmawia.coriolan.learning.exercise

import android.content.Context
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.ExerciseDescriptor
import com.ashalmawia.coriolan.learning.FinishListener
import com.ashalmawia.coriolan.learning.assignment.Assignment
import com.ashalmawia.coriolan.learning.assignment.MockState
import com.ashalmawia.coriolan.learning.scheduler.CardWithState
import com.ashalmawia.coriolan.learning.scheduler.StateType
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import org.joda.time.DateTime

class MockExercise : Exercise {
    override fun refetchCard(card: Card) {
    }

    override fun dropCard(card: Card) {
    }

    override fun showNextOrComplete() {
    }
}

class MockExerciseDescriptor(private val name: String = "mock") : ExerciseDescriptor<MockState, MockExercise> {
    override val stableId: String
        get() = name

    override val stateType: StateType
        get() = StateType.UNKNOWN

    override fun name(): Int = 0

    override fun pendingCards(repository: Repository, deck: Deck, date: DateTime): List<CardWithState<MockState>> {
        return emptyList()
    }

    override fun exercise(context: Context, assignment: Assignment<MockState>, finishListener: FinishListener): MockExercise {
        return MockExercise()
    }

    override fun onTranslationAdded(repository: Repository, card: Card) {
    }
}