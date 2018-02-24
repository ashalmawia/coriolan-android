package com.ashalmawia.coriolan

import android.annotation.SuppressLint
import android.content.Context

import kotlinx.android.synthetic.main.card_activity.*

import android.content.Intent

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.debug.DEBUG_SHOW_SCHEDULER_STATUS
import com.ashalmawia.coriolan.learning.FlowListener
import com.ashalmawia.coriolan.learning.LearningFlow
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.ui.view.CardView
import com.ashalmawia.coriolan.ui.view.CardViewListener
import com.ashalmawia.coriolan.util.setStartDrawableTint
import com.ashalmawia.coriolan.util.setUpToolbar
import kotlinx.android.synthetic.main.deck_progress_bar.*

class CardActivity : AppCompatActivity(), CardViewListener {

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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.learning_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.learning_menu__delete_card -> {
                confirmDeleteCurrentCard()
            }
        }
        return super.onOptionsItemSelected(item)
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

    private fun bindToCurrent() {
        val view = cardView as CardView
        view.bind(flow().card())
        view.listener = this

        updateProgressCounts()

        maybeUpdateDebugView(flow().card())
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
