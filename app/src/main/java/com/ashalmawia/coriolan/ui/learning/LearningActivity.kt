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
import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.exercise.sr.SRAnswer
import com.ashalmawia.coriolan.learning.LearningFlow
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.exercise.ExerciseRenderer
import com.ashalmawia.coriolan.learning.exercise.ExercisesRegistry
import com.ashalmawia.coriolan.learning.exercise.sr.SRState
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.ExpressionExtras
import com.ashalmawia.coriolan.ui.AddEditCardActivity
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.util.setStartDrawableTint
import kotlinx.android.synthetic.main.learning_activity.*
import kotlinx.android.synthetic.main.deck_progress_bar.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin

private const val REQUEST_CODE_EDIT_CARD = 1

private const val EXTRA_DOMAIN_ID = "extra_domain_id"
private const val EXTRA_DECK_ID = "extra_deck_id"
private const val EXTRA_STUDY_ORDER = "extra_study_order"

class LearningActivity : BaseActivity(), LearningFlow.Listener<SRState>, ExerciseRenderer.Listener<SRAnswer> {

    companion object {
        fun intent(context: Context, deck: Deck, studyOrder: StudyOrder): Intent {
            val intent = Intent(context, LearningActivity::class.java)
            intent.putExtra(EXTRA_DOMAIN_ID, deck.domain.id)
            intent.putExtra(EXTRA_DECK_ID, deck.id)
            intent.putExtra(EXTRA_STUDY_ORDER, studyOrder.toString())
            return intent
        }
    }

    private val decksRegistry: DecksRegistry = domainScope().get()
    private val repository: Repository = get()

    private val flow by lazy {
        val learningFlowFactory: LearningFlow.Factory<SRState, SRAnswer> = get()
        val exercisesRegistry = getKoin().get<ExercisesRegistry>()
        val (deck, studyOrder) = resolveParameters()
        val exercise = exercisesRegistry.defaultExercise() as Exercise<SRState, SRAnswer>
        learningFlowFactory.createLearningFlow(deck, studyOrder, exercise, this)
    }
    private val renderer by lazy { flow.exercise.createRenderer(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.learning_activity)

        adjustProgressCountsUI()

        setUpToolbar(flow.deck.name)
        toolbarTitle.text = flow.deck.name

        beginExercise()
        delegate.isHandleNativeActionModesEnabled = false
    }

    private fun resolveParameters(): Pair<Deck, StudyOrder> {
        val deckId = intent.getLongExtra(EXTRA_DECK_ID, 0L)
        val domainId = intent.getLongExtra(EXTRA_DOMAIN_ID, 0L)
        val domain = repository.domainById(domainId)!!
        val deck = repository.deckById(deckId, domain)!!
        val studyOrder = StudyOrder.valueOf(intent.getStringExtra(EXTRA_STUDY_ORDER)!!)
        return Pair(deck, studyOrder)
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

    private fun beginExercise() {
        renderer.prepareUi(this, exerciseContainer)

        updateProgressCounts()
        invalidateOptionsMenu()

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

    override fun onRender(card: CardWithState<SRState>, extras: List<ExpressionExtras>) {
        renderer.renderCard(card, extras)
    }

    override fun onFinish() {
        finish()
    }

    override fun onAnswered(answer: SRAnswer) {
        flow.replyCurrent(answer)
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