package com.ashalmawia.coriolan.learning.exercise.preview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.exercise.ExerciseRenderer
import com.ashalmawia.coriolan.ui.learning.CardView
import com.ashalmawia.coriolan.ui.learning.CardViewAnswer
import com.ashalmawia.coriolan.ui.learning.CardViewButton
import com.ashalmawia.coriolan.ui.learning.CardViewConfiguration
import com.ashalmawia.coriolan.ui.learning.CardViewListener

class PreviewExerciseRenderer(
        context: Context,
        private val uiContainer: ViewGroup,
        private val listener: ExerciseRenderer.Listener
) : ExerciseRenderer, CardViewListener {

    private val cardView = CardView(context, cardViewConfig(), this)

    override fun prepareUi(context: Context, parentView: ViewGroup): View {
        return cardView
    }

    override fun renderTask(task: Task) {
        uiContainer.removeAllViews()
        cardView.bind(task.card, listOf(CardViewAnswer.NEXT))
        uiContainer.addView(cardView)
    }

    override fun onAnswered(answer: CardViewAnswer) {
        listener.onAnswered(answer)
    }
}

private fun cardViewConfig() = CardViewConfiguration.Builder()
        .addButton(R.string.button_next, CardViewButton.Type.NEUTRAL, CardViewAnswer.NEXT)
        .alwaysOpen(true)
        .build()