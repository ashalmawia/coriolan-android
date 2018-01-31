package com.ashalmawia.coriolan.learning

import android.content.Context

object ExercisesRegistry {

    private val default = SimpleExercise()

    private val exercises = listOf(
            default
            // all others go here
    )

    fun allExercises(): List<Exercise> {
        return exercises
    }

    fun defaultExercise(context: Context): Exercise {
        return default
    }
}