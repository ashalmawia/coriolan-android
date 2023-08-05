package com.ashalmawia.coriolan.ui.learning

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.LearningFlow
import com.ashalmawia.coriolan.learning.StudyTargets
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.DeckId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LearningViewModel(
        private val learningFlowFactory: LearningFlow.Factory,
        private val repository: Repository,
        private val view: LearningView
) : ViewModel(), LearningFlow.Listener {

    private lateinit var flow: LearningFlow

    fun start(
            context: Context,
            uiContainer: ViewGroup,
            deckId: DeckId,
            cardTypeFilter: CardTypeFilter,
            studyOrder: StudyOrder,
            studyTargets: StudyTargets
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val deck = repository.deckById(deckId)
                withContext(Dispatchers.Main) {
                    view.initialize(deck.name)
                    view.showLoading()
                }

                flow = learningFlowFactory.createLearningFlow(
                        context,
                        uiContainer,
                        deck,
                        cardTypeFilter,
                        studyOrder,
                        studyTargets,
                        this@LearningViewModel)
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    beginExercise()
                }
            }
        }
    }

    val canUndo: Boolean
        get() = if (this::flow.isInitialized) flow.canUndo() else false


    fun undo() {
        flow.undo()
    }

    fun editCurrentCard() {
        view.launchEdit(flow.current.card)
    }

    fun deleteCurrentCard() {
        val current = flow.current
        flow.dropCard(current.card)
        deleteCardFromRepository(current.card)
    }

    private fun deleteCardFromRepository(card: Card) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repository.deleteCard(card)
            }
        }
    }

    fun onCurrentCardUpdated() {
        flow.refetchTask(flow.current)
    }

    private fun beginExercise() {
        flow.showNextOrComplete()
    }

    override fun onTaskRendered() {
        view.updateProgressCounts(flow.counts)
    }

    override fun onFinish(emptyAssignment: Boolean) {
        if (!emptyAssignment) {
            view.congratulateWithAccomplishedAssignment()
        }
        view.finish()
    }
}