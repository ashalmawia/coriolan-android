package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.learning.exercise.sr.FlashcardsExercise

class ExercisesRegistryImpl : ExercisesRegistry {

    private val exercises = listOf(
            FlashcardsExercise()
    )

    override fun allExercises(): List<Exercise> {
        return exercises
    }

    override fun defaultExercise(): Exercise {
        return exercises.first()
    }
}