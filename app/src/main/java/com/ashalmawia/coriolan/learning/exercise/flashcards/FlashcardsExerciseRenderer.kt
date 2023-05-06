package com.ashalmawia.coriolan.learning.exercise.flashcards

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.exercise.ExerciseRenderer
import com.ashalmawia.coriolan.ui.learning.CardView
import com.ashalmawia.coriolan.ui.learning.CardViewListener

class FlashcardsExerciseRenderer(
        context: Context,
        private val uiContainer: ViewGroup,
        private val listener: ExerciseRenderer.Listener
): ExerciseRenderer, CardViewListener {

    private val cardView = CardView(context, this)

    override fun prepareUi(context: Context, parentView: ViewGroup): View {
        return cardView
    }

    override fun renderTask(task: Task) {
        uiContainer.removeAllViews()

        val answers = answers(task.learningProgress).asList()
        cardView.bind(task.card, answers)

        uiContainer.addView(cardView)
    }

    private fun answers(progress: LearningProgress): Array<FlashcardsAnswer> {
        return when (progress.status) {
            Status.NEW -> arrayOf(FlashcardsAnswer.WRONG, FlashcardsAnswer.CORRECT, FlashcardsAnswer.EASY)
            Status.RELEARN -> arrayOf(FlashcardsAnswer.WRONG, FlashcardsAnswer.CORRECT)
            Status.IN_PROGRESS, Status.LEARNT -> arrayOf(FlashcardsAnswer.WRONG, FlashcardsAnswer.HARD, FlashcardsAnswer.CORRECT, FlashcardsAnswer.EASY)
        }
    }

    override fun onEasy() {
        listener.onAnswered(FlashcardsAnswer.EASY)
    }

    override fun onCorrect() {
        listener.onAnswered(FlashcardsAnswer.CORRECT)
    }

    override fun onHard() {
        listener.onAnswered(FlashcardsAnswer.HARD)
    }

    override fun onWrong() {
        listener.onAnswered(FlashcardsAnswer.WRONG)
    }
}