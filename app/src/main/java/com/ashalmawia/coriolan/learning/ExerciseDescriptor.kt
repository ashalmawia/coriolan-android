package com.ashalmawia.coriolan.learning

import android.content.Context
import android.support.annotation.StringRes
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.assignment.Assignment
import com.ashalmawia.coriolan.learning.mutation.Mutations
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.learning.scheduler.CardWithState
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.learning.scheduler.StateType
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import org.joda.time.DateTime

interface ExerciseDescriptor<S : State, out T : Exercise> {

    /**
     * Unique string ID which must never be changed.
     * Data storing relies on it.
     */
    val stableId: String

    /**
     * State type is needed to ensure that correct data structures
     * are preinitialized on the first start to store states for this exercise.
     * As stableId, it must never change.
     */
    val stateType: StateType

    /**
     * Each exercise must have it's name
     */
    @StringRes
    fun name(): Int

    fun pendingCards(repository: Repository, deck: Deck, date: DateTime): List<CardWithState<S>>

    fun exercise(context: Context, deck: Deck, assignment: Assignment<S>, finishListener: FinishListener): T

    fun onTranslationAdded(repository: Repository, card: Card)

    fun mutations(preferences: Preferences, journal: Journal, date: DateTime, order: StudyOrder): Mutations<S>
}