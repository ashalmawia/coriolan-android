package com.ashalmawia.coriolan.learning

import com.ashalmawia.coriolan.learning.exercise.MockExercise

class MockExercisesRegistry : ExercisesRegistry {

    private val mockExercise = MockExercise(stateType = StateType.SR_STATE)

    override fun allExercises(): List<Exercise<*, *>> = listOf(defaultExercise())

    override fun defaultExercise(): Exercise<*, *> = mockExercise
}