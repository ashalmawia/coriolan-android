package com.ashalmawia.coriolan

import android.content.Context

import kotlinx.android.synthetic.main.card_activity.*

import android.content.Intent
import android.graphics.drawable.Drawable

import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
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

    private lateinit var undoIcon: VectorDrawableSelector
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        undoIcon = VectorDrawableSelector.create(this,
                R.drawable.ic_undo, R.color.action_bar_icon_enabled, R.color.action_bar_icon_disabled
        )

        menuInflater.inflate(R.menu.learning_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val canUndo = exercise.canUndo()
        menu.findItem(R.id.learning_menu__undo).isEnabled = canUndo
        menu.findItem(R.id.learning_menu__undo).icon = undoIcon.get(canUndo)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.learning_menu__undo -> {
                undo()
                return true
            }
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

    private fun undo() {
        exercise.undo()
        refresh()
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
                .setPositiveButton(R.string.button_delete) { _, _ -> deleteCurrentCard() }
                .create()
        dialog.show()
    }

    private fun deleteCurrentCard() {
        val current = exercise.card()
        exercise.dropCard(current.card)
        decksRegistry().deleteCard(current.card)
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

        invalidateOptionsMenu()
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

private data class VectorDrawableSelector(val enabled: Drawable, val disabled: Drawable) {

    fun get(isEnabled: Boolean): Drawable = if (isEnabled) enabled else disabled

    companion object {
        fun create(
                context: Context,
                @DrawableRes drawableRes: Int,
                @ColorRes enabledColorRes: Int,
                @ColorRes disabledColorRes: Int
        ): VectorDrawableSelector {

            val resources = context.resources

            val enabled = VectorDrawableCompat.create(resources, drawableRes, null)!!
            DrawableCompat.setTint(enabled, ResourcesCompat.getColor(resources, enabledColorRes, null))

            val disabled = enabled.constantState.newDrawable(resources).mutate()
            DrawableCompat.setTint(disabled, ResourcesCompat.getColor(resources, disabledColorRes, null))

            return VectorDrawableSelector(enabled, disabled)
        }
    }
}