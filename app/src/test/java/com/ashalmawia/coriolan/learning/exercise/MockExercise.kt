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

    override fun canUndo(): Boolean = false

    override fun undo() {
    }
}

class MockExerciseDescriptor(override val stableId: String = "mock", override val stateType: StateType = StateType.UNKNOWN) : ExerciseDescriptor<MockState, MockExercise> {

    override fun name(): Int = 0

    override fun pendingCards(repository: Repository, deck: Deck, date: DateTime): List<CardWithState<MockState>> {
        return emptyList()
    }

    override fun exercise(context: Context, deck: Deck, assignment: Assignment<MockState>, finishListener: FinishListener): MockExercise {
        return MockExercise()
    }

    override fun onTranslationAdded(repository: Repository, card: Card) {
    }

    override fun equals(other: Any?): Boolean {
        return other is MockExerciseDescriptor && stableId == other.stableId
    }

    override fun hashCode(): Int {
        return stableId.hashCode()
    }
}