package com.ashalmawia.coriolan.ui.main.decks_list

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.LearningBinding
import com.ashalmawia.coriolan.learning.TodayChangeListener
import com.ashalmawia.coriolan.learning.TodayManager
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.PendingCardsCount
import com.ashalmawia.coriolan.ui.BaseFragment
import com.ashalmawia.coriolan.ui.add_edit.AddEditCardActivity
import com.ashalmawia.coriolan.ui.learning.CardTypeFilter
import com.ashalmawia.coriolan.ui.learning.LearningActivity
import com.ashalmawia.coriolan.ui.main.DomainActivity
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
    private val preferences: Preferences by inject()

    private val domain: Domain by lazy {
        val domainId = requireArguments().getLong(ARGUMENT_DOMAIN_ID)
        repository.domainById(domainId)!!
    }
    private val adapter = DecksListAdapter(this)

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
        adapter.setData(decksList())
    }

    override fun beginStudy(item: DeckListItem, studyOrder: StudyOrder) {
        if (item.hasPending) {
            launchLearning(item, studyOrder)
        } else {
            val totalCounts = repository.deckPendingCountsMix(item.deck, today())
            val total = repository.deckStats(item.deck)[CardTypeFilter.BOTH]!!.total
            if (total == 0) {
                showDeckEmptyMessage(item)
            } else if (totalCounts.isAnythingPending()) {
                showSuggestStudyMoreDialog(item)
            } else {
                showNothingToLearnTodayDialog()
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
                    showIncreaseLimitsDialog(deck)
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

    private fun launchLearning(deck: DeckListItem, studyOrder: StudyOrder) {
        val intent = LearningActivity.intent(requireContext(), deck.deck, deck.cardTypeFilter, studyOrder)
        requireActivity().startActivity(intent)
    }

    override fun showDeckDetailsDialog(deck: DeckListItem) {
        val dialog = DeckDetailsDialog(requireActivity(), deck.deck, repository)
        dialog.show()
    }

    override fun showIncreaseLimitsDialog(deck: DeckListItem) {
        val dialog = IncreaseLimitsDialog(requireActivity(), deck, today(), repository, get()).build()
        dialog.setOnDismissListener { fetchData() }
        dialog.show()
    }

    private fun today() = TodayManager.today()

    override fun onDayChanged() {
        // to update pending counters on deck items
        fetchData()
    }

    private fun decksList(): List<DeckListItem> {
        val decks = repository.allDecksWithPendingCounts(domain, today())
        return convertDecksToListItems(decks)
    }

    private fun convertDecksToListItems(decks: Map<Deck, PendingCardsCount>): List<DeckListItem> {
        return if (preferences.mixForwardAndReverse) {
            decks.map { (deck, counts) ->
                DeckListItem(deck, CardTypeFilter.BOTH, counts.total > 0)
            }
        } else {
            decks.flatMap { (deck, counts) -> listOf(
                    DeckListItem(deck, CardTypeFilter.FORWARD, counts.forward > 0),
                    DeckListItem(deck, CardTypeFilter.REVERSE, counts.reverse > 0)
            ) }
        }
    }

    private fun firstDeckView(): View? {
        return (views.decksList.findViewHolderForAdapterPosition(1) as? DeckViewHolder)?.views?.deckListItemText
    }
}
