package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.learning.TodayProvider
import com.ashalmawia.coriolan.learning.exercise.sr.SpacedRepetitionScheduler
import com.ashalmawia.coriolan.learning.exercise.sr.SpacedRepetitionExercise

class ExercisesRegistryImpl(
        todayProvider: TodayProvider,
        emptyStateProvider: EmptyStateProvider,
        scheduler: SpacedRepetitionScheduler
) : ExercisesRegistry {

    private val default: Exercise = SpacedRepetitionExercise(todayProvider, emptyStateProvider, scheduler)

    private val exercises = listOf(
            default
            // all others go here
    )

    override fun allExercises(): List<Exercise> {
        return exercises
    }

    override fun defaultExercise(): Exercise {
        return default
    }
}