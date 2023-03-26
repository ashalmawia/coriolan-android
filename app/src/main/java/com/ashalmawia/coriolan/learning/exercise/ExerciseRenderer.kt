package com.ashalmawia.coriolan.learning.exercise

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.model.TermExtras

interface ExerciseRenderer {

    fun prepareUi(context: Context, parentView: ViewGroup): View

    fun renderTask(task: Task, extras: List<TermExtras>)

    interface Listener {
        fun onAnswered(answer: Any)
    }
}