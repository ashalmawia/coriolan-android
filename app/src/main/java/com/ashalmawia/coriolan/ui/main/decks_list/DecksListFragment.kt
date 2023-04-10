package com.ashalmawia.coriolan.ui.main.decks_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.LearningBinding
import com.ashalmawia.coriolan.learning.TodayChangeListener
import com.ashalmawia.coriolan.learning.TodayManager
import com.ashalmawia.coriolan.learning.exercise.ExercisesRegistry
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.BaseFragment
import com.ashalmawia.coriolan.ui.learning.CardTypeFilter
import com.ashalmawia.coriolan.ui.learning.LearningActivity
import com.ashalmawia.coriolan.ui.main.DomainActivity
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
    private val exercisesRegistry: ExercisesRegistry by inject()

    private val domain: Domain by lazy {
        val domainId = requireArguments().getLong(ARGUMENT_DOMAIN_ID)
        repository.domainById(domainId)!!
    }
    private val adapter: DecksListAdapter by lazy {
        DecksListAdapter(get(), exercisesRegistry.defaultExercise(), this)
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
        adapter.setData(decksList())
    }

    override fun beginStudy(deck: DeckListItem, studyOrder: StudyOrder) {
        val intent = LearningActivity.intent(requireContext(), deck.deck, deck.cardTypeFilter, studyOrder)
        requireActivity().startActivity(intent)
    }

    override fun showDeckDetailsDialog(deck: DeckListItem) {
        val dialog = DeckDetailsDialog(requireActivity(), deck.deck, today(), repository)
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
        val decks = repository.allDecks(domain)
        return convertDecksToListItems(decks)
    }

    private fun convertDecksToListItems(decks: List<Deck>): List<DeckListItem> {
        return if (preferences.mixForwardAndReverse) {
            decks.map {
                DeckListItem(it, CardTypeFilter.BOTH)
            }
        } else {
            decks.flatMap { listOf(
                    DeckListItem(it, CardTypeFilter.FORWARD),
                    DeckListItem(it, CardTypeFilter.REVERSE)
            ) }
        }
    }

    private fun firstDeckView(): View? {
        return (views.decksList.findViewHolderForAdapterPosition(1) as? DeckViewHolder)?.text
    }
}
