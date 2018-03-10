package com.ashalmawia.coriolan

import android.annotation.SuppressLint
import android.content.Context

import kotlinx.android.synthetic.main.card_activity.*

import android.content.Intent

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.debug.DEBUG_SHOW_SCHEDULER_STATUS
import com.ashalmawia.coriolan.learning.FlowListener
import com.ashalmawia.coriolan.learning.LearningFlow
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.ui.AddEditCardActivity
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.view.CardView
import com.ashalmawia.coriolan.ui.view.CardViewListener
import com.ashalmawia.coriolan.util.setStartDrawableTint
import kotlinx.android.synthetic.main.deck_progress_bar.*

private val REQUEST_CODE_EDIT_CARD = 1

class CardActivity : BaseActivity(), CardViewListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_activity)

        adjustProgressCountsUI()
        addDebugViewIfNeeded()

        setUpToolbar(flow().deck.name)

        bindToCurrent()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        bindToCurrent()
    }

    override fun onStart() {
        super.onStart()

        flow().listener = object : FlowListener {
            override fun onFinish() {
                finish()
            }
        }
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
        val intent = AddEditCardActivity.edit(this, flow().card())
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
        val current = flow().card()
        flow().deleteCurrent(this)
        DecksRegistry.get().deleteCard(current)
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
        flow().onCurrentCardUpdated(this)
        refresh()
    }

    private fun refresh() {
        bindToCurrent()
    }

    override fun onStop() {
        super.onStop()

        LearningFlow.current?.listener = null
    }

    override fun onCorrect() {
        flow().correct(this)
    }

    override fun onWrong() {
        flow().wrong(this)
    }

    override fun onEasy() {
        flow().easy(this)
    }

    override fun onHard() {
        flow().hard(this)
    }

    private fun bindToCurrent() {
        val flow = flow()

        val view = cardView as CardView
        view.bind(flow.card(), flow.answers())
        view.listener = this

        updateProgressCounts()

        maybeUpdateDebugView(flow.card())
    }

    private fun flow() = LearningFlow.current!!

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, CardActivity::class.java)
        }
    }

    private fun adjustProgressCountsUI() {
        deck_progress_bar__new.setStartDrawableTint(R.color.deck_progress_bar__foregreound)
        deck_progress_bar__review.setStartDrawableTint(R.color.deck_progress_bar__foregreound)
        deck_progress_bar__relearn.setStartDrawableTint(R.color.deck_progress_bar__foregreound)
    }

    private fun updateProgressCounts() {
        val counts = flow().counts
        deck_progress_bar__new.text = counts.countNew().toString()
        deck_progress_bar__review.text = counts.countReview().toString()
        deck_progress_bar__relearn.text = counts.countRelearn().toString()
    }

    private fun addDebugViewIfNeeded() {
        if (DEBUG_SHOW_SCHEDULER_STATUS) {
            val view = TextView(this)
            view.tag = "DebugStatus"
            view.setPadding(100, view.paddingTop, 10, view.paddingBottom)
            root.addView(view)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun maybeUpdateDebugView(card: Card) {
        if (DEBUG_SHOW_SCHEDULER_STATUS) {
            val scheduler = flow().scheduler
            val view = root.findViewWithTag<TextView>("DebugStatus")
            view.text = "now: ${card.state}\n\n" +
                    "yes: ${scheduler.correct(card.state)}\n" +
                    "no: ${scheduler.wrong(card.state)}"
        }
    }
}
