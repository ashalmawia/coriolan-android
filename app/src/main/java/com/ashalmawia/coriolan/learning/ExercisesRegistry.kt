package com.ashalmawia.coriolan.learning

object ExercisesRegistry {

    private val default = LearningExerciseDescriptor()

    private val exercises = listOf(
            default
            // all others go here
    )

    fun allExercises(): List<ExerciseDescriptor<*, *>> {
        return exercises
    }

    fun defaultExercise(): ExerciseDescriptor<*, *> {
        return default
    }
}