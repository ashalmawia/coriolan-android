package com.ashalmawia.coriolan

import android.content.Context
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
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.dependencies.domainScope
import com.ashalmawia.coriolan.dependencies.learningFlowScope
import com.ashalmawia.coriolan.learning.SRAnswer
import com.ashalmawia.coriolan.learning.LearningFlow
import com.ashalmawia.coriolan.learning.exercise.sr.SRState
import com.ashalmawia.coriolan.ui.AddEditCardActivity
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.view.CardView
import com.ashalmawia.coriolan.ui.view.CardViewListener
import com.ashalmawia.coriolan.util.setStartDrawableTint
import kotlinx.android.synthetic.main.card_activity.*
import kotlinx.android.synthetic.main.deck_progress_bar.*

private const val REQUEST_CODE_EDIT_CARD = 1

private const val EXTRA_ANSWERS = "answers"

class CardActivity : BaseActivity(), CardViewListener {

    companion object {
        fun intent(context: Context, answers: Array<SRAnswer>): Intent {
            return Intent(context, CardActivity::class.java).apply {
                putExtra(EXTRA_ANSWERS, answers.map { it.toString() }.toTypedArray())
            }
        }
    }

    private val decksRegistry: DecksRegistry = domainScope().get()

    private val flow by lazy {
        @Suppress("UNCHECKED_CAST")
        learningFlowScope().get<LearningFlow<*, *>>() as LearningFlow<SRState, SRAnswer>
    }

    private val finishListener = { finish() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_activity)

        adjustProgressCountsUI()

        setUpToolbar(flow.deck.name)
        toolbarTitle.text = flow.deck.name

        bindToCurrent(intent.answers())

        delegate.isHandleNativeActionModesEnabled = false
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        bindToCurrent(intent.answers())
    }

    private fun Intent.answers() = getStringArrayExtra(EXTRA_ANSWERS).map { SRAnswer.valueOf(it) }

    override fun onStart() {
        super.onStart()

        flow.addFinishListener(finishListener)
    }

    override fun onBackPressed() {
        learningFlowScope().close()
        super.onBackPressed()
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
        val canUndo = flow.canUndo()
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
        flow.undo()
    }

    private fun editCurrentCard() {
        val intent = AddEditCardActivity.edit(this, flow.card.card)
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
        val current = flow.card
        flow.dropCard(current.card)
        decksRegistry.deleteCard(current.card)
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
        flow.refetchCard(flow.card)
    }

    override fun onStop() {
        super.onStop()

        flow.removeFinishListener(finishListener)
    }

    override fun onCorrect() {
        flow.replyCurrent(SRAnswer.CORRECT)
    }

    override fun onWrong() {
        flow.replyCurrent(SRAnswer.WRONG)
    }

    override fun onEasy() {
        flow.replyCurrent(SRAnswer.EASY)
    }

    override fun onHard() {
        flow.replyCurrent(SRAnswer.HARD)
    }

    private fun bindToCurrent(answers: List<SRAnswer>) {
        val view = cardView as CardView
        val card = flow.card
        view.bind(card.card, answers)
        view.listener = this

        updateProgressCounts()

        invalidateOptionsMenu()
    }

    private fun adjustProgressCountsUI() {
        deck_progress_bar__new.setStartDrawableTint(R.color.card_activity__pending_counters)
        deck_progress_bar__review.setStartDrawableTint(R.color.card_activity__pending_counters)
        deck_progress_bar__relearn.setStartDrawableTint(R.color.card_activity__pending_counters)
    }

    private fun updateProgressCounts() {
        val counts = flow.counts
        deck_progress_bar__new.text = counts.new.toString()
        deck_progress_bar__review.text = counts.review.toString()
        deck_progress_bar__relearn.text = counts.relearn.toString()
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