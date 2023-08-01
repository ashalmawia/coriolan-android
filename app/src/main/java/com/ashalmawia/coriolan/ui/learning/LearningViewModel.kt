package com.ashalmawia.coriolan.ui.learning

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.LearningFlow
import com.ashalmawia.coriolan.learning.StudyTargets
import com.ashalmawia.coriolan.learning.mutation.StudyOrder

class LearningViewModel(
        private val learningFlowFactory: LearningFlow.Factory,
        private val repository: Repository,
        private val view: LearningView
) : ViewModel(), LearningFlow.Listener {

    private lateinit var flow: LearningFlow

    fun start(
            context: Context,
            uiContainer: ViewGroup,
            deckId: Long,
            cardTypeFilter: CardTypeFilter,
            studyOrder: StudyOrder,
            studyTargets: StudyTargets
    ) {
        val deck = repository.deckById(deckId)
        flow = learningFlowFactory.createLearningFlow(
                context,
                uiContainer,
                deck,
                cardTypeFilter,
                studyOrder,
                studyTargets,
                this)
        beginExercise()
    }

    val canUndo: Boolean
        get() = flow.canUndo()


    fun undo() {
        flow.undo()
    }

    fun editCurrentCard() {
        view.launchEdit(flow.current.card)
    }

    fun deleteCurrentCard() {
        val current = flow.current
        flow.dropCard(current.card)
        repository.deleteCard(current.card)
    }

    fun onCurrentCardUpdated() {
        flow.refetchTask(flow.current)
    }

    private fun beginExercise() {
        val toolbarTitle = flow.deck.name
        view.onExerciseBegins(toolbarTitle)
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