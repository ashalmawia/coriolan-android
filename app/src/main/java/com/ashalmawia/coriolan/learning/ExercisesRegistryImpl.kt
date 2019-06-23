package com.ashalmawia.coriolan.learning

import android.content.Context
import com.ashalmawia.coriolan.learning.exercise.EmptyStateProvider
import com.ashalmawia.coriolan.learning.exercise.sr.Scheduler

class ExercisesRegistryImpl(
        context: Context,
        todayProvider: TodayProvider,
        emptyStateProvider: EmptyStateProvider,
        scheduler: Scheduler
) : ExercisesRegistry {

    private val default = SpacedRepetitionExercise(context, todayProvider, emptyStateProvider, scheduler)

    private val exercises = listOf(
            default
            // all others go here
    )

    override fun allExercises(): List<Exercise<*, *>> {
        return exercises
    }

    override fun defaultExercise(): Exercise<*, *> {
        return default
    }
}