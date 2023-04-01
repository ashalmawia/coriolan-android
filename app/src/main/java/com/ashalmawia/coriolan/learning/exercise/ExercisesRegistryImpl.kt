package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.TodayProvider
import com.ashalmawia.coriolan.learning.exercise.sr.FlashcardsExercise

class ExercisesRegistryImpl(
        repository: Repository,
        todayProvider: TodayProvider,
        emptyStateProvider: EmptyStateProvider
) : ExercisesRegistry {

    private val exercises = listOf(
            FlashcardsExercise(repository, todayProvider, emptyStateProvider)
    )

    override fun allExercises(): List<Exercise> {
        return exercises
    }

    override fun defaultExercise(): Exercise {
        return exercises.first()
    }
}