package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.ExpressionExtras
import com.ashalmawia.coriolan.model.mockTask

class MockExerciseExecutor(private val exercise: MockExercise) : ExerciseExecutor {

    override val exerciseId: ExerciseId
        get() = exercise.id

    override fun isPending(task: Task): Boolean = false

    override fun getTask(card: Card): Task {
        return mockTask(exercise = exercise)
    }

    override fun renderTask(task: Task, extras: List<ExpressionExtras>) {
    }

    override fun undoTask(task: Task, undoneState: State): Task {
        return task
    }

    override fun onAnswered(answer: Any) {
    }
}