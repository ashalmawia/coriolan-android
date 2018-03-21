package com.ashalmawia.coriolan.learning

import android.content.Context
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.learning.assignment.Assignment
import com.ashalmawia.coriolan.learning.mutation.MutationRegistry
import com.ashalmawia.coriolan.learning.scheduler.*
import com.ashalmawia.coriolan.model.Deck

class LearningFlow<S : State, out E : Exercise>(
        context: Context,
        val deck: Deck,
        random: Boolean,
        exerciseDescriptor: ExerciseDescriptor<S, E>) : FinishListener {

    val exercise: E

    var finishListener: FinishListener? = null

    init {
        val assignment = createAssignment(repository(context), Preferences.get(context), Journal.get(context), random, exerciseDescriptor, deck)
        exercise = exerciseDescriptor.exercise(context, assignment, this)
        exercise.showNextOrComplete()
    }

    override fun onFinish() {
        LearningFlow.current = null
        finishListener?.onFinish()
    }

    companion object {
        var current: LearningFlow<*, *>? = null

        fun <S: State, E : Exercise> initiate(
                context: Context,
                deck: Deck,
                random: Boolean = true,
                exercise: ExerciseDescriptor<S, E>
        ) {
            val flow = LearningFlow(context, deck, random, exercise)
            current = flow
        }

        fun <S: State, E : Exercise> peekCounts(context: Context, exercise: ExerciseDescriptor<S, E>, deck: Deck): Counts {
            val preferences = Preferences.get(context)
            return createAssignment(repository(context), preferences, journal(context), false, exercise, deck).counts()
        }
    }
}

private fun <S: State, E : Exercise> createAssignment(
        repository: Repository, preferences: Preferences, journal: Journal, random: Boolean, exercise: ExerciseDescriptor<S, E>, deck: Deck
): Assignment<S> {
    val date = today()
    val cards = exercise.pendingCards(repository, deck, date)
    val mutations = MutationRegistry.mutations(preferences, journal, date, random)
    return Assignment(date, mutations.apply(cards))
}

private fun repository(context: Context) = Repository.get(context)

private fun journal(context: Context) = Journal.get(context)