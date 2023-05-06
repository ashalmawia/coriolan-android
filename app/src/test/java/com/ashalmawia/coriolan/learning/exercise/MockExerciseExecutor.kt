package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.learning.Task

class MockExerciseExecutor(private val exercise: MockExercise) : ExerciseExecutor {

    override val exerciseId: ExerciseId
        get() = exercise.id

    override fun renderTask(task: Task) {
    }

    override fun onAnswered(answer: Any) {
    }
}