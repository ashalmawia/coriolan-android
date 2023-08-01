package com.ashalmawia.coriolan.ui.learning

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.databinding.LearningActivityBinding
import com.ashalmawia.coriolan.learning.StudyTargets
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.commons.DeletingCard.confirmDeleteCurrentCard
import com.ashalmawia.coriolan.ui.util.viewModelBuilder
import org.koin.android.ext.android.get

private const val REQUEST_CODE_EDIT_CARD = 1

private const val EXTRA_DECK_ID = "extra_deck_id"
private const val EXTRA_CARD_TYPE_FILTER = "extra_card_type"
private const val EXTRA_STUDY_ORDER = "extra_study_order"
private const val EXTRA_STUDY_TARGETS = "extra_study_targets"

class LearningActivity : BaseActivity() {

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
    private val view: LearningView by lazy { LearningViewImpl(views, this) }

    private val viewModel: LearningViewModel by viewModelBuilder {
        LearningViewModel(get(), get(), view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)

        delegate.isHandleNativeActionModesEnabled = false

        withParameters { deckId, cardType, studyOrder, studyTargets ->
            viewModel.start(this@LearningActivity, views.exerciseContainer, deckId, cardType, studyOrder, studyTargets)
        }
    }

    private fun <T> withParameters(onResolved: (Long, CardTypeFilter, StudyOrder, StudyTargets) -> T): T {
        val deckId = intent.getLongExtra(EXTRA_DECK_ID, 0L)
        val cardType = CardTypeFilter.valueOf(intent.getStringExtra(EXTRA_CARD_TYPE_FILTER)!!)
        val studyOrder = StudyOrder.valueOf(intent.getStringExtra(EXTRA_STUDY_ORDER)!!)
        val studyTargets = intent.getSerializableExtra(EXTRA_STUDY_TARGETS) as StudyTargets

        return onResolved(deckId, cardType, studyOrder, studyTargets)
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
        val canUndo = viewModel.canUndo
        menu.findItem(R.id.learning_menu__undo).isEnabled = canUndo
        menu.findItem(R.id.learning_menu__undo).icon = undoIcon.get(canUndo)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.learning_menu__undo -> {
                viewModel.undo()
                return true
            }
            R.id.learning_menu__edit_card -> {
                viewModel.editCurrentCard()
                return true
            }
            R.id.learning_menu__delete_card -> {
                confirmDeleteCurrentCard(this, viewModel::deleteCurrentCard)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_EDIT_CARD -> {
                viewModel.onCurrentCardUpdated()
            }
        }
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