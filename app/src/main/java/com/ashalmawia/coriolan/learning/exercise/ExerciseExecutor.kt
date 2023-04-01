package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.model.Card

interface ExerciseExecutor : ExerciseRenderer.Listener {

    val exerciseId: ExerciseId

    fun isPending(task: Task): Boolean

    fun getTask(card: Card): Task

    fun renderTask(task: Task)

    fun undoTask(task: Task, undoneState: State): Task
}

interface ExerciseListener {

    fun onTaskStudied(updated: Task)
}