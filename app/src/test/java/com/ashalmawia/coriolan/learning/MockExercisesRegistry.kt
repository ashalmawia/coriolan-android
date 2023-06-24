package com.ashalmawia.coriolan.learning

import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.exercise.ExercisesRegistry

class MockExercisesRegistry(
        private val list: List<Exercise> = listOf()
) : ExercisesRegistry {

    override fun allExercises(): List<Exercise> = list
}