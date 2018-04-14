package com.ashalmawia.coriolan

import android.content.Context

import kotlinx.android.synthetic.main.card_activity.*

import android.content.Intent

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.learning.FinishListener
import com.ashalmawia.coriolan.learning.LearningFlow
import com.ashalmawia.coriolan.learning.LearningExercise
import com.ashalmawia.coriolan.ui.AddEditCardActivity
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.view.CardView
import com.ashalmawia.coriolan.ui.view.CardViewListener
import com.ashalmawia.coriolan.util.setStartDrawableTint
import kotlinx.android.synthetic.main.deck_progress_bar.*

private val REQUEST_CODE_EDIT_CARD = 1

class CardActivity : BaseActivity(), CardViewListener, FinishListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_activity)

        adjustProgressCountsUI()

        setUpToolbar(flow().deck.name)
        toolbarTitle.text = flow().deck.name

        bindToCurrent()

        delegate.isHandleNativeActionModesEnabled = false
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        bindToCurrent()
    }

    override fun onStart() {
        super.onStart()

        flow().finishListener = this
    }

    override fun onFinish() {
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.learning_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.learning_menu__edit_card -> {
                editCurrentCard()
                return true
            }
            R.id.learning_menu__delete_card -> {
                confirmDeleteCurrentCard()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun editCurrentCard() {
        val intent = AddEditCardActivity.edit(this, exercise.card().card)
        startActivityForResult(intent, REQUEST_CODE_EDIT_CARD)
    }

    private fun confirmDeleteCurrentCard() {
        val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.learning_menu_delete_card)
                .setMessage(R.string.deleting_card__are_you_sure)
                .setNegativeButton(R.string.button_cancel, null)
                .setPositiveButton(R.string.button_delete) { _, _ -> deleteCurrentCard()}
                .create()
        dialog.show()
    }

    private fun deleteCurrentCard() {
        val current = exercise.card()
        exercise.dropCard(current.card)
        DecksRegistry.get().deleteCard(current.card)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_EDIT_CARD -> {
                onCurrentCardUpdated()
            }
        }
    }

    private fun onCurrentCardUpdated() {
        exercise.refetchCard(exercise.card().card)
        refresh()
    }

    private fun refresh() {
        bindToCurrent()
    }

    override fun onStop() {
        super.onStop()

        LearningFlow.current?.finishListener = null
    }

    override fun onCorrect() {
        exercise.correct()
    }

    override fun onWrong() {
        exercise.wrong()
    }

    override fun onEasy() {
        exercise.easy()
    }

    override fun onHard() {
        exercise.hard()
    }

    private fun bindToCurrent() {
        val view = cardView as CardView
        val card = exercise.card()
        view.bind(card.card, exercise.answers(card.state))
        view.listener = this

        updateProgressCounts()
    }

    private fun flow() = LearningFlow.current!!
    private val exercise = flow().exercise as LearningExercise

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, CardActivity::class.java)
        }
    }

    private fun adjustProgressCountsUI() {
        deck_progress_bar__new.setStartDrawableTint(R.color.card_activity__pending_counters)
        deck_progress_bar__review.setStartDrawableTint(R.color.card_activity__pending_counters)
        deck_progress_bar__relearn.setStartDrawableTint(R.color.card_activity__pending_counters)
    }

    private fun updateProgressCounts() {
        val counts = exercise.counts
        deck_progress_bar__new.text = counts.countNew().toString()
        deck_progress_bar__review.text = counts.countReview().toString()
        deck_progress_bar__relearn.text = counts.countRelearn().toString()
    }
}
