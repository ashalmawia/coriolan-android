package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.learning.Task

interface ExerciseRenderer {

    fun renderTask(task: Task)

    interface Listener {
        fun onAnswered(answer: Any)
    }
}