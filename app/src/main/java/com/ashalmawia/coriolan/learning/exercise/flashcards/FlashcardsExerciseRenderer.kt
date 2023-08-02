package com.ashalmawia.coriolan.learning.exercise.flashcards

import android.content.Context
import android.view.ViewGroup
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.exercise.ExerciseRenderer
import com.ashalmawia.coriolan.ui.learning.CardView
import com.ashalmawia.coriolan.ui.learning.CardViewListener
import com.ashalmawia.coriolan.ui.learning.CardAnswer
import com.ashalmawia.coriolan.ui.learning.CardViewButton
import com.ashalmawia.coriolan.ui.learning.CardViewConfiguration

class FlashcardsExerciseRenderer(
        context: Context,
        private val uiContainer: ViewGroup,
        private val listener: ExerciseRenderer.Listener
): ExerciseRenderer, CardViewListener {

    private val cardView = CardView(context, flashcardsCardViewConfig(), this)

    override fun renderTask(task: Task) {
        uiContainer.removeAllViews()

        val answers = answers(task.learningProgress).asList()
        cardView.bind(task.card, answers)

        uiContainer.addView(cardView)
    }

    private fun answers(progress: LearningProgress): Array<CardAnswer> {
        return when (progress.status) {
            Status.NEW -> arrayOf(CardAnswer.WRONG, CardAnswer.CORRECT, CardAnswer.EASY)
            Status.RELEARN -> arrayOf(CardAnswer.WRONG, CardAnswer.CORRECT)
            Status.IN_PROGRESS, Status.LEARNT -> arrayOf(CardAnswer.WRONG, CardAnswer.HARD, CardAnswer.CORRECT, CardAnswer.EASY)
        }
    }

    override fun onAnswered(answer: CardAnswer) {
        listener.onAnswered(answer)
    }
}

private fun flashcardsCardViewConfig() = CardViewConfiguration.Builder()
        .addButton(R.string.cards_hard, CardViewButton.Type.NEUTRAL, CardAnswer.HARD)
        .addButton(R.string.cards_easy, CardViewButton.Type.NEUTRAL, CardAnswer.EASY)
        .addButton(R.string.cards_no, CardViewButton.Type.NEGATIVE, CardAnswer.WRONG)
        .addButton(R.string.cards_yes, CardViewButton.Type.POSITIVE, CardAnswer.CORRECT)
        .alwaysOpen(false)
        .build()