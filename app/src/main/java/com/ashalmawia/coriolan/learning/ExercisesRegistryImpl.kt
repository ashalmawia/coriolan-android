package com.ashalmawia.coriolan.learning

import android.content.Context
import com.ashalmawia.coriolan.learning.exercise.sr.Scheduler

class ExercisesRegistryImpl(context: Context, scheduler: Scheduler) : ExercisesRegistry {

    private val default = SpacedRepetitionExercise(context, scheduler)

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