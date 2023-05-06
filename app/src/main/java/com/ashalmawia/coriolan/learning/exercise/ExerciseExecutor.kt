package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.Task

interface ExerciseExecutor : ExerciseRenderer.Listener {

    val exerciseId: ExerciseId

    fun renderTask(task: Task)
}

interface ExerciseListener {

    fun onTaskStudied(task: Task, newProgress: LearningProgress)
}