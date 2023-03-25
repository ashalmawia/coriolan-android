package com.ashalmawia.coriolan.learning.exercise.sr

import android.content.Context
import android.view.ViewGroup
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.TodayProvider
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.exercise.ExerciseExecutor
import com.ashalmawia.coriolan.learning.exercise.ExerciseListener
import com.ashalmawia.coriolan.learning.exercise.GenericLogbook
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.ExpressionExtras
import org.joda.time.DateTime

class SpacedRepetitionExerciseExecutor(
        context: Context,
        exercise: Exercise,
        private val repository: Repository,
        private val todayProvider: TodayProvider,
        journal: Journal,
        private val scheduler: SpacedRepetitionScheduler,
        uiContainer: ViewGroup,
        private val listener: ExerciseListener
) : ExerciseExecutor {

    private val renderer = SpacedRepetitionExerciseRenderer(context, uiContainer, this)
    private val logbook = GenericLogbook(journal, todayProvider, exercise)

    override val canUndo: Boolean = exercise.canUndo

    private var currentCard: CardWithState? = null

    override fun renderCard(card: CardWithState, extras: List<ExpressionExtras>) {
        currentCard = card
        renderer.renderCard(card, extras)
    }

    override fun onAnswered(answer: Any) {
        val card = currentCard!!
        val oldState = card.state
        val updated = processReply(card, answer as SRAnswer)
        logbook.recordCardStudied(card.card, oldState, updated.state)
        listener.onCardStudied(updated)
    }

    private fun processReply(card: CardWithState, answer: SRAnswer): CardWithState {
        val newSrState = scheduler.processAnswer(answer, card.state.spacedRepetition)
        return updateCardState(card, card.state.copy(spacedRepetition = newSrState))
    }

    private fun updateCardState(card: CardWithState, newState: State): CardWithState {
        repository.updateCardState(card.card, newState)
        return CardWithState(card.card, newState)
    }

    override fun undoCard(card: CardWithState, undoneState: State): CardWithState {
        val updated = updateCardState(card, card.state)
        logbook.undoCardStudied(updated.card, updated.state, undoneState)
        return updated
    }

    override fun getCardWithState(card: Card): CardWithState {
        return CardWithState(card, repository.getCardState(card))
    }

    override fun isPending(card: CardWithState): Boolean = card.state().due <= todayProvider.today()
}

private fun CardWithState.state() = state.spacedRepetition