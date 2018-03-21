package com.ashalmawia.coriolan.learning

import android.content.Context
import android.support.annotation.StringRes
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.assignment.Assignment
import com.ashalmawia.coriolan.learning.scheduler.CardWithState
import com.ashalmawia.coriolan.learning.scheduler.StateType
import com.ashalmawia.coriolan.learning.scheduler.sr.SRState
import com.ashalmawia.coriolan.learning.scheduler.sr.emptyState
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import org.joda.time.DateTime

class LearningExerciseDescriptor : ExerciseDescriptor<SRState, LearningExercise> {

    override val stableId: String
        get() = "simple"

    override val stateType: StateType
        get() = StateType.SR_STATE

    @StringRes
    override fun name(): Int {
        return R.string.exercise_simple
    }

    override fun pendingCards(repository: Repository, deck: Deck, date: DateTime): List<CardWithState<SRState>> {
        return repository.cardsDueDate(stableId, deck, date)
    }

    override fun exercise(context: Context, assignment: Assignment<SRState>, finishListener: FinishListener): LearningExercise
            = LearningExercise(context, stableId, assignment, finishListener)

    override fun onTranslationAdded(repository: Repository, card: Card) {
        repository.updateSRCardState(card, emptyState(), stableId)
    }
}