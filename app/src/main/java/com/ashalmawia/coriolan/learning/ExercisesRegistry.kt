package com.ashalmawia.coriolan.learning

object ExercisesRegistry {

    private val default = SimpleExercise()

    private val exercises: MutableList<Exercise> = mutableListOf(
            default
            // all others go here
    )

    fun allExercises(): List<Exercise> {
        return exercises
    }

    fun defaultExercise(): Exercise {
        return default
    }
}