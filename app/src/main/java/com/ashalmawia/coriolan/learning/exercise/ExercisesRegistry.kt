package com.ashalmawia.coriolan.learning.exercise

interface ExercisesRegistry {

    fun allExercises(): List<Exercise>

    fun enabledExercises(): List<Exercise> {
        return allExercises()
    }
}