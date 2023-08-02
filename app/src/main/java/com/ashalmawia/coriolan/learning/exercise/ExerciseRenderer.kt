package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.ui.learning.CardAnswer

interface ExerciseRenderer {

    fun renderTask(task: Task)

    interface Listener {
        fun onAnswered(answer: CardAnswer)
    }
}