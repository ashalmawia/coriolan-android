package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.exercise.sr.FlashcardsExercise

class ExercisesRegistryImpl(
        repository: Repository,
        emptyStateProvider: EmptyStateProvider
) : ExercisesRegistry {

    private val exercises = listOf(
            FlashcardsExercise(repository, emptyStateProvider)
    )

    override fun allExercises(): List<Exercise> {
        return exercises
    }

    override fun defaultExercise(): Exercise {
        return exercises.first()
    }
}