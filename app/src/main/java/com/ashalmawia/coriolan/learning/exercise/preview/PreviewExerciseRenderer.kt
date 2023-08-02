package com.ashalmawia.coriolan.learning.exercise.preview

import android.content.Context
import android.view.ViewGroup
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.exercise.ExerciseRenderer
import com.ashalmawia.coriolan.ui.learning.CardView
import com.ashalmawia.coriolan.ui.learning.CardAnswer
import com.ashalmawia.coriolan.ui.learning.CardViewButton
import com.ashalmawia.coriolan.ui.learning.CardViewConfiguration
import com.ashalmawia.coriolan.ui.learning.CardViewListener

class PreviewExerciseRenderer(
        context: Context,
        private val uiContainer: ViewGroup,
        private val listener: ExerciseRenderer.Listener
) : ExerciseRenderer, CardViewListener {

    private val cardView = CardView(context, cardViewConfig(), this)

    override fun renderTask(task: Task) {
        uiContainer.removeAllViews()
        cardView.bind(task.card, listOf(CardAnswer.ACCEPT))
        uiContainer.addView(cardView)
    }

    override fun onAnswered(answer: CardAnswer) {
        listener.onAnswered(answer)
    }
}

private fun cardViewConfig() = CardViewConfiguration.Builder()
        .addButton(R.string.button_next, CardViewButton.Type.NEUTRAL, CardAnswer.ACCEPT)
        .alwaysOpen(true)
        .showNewBadge(true)
        .build()