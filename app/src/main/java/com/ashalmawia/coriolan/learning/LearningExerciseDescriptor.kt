package com.ashalmawia.coriolan.learning

import android.content.Context
import android.support.annotation.StringRes
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.assignment.Assignment
import com.ashalmawia.coriolan.learning.mutation.Mutation
import com.ashalmawia.coriolan.learning.mutation.Mutations
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

    override fun exercise(context: Context, deck: Deck, assignment: Assignment<SRState>, finishListener: FinishListener): LearningExercise
            = LearningExercise(context, stableId, deck, assignment, finishListener)

    override fun onTranslationAdded(repository: Repository, card: Card) {
        repository.updateSRCardState(card, emptyState(), stableId)
    }

    override fun mutations(preferences: Preferences, journal: Journal, date: DateTime, random: Boolean): Mutations<SRState> {
        return Mutations(listOf(
                // order matters
                Mutation.CardTypeFilter.from(preferences),
                Mutation.SortByPeriod(),
                Mutation.LimitCount(preferences, journal, date),
                Mutation.Shuffle(random)
        ))
    }
}