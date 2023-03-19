package com.ashalmawia.coriolan.learning.exercise.sr

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.exercise.ExerciseRenderer
import com.ashalmawia.coriolan.model.ExpressionExtras
import com.ashalmawia.coriolan.ui.learning.CardView
import com.ashalmawia.coriolan.ui.learning.CardViewListener

class SpacedRepetitionExerciseRenderer(
        private val listener: ExerciseRenderer.Listener
): ExerciseRenderer, CardViewListener {

    private lateinit var cardView: CardView

    override fun prepareUi(context: Context, parentView: ViewGroup): View {
        val inflator = LayoutInflater.from(context)
        cardView = inflator.inflate(R.layout.card_view, parentView, false) as CardView
        cardView.listener = this
        parentView.addView(cardView)
        return cardView
    }

    override fun renderCard(card: CardWithState, extras: List<ExpressionExtras>) {
        val answers = answers(card.state.spacedRepetition).asList()
        cardView.bind(card.card, extras, answers)
    }

    private fun answers(state: SRState): Array<SRAnswer> {
        return when (state.status) {
            Status.NEW -> arrayOf(SRAnswer.WRONG, SRAnswer.CORRECT, SRAnswer.EASY)
            Status.RELEARN -> arrayOf(SRAnswer.WRONG, SRAnswer.CORRECT)
            Status.IN_PROGRESS, Status.LEARNT -> arrayOf(SRAnswer.WRONG, SRAnswer.HARD, SRAnswer.CORRECT, SRAnswer.EASY)
        }
    }

    override fun onEasy() {
        listener.onAnswered(SRAnswer.EASY)
    }

    override fun onCorrect() {
        listener.onAnswered(SRAnswer.CORRECT)
    }

    override fun onHard() {
        listener.onAnswered(SRAnswer.HARD)
    }

    override fun onWrong() {
        listener.onAnswered(SRAnswer.WRONG)
    }
}