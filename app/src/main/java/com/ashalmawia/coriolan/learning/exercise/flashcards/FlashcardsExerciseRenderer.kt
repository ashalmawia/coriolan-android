package com.ashalmawia.coriolan.learning.exercise.flashcards

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.exercise.ExerciseRenderer
import com.ashalmawia.coriolan.ui.learning.CardView
import com.ashalmawia.coriolan.ui.learning.CardViewListener
import com.ashalmawia.coriolan.ui.learning.CardViewAnswer
import com.ashalmawia.coriolan.ui.learning.CardViewButton
import com.ashalmawia.coriolan.ui.learning.CardViewConfiguration

class FlashcardsExerciseRenderer(
        context: Context,
        private val uiContainer: ViewGroup,
        private val listener: ExerciseRenderer.Listener
): ExerciseRenderer, CardViewListener {

    private val cardView = CardView(context, flashcardsCardViewConfig(), this)

    override fun prepareUi(context: Context, parentView: ViewGroup): View {
        return cardView
    }

    override fun renderTask(task: Task) {
        uiContainer.removeAllViews()

        val answers = answers(task.learningProgress).asList()
        cardView.bind(task.card, answers)

        uiContainer.addView(cardView)
    }

    private fun answers(progress: LearningProgress): Array<CardViewAnswer> {
        return when (progress.status) {
            Status.NEW -> arrayOf(CardViewAnswer.WRONG, CardViewAnswer.CORRECT, CardViewAnswer.EASY)
            Status.RELEARN -> arrayOf(CardViewAnswer.WRONG, CardViewAnswer.CORRECT)
            Status.IN_PROGRESS, Status.LEARNT -> arrayOf(CardViewAnswer.WRONG, CardViewAnswer.HARD, CardViewAnswer.CORRECT, CardViewAnswer.EASY)
        }
    }

    override fun onAnswered(answer: CardViewAnswer) {
        listener.onAnswered(answer)
    }
}

private fun flashcardsCardViewConfig() = CardViewConfiguration.Builder()
        .addButton(R.string.cards_hard, CardViewButton.Type.NEUTRAL, CardViewAnswer.HARD)
        .addButton(R.string.cards_easy, CardViewButton.Type.NEUTRAL, CardViewAnswer.EASY)
        .addButton(R.string.cards_no, CardViewButton.Type.NEGATIVE, CardViewAnswer.WRONG)
        .addButton(R.string.cards_yes, CardViewButton.Type.POSITIVE, CardViewAnswer.CORRECT)
        .alwaysOpen(false)
        .build()