package com.ashalmawia.coriolan.learning.exercise.sr

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.exercise.ExerciseRenderer
import com.ashalmawia.coriolan.model.ExpressionExtras
import com.ashalmawia.coriolan.ui.view.CardView
import com.ashalmawia.coriolan.ui.view.CardViewListener

class SpacedRepetitionExerciseRenderer(
        private val scheduler: Scheduler,
        private val listener: ExerciseRenderer.Listener<SRAnswer>
): ExerciseRenderer<SRState, SRAnswer>, CardViewListener {

    private lateinit var cardView: CardView

    override fun prepareUi(context: Context, parentView: ViewGroup): View {
        val inflator = LayoutInflater.from(context)
        cardView = inflator.inflate(R.layout.card_view, parentView, false) as CardView
        cardView.listener = this
        parentView.addView(cardView)
        return cardView
    }

    override fun renderCard(card: CardWithState<SRState>, extras: List<ExpressionExtras>) {
        val answers = answers(card.state).asList()
        cardView.bind(card.card, extras, answers)
    }

    private fun answers(state: SRState): Array<SRAnswer> = scheduler.answers(state)

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