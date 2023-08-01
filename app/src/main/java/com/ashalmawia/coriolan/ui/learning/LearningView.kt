package com.ashalmawia.coriolan.ui.learning

import android.widget.Toast
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.databinding.LearningActivityBinding
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Counts
import com.ashalmawia.coriolan.ui.add_edit.AddEditCardActivity
import com.ashalmawia.coriolan.util.setStartDrawableTint

private const val REQUEST_CODE_EDIT_CARD = 1

interface LearningView {
    fun onExerciseBegins(toolbarTitle: String)
    fun updateProgressCounts(counts: Counts)
    fun launchEdit(card: Card)
    fun congratulateWithAccomplishedAssignment()
    fun finish()
}

class LearningViewImpl(
        private val views: LearningActivityBinding,
        private val activity: LearningActivity
) : LearningView {

    init {
        adjustProgressCountsUI()
    }

    override fun onExerciseBegins(toolbarTitle: String) {
        activity.setUpToolbar(toolbarTitle)
        views.toolbarTitle.text = toolbarTitle
    }

    override fun updateProgressCounts(counts: Counts) {
        views.deckProgressBar.apply {
            deckProgressBarNew.text = counts.new.toString()
            deckProgressBarReview.text = counts.review.toString()
            deckProgressBarRelearn.text = counts.relearn.toString()
        }
        activity.invalidateOptionsMenu()
    }

    override fun launchEdit(card: Card) {
        val intent = AddEditCardActivity.edit(activity, card)
        activity.startActivityForResult(intent, REQUEST_CODE_EDIT_CARD)
    }

    private fun adjustProgressCountsUI() {
        views.deckProgressBar.apply {
            deckProgressBarNew.setStartDrawableTint(R.color.card_activity__pending_counters)
            deckProgressBarReview.setStartDrawableTint(R.color.card_activity__pending_counters)
            deckProgressBarRelearn.setStartDrawableTint(R.color.card_activity__pending_counters)
        }
    }

    override fun congratulateWithAccomplishedAssignment() {
        Toast.makeText(activity, R.string.assignment_accomplished_congratulation, Toast.LENGTH_LONG).show()
    }

    override fun finish() {
        activity.finish()
    }
}