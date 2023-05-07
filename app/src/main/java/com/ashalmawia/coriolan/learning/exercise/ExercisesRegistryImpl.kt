package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.learning.exercise.flashcards.FlashcardsExercise
import com.ashalmawia.coriolan.learning.exercise.preview.PreviewExercise

class ExercisesRegistryImpl : ExercisesRegistry {

    private val exercises = listOf(
            PreviewExercise(),
            FlashcardsExercise()
    )

    override fun allExercises(): List<Exercise> {
        return exercises
    }
}