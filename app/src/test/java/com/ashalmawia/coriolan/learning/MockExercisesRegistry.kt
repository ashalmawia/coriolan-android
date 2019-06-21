package com.ashalmawia.coriolan.learning

import com.ashalmawia.coriolan.learning.exercise.MockExercise

class MockExercisesRegistry(
        private val list: List<Exercise<*, *>> = listOf(MockExercise(stateType = StateType.SR_STATE))
) : ExercisesRegistry {

    override fun allExercises(): List<Exercise<*, *>> = list

    override fun defaultExercise(): Exercise<*, *> = list[0]
}