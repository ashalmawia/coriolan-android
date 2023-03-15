package com.ashalmawia.coriolan.learning.exercise

import android.content.Context
import com.ashalmawia.coriolan.learning.TodayProvider
import com.ashalmawia.coriolan.learning.exercise.sr.Scheduler
import com.ashalmawia.coriolan.learning.exercise.sr.SpacedRepetitionExercise

class ExercisesRegistryImpl(
        context: Context,
        todayProvider: TodayProvider,
        emptyStateProvider: EmptyStateProvider,
        scheduler: Scheduler
) : ExercisesRegistry {

    private val default = SpacedRepetitionExercise(todayProvider, emptyStateProvider, scheduler)

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