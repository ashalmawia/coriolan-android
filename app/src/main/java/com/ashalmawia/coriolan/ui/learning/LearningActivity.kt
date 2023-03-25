package com.ashalmawia.coriolan.ui.learning

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.appcompat.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.dependencies.domainScope
import com.ashalmawia.coriolan.learning.LearningFlow
import com.ashalmawia.coriolan.learning.exercise.ExercisesRegistry
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.add_edit.AddEditCardActivity
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.util.setStartDrawableTint
import kotlinx.android.synthetic.main.learning_activity.*
import kotlinx.android.synthetic.main.deck_progress_bar.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin

private const val REQUEST_CODE_EDIT_CARD = 1

private const val EXTRA_DOMAIN_ID = "extra_domain_id"
private const val EXTRA_DECK_ID = "extra_deck_id"
private const val EXTRA_CARD_TYPE = "extra_card_type"
private const val EXTRA_STUDY_ORDER = "extra_study_order"

class LearningActivity : BaseActivity(), LearningFlow.Listener {

    companion object {
        fun intent(context: Context, deck: Deck, cardType: CardType, studyOrder: StudyOrder): Intent {
            val intent = Intent(context, LearningActivity::class.java)
            intent.putExtra(EXTRA_DOMAIN_ID, deck.domain.id)
            intent.putExtra(EXTRA_DECK_ID, deck.id)
            intent.putExtra(EXTRA_CARD_TYPE, cardType.toString())
            intent.putExtra(EXTRA_STUDY_ORDER, studyOrder.toString())
            return intent
        }
    }

    private val decksRegistry: DecksRegistry = domainScope().get()
    private val repository: Repository = get()

    private val flow by lazy {
        val learningFlowFactory: LearningFlow.Factory = get()
        val exercisesRegistry = getKoin().get<ExercisesRegistry>()
        val (deck, cardType, studyOrder) = resolveParameters()
        val exercise = exercisesRegistry.defaultExercise()
        learningFlowFactory.createLearningFlow(
                this, exerciseContainer, deck, cardType, studyOrder, exercise, this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.learning_activity)

        adjustProgressCountsUI()

        beginExercise()
        delegate.isHandleNativeActionModesEnabled = false
    }

    private fun resolveParameters(): Triple<Deck, CardType, StudyOrder> {
        val deckId = intent.getLongExtra(EXTRA_DECK_ID, 0L)
        val domainId = intent.getLongExtra(EXTRA_DOMAIN_ID, 0L)
        val domain = repository.domainById(domainId)!!
        val deck = repository.deckById(deckId, domain)!!
        val cardType = CardType.valueOf(intent.getStringExtra(EXTRA_CARD_TYPE)!!)
        val studyOrder = StudyOrder.valueOf(intent.getStringExtra(EXTRA_STUDY_ORDER)!!)
        return Triple(deck, cardType, studyOrder)
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
        flow.refetchTask(flow.card)
    }

    private fun beginExercise() {
        setUpToolbar(flow.deck.name)
        toolbarTitle.text = flow.deck.name

        flow.showNextOrComplete()
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

    override fun onTaskRendered() {
        updateProgressCounts()
        invalidateOptionsMenu()
    }

    override fun onFinish() {
        finish()
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