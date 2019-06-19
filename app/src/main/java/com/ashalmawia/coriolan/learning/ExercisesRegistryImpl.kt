package com.ashalmawia.coriolan.learning

import android.content.Context

class ExercisesRegistryImpl(context: Context) : ExercisesRegistry {

    private val default = SpacedRRepetitionExercise(context)

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