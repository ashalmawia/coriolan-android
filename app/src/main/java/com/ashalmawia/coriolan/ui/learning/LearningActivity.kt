package com.ashalmawia.coriolan.ui.learning

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.LearningActivityBinding
import com.ashalmawia.coriolan.learning.LearningFlow
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.learning.StudyTargets
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.add_edit.AddEditCardActivity
import com.ashalmawia.coriolan.util.setStartDrawableTint
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

private const val REQUEST_CODE_EDIT_CARD = 1

private const val EXTRA_DECK_ID = "extra_deck_id"
private const val EXTRA_CARD_TYPE_FILTER = "extra_card_type"
private const val EXTRA_STUDY_ORDER = "extra_study_order"
private const val EXTRA_STUDY_TARGETS = "extra_study_targets"

class LearningActivity : BaseActivity(), LearningFlow.Listener {

    companion object {
        fun intent(context: Context,
                   deck: Deck,
                   cardTypeFilter: CardTypeFilter,
                   studyOrder: StudyOrder,
                   studyTargets: StudyTargets
        ): Intent {
            val intent = Intent(context, LearningActivity::class.java)
            intent.putExtra(EXTRA_DECK_ID, deck.id)
            intent.putExtra(EXTRA_CARD_TYPE_FILTER, cardTypeFilter.toString())
            intent.putExtra(EXTRA_STUDY_ORDER, studyOrder.toString())
            intent.putExtra(EXTRA_STUDY_TARGETS, studyTargets)
            return intent
        }
    }

    private val views by lazy { LearningActivityBinding.inflate(layoutInflater) }

    private val repository: Repository by inject()

    private val flow by lazy {
        val learningFlowFactory: LearningFlow.Factory = get()

        withParameters { deck, cardType, studyOrder, studyTargets ->
            learningFlowFactory.createLearningFlow(
                    this, views.exerciseContainer, deck, cardType, studyOrder, studyTargets, this
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)

        adjustProgressCountsUI()

        beginExercise()
        delegate.isHandleNativeActionModesEnabled = false
    }

    private fun <T> withParameters(onResolved: (Deck, CardTypeFilter, StudyOrder, StudyTargets) -> T): T {
        val deckId = intent.getLongExtra(EXTRA_DECK_ID, 0L)
        val deck = repository.deckById(deckId)
        val cardType = CardTypeFilter.valueOf(intent.getStringExtra(EXTRA_CARD_TYPE_FILTER)!!)
        val studyOrder = StudyOrder.valueOf(intent.getStringExtra(EXTRA_STUDY_ORDER)!!)
        val studyTargets = intent.getSerializableExtra(EXTRA_STUDY_TARGETS, StudyTargets::class.java)!!

        return onResolved(deck, cardType, studyOrder, studyTargets)
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
        repository.deleteCard(current.card)
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
        flow.refetchTask(flow.card)
    }

    private fun beginExercise() {
        setUpToolbar(flow.deck.name)
        views.toolbarTitle.text = flow.deck.name

        flow.showNextOrComplete()
    }

    private fun adjustProgressCountsUI() {
        views.deckProgressBar.apply {
            deckProgressBarNew.setStartDrawableTint(R.color.card_activity__pending_counters)
            deckProgressBarReview.setStartDrawableTint(R.color.card_activity__pending_counters)
            deckProgressBarRelearn.setStartDrawableTint(R.color.card_activity__pending_counters)
        }
    }

    private fun updateProgressCounts() {
        val counts = flow.counts
        views.deckProgressBar.apply {
            deckProgressBarNew.text = counts.new.toString()
            deckProgressBarReview.text = counts.review.toString()
            deckProgressBarRelearn.text = counts.relearn.toString()
        }
    }

    override fun onTaskRendered() {
        updateProgressCounts()
        invalidateOptionsMenu()
    }

    override fun onFinish(emptyAssignment: Boolean) {
        if (!emptyAssignment) {
            congratulateWithAccomplishedAssignment()
        }
        finish()
    }

    private fun congratulateWithAccomplishedAssignment() {
        Toast.makeText(this, R.string.assignment_accomplished_congratulation, Toast.LENGTH_LONG).show()
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

            val disabled = enabled.constantState!!.newDrawable(resources).mutate()
            DrawableCompat.setTint(disabled, ResourcesCompat.getColor(resources, disabledColorRes, null))

            return VectorDrawableSelector(enabled, disabled)
        }
    }
}