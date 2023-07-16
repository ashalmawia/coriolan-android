package com.ashalmawia.coriolan.ui.main.decks_list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.LearningBinding
import com.ashalmawia.coriolan.learning.StudyTargets
import com.ashalmawia.coriolan.learning.TodayChangeListener
import com.ashalmawia.coriolan.learning.TodayManager
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.BaseFragment
import com.ashalmawia.coriolan.ui.add_edit.AddEditCardActivity
import com.ashalmawia.coriolan.ui.add_edit.AddEditDeckActivity
import com.ashalmawia.coriolan.ui.commons.list.FlexListBuilder
import com.ashalmawia.coriolan.ui.commons.list.FlexListItem
import com.ashalmawia.coriolan.ui.learning.LearningActivity
import com.ashalmawia.coriolan.ui.main.DomainActivity
import com.ashalmawia.coriolan.ui.util.activityViewModelBuilder
import com.ashalmawia.coriolan.ui.util.negativeButton
import com.ashalmawia.coriolan.ui.util.positiveButton
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

private const val ARGUMENT_DOMAIN_ID = "domain_id"

class DecksListFragment : BaseFragment(), DeckListAdapterListener, TodayChangeListener {

    companion object {
        fun create(domain: Domain): DecksListFragment {
            val arguments = Bundle().also {
                it.putLong(ARGUMENT_DOMAIN_ID, domain.id)
            }
            return DecksListFragment().also { it.arguments = arguments }
        }
    }

    private lateinit var views: LearningBinding

    private val repository: Repository by inject()
    private val adapter = DecksListAdapter(this)

    private val viewModel: DecksListViewModel by activityViewModelBuilder {
        DecksListViewModel(requireArguments().getLong(ARGUMENT_DOMAIN_ID), repository, get(), get())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        views = LearningBinding.inflate(inflater, container, false)
        return views.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        initializeList()

        reportFragmentInflated(view)
    }

    private fun reportFragmentInflated(view: View) {
        val globalLayoutListener = object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val firstDeckView = firstDeckView() ?: return
                (requireActivity() as DomainActivity).onDecksListFragmentInflated(firstDeckView)
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }

    override fun onStart() {
        super.onStart()

        TodayManager.register(this)
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    override fun onStop() {
        super.onStop()

        TodayManager.unregister(this)
    }

    private fun initializeList() {
        val context = context ?: return

        views.decksList.layoutManager = LinearLayoutManager(context)
        views.decksList.adapter = adapter
    }

    private fun fetchData() {
        showLoading()
        viewModel.fetchDecksList { decks ->
            val list = buildDecksList(decks)
            adapter.setItems(list)
            hideLoading()
        }
    }

    override fun beginStudy(item: DeckListItem, studyOrder: StudyOrder) {
        if (item.hasPending) {
            launchLearning(item, studyOrder)
        } else {
            viewModel.fetchDeckCardCounts(item) { counts, total ->
                if (total == 0) {
                    showDeckEmptyMessage(item)
                } else if (counts.isAnythingPending()) {
                    showSuggestStudyMoreDialog(item)
                } else {
                    showNothingToLearnTodayDialog()
                }
            }
        }
    }

    private fun showNothingToLearnTodayDialog() {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.decks_list_nothing_to_learn_dialog_title)
                .setMessage(R.string.decks_list_nothing_to_learn_dialog_description)
                .negativeButton(R.string.button_cancel)
                .create()
                .show()
    }

    private fun showSuggestStudyMoreDialog(deck: DeckListItem) {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.decks_list_suggest_study_more_dialog_title)
                .setMessage(R.string.decks_list_suggest_study_more_dialog_description)
                .positiveButton(R.string.decks_list_suggest_study_more_dialog_cta) {
                    showLearnMoreDialog(deck)
                }
                .negativeButton(R.string.button_cancel)
                .create()
                .show()
    }

    private fun showDeckEmptyMessage(deck: DeckListItem) {
        AlertDialog.Builder(requireContext())
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
        val intent = AddEditCardActivity.add(requireContext(), deck)
        startActivity(intent)
    }

    private fun launchLearning(
            deck: DeckListItem,
            studyOrder: StudyOrder,
            studyTargets: StudyTargets = viewModel.defaultStudyTargets()
    ) {
        val intent = LearningActivity.intent(requireContext(), deck.deck, deck.cardTypeFilter, studyOrder, studyTargets)
        requireActivity().startActivity(intent)
    }

    override fun showDeckDetailsDialog(deck: DeckListItem) {
        val dialog = DeckDetailsDialog(requireActivity(), deck.deck, repository)
        dialog.show()
    }

    override fun showLearnMoreDialog(deck: DeckListItem) {
        val dialog = LearnMoreDialog(requireActivity(), deck, today(), repository) { new, review ->
            if (new + review > 0) {
                launchLearning(deck, StudyOrder.default(), StudyTargets(new, review))
            }
        }
        dialog.build().show()
    }

    private fun today() = TodayManager.today()

    override fun onDayChanged() {
        // to update pending counters on deck items
        fetchData()
    }

    private fun buildDecksList(decks: List<DeckListItem>): List<FlexListItem> {
        val builder = FlexListBuilder<DeckListItem>()
        builder.addCategory(R.string.decks_select_deck)
        builder.addEntities(decks)
        builder.addOption(R.string.add_deck__title, { createNewDeck(it) }, R.drawable.ic_add)
        return builder.build()
    }

    private fun createNewDeck(context: Context) {
        val intent = AddEditDeckActivity.create(context, viewModel.domain)
        startActivity(intent)
    }

    private fun firstDeckView(): View? {
        return (views.decksList.findViewHolderForAdapterPosition(1)
                as? DeckListDeckViewHolder)?.views?.deckListItemText
    }
}
