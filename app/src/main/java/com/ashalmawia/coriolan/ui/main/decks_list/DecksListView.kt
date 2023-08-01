package com.ashalmawia.coriolan.ui.main.decks_list

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.LearningBinding
import com.ashalmawia.coriolan.learning.StudyTargets
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.BaseFragment
import com.ashalmawia.coriolan.ui.add_edit.AddEditCardActivity
import com.ashalmawia.coriolan.ui.add_edit.AddEditDeckActivity
import com.ashalmawia.coriolan.ui.commons.list.FlexListBuilder
import com.ashalmawia.coriolan.ui.commons.list.FlexListItem
import com.ashalmawia.coriolan.ui.learning.LearningActivity
import com.ashalmawia.coriolan.ui.util.negativeButton
import com.ashalmawia.coriolan.ui.util.positiveButton
import org.joda.time.DateTime

interface DecksListView {

    fun showLoading()
    fun hideLoading()

    fun setDecks(list: List<DeckListItem>)

    fun showNothingToLearnTodayDialog()
    fun showSuggestStudyMoreDialog(deck: DeckListItem, repository: Repository, today: DateTime)
    fun showDeckEmptyMessage(deck: DeckListItem)
    fun showDeckDetailsDialog(deck: DeckListItem, repository: Repository)
    fun showLearnMoreDialog(deck: DeckListItem, repository: Repository, today: DateTime)

    fun launchLearning(
            deck: DeckListItem,
            studyOrder: StudyOrder,
            studyTargets: StudyTargets
    )

    fun firstDeckView(): View?
}

class DecksListViewImpl(
        private val views: LearningBinding,
        private val fragment: BaseFragment,
        private val viewModel: DecksListViewModel
) : DecksListView {

    private val context = views.root.context
    private val adapter = DecksListAdapter(viewModel)

    init {
        views.decksList.layoutManager = LinearLayoutManager(context)
        views.decksList.adapter = adapter
    }

    override fun showLoading() {
        fragment.showLoading()
    }

    override fun hideLoading() {
        fragment.hideLoading()
    }

    override fun setDecks(list: List<DeckListItem>) {
        val items = buildDecksList(list)
        adapter.setItems(items)
    }

    private fun buildDecksList(decks: List<DeckListItem>): List<FlexListItem> {
        val builder = FlexListBuilder<DeckListItem>()
        builder.addCategory(R.string.decks_select_deck)
        builder.addEntities(decks)
        builder.addOption(R.string.add_deck__title, { createNewDeck(it) }, R.drawable.ic_add)
        return builder.build()
    }

    override fun showNothingToLearnTodayDialog() {
        AlertDialog.Builder(context)
                .setTitle(R.string.decks_list_nothing_to_learn_dialog_title)
                .setMessage(R.string.decks_list_nothing_to_learn_dialog_description)
                .negativeButton(R.string.button_cancel)
                .create()
                .show()
    }

    override fun showSuggestStudyMoreDialog(deck: DeckListItem, repository: Repository, today: DateTime) {
        AlertDialog.Builder(context)
                .setTitle(R.string.decks_list_suggest_study_more_dialog_title)
                .setMessage(R.string.decks_list_suggest_study_more_dialog_description)
                .positiveButton(R.string.decks_list_suggest_study_more_dialog_cta) {
                    showLearnMoreDialog(deck, repository, today)
                }
                .negativeButton(R.string.button_cancel)
                .create()
                .show()
    }

    override fun showDeckEmptyMessage(deck: DeckListItem) {
        AlertDialog.Builder(context)
                .setTitle(R.string.decks_list_deck_empty_dialog_title)
                .setMessage(R.string.decks_list_deck_empty_dialog_description)
                .positiveButton(R.string.decks_list_deck_empty_dialog_cta) {
                    startAddCards(deck.deck)
                }
                .negativeButton(R.string.button_cancel)
                .create()
                .show()
    }

    private fun startAddCards(deck: Deck) {
        val intent = AddEditCardActivity.add(context, deck)
        fragment.startActivity(intent)
    }

    override fun launchLearning(
            deck: DeckListItem,
            studyOrder: StudyOrder,
            studyTargets: StudyTargets
    ) {
        val intent = LearningActivity.intent(context, deck.deck, deck.cardTypeFilter, studyOrder, studyTargets)
        fragment.startActivity(intent)
    }

    override fun showDeckDetailsDialog(deck: DeckListItem, repository: Repository) {
        val dialog = DeckDetailsDialog(fragment.requireActivity(), deck.deck, repository)
        dialog.show()
    }

    override fun showLearnMoreDialog(deck: DeckListItem, repository: Repository, today: DateTime) {
        val dialog = LearnMoreDialog(fragment.requireActivity(), deck, today, repository) { new, review ->
            if (new + review > 0) {
                launchLearning(deck, StudyOrder.default(), StudyTargets(new, review))
            }
        }
        dialog.build().show()
    }

    override fun firstDeckView(): View? {
        return (views.decksList.findViewHolderForAdapterPosition(1)
                as? DeckListDeckViewHolder)?.views?.deckListItemText
    }

    private fun createNewDeck(context: Context) {
        val intent = AddEditDeckActivity.create(context, viewModel.domain)
        fragment.requireActivity().startActivity(intent)
    }
}