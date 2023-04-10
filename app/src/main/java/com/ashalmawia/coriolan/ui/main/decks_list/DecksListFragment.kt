package com.ashalmawia.coriolan.ui.main.decks_list

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.TodayChangeListener
import com.ashalmawia.coriolan.learning.TodayManager
import com.ashalmawia.coriolan.learning.exercise.ExercisesRegistry
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.BaseFragment
import com.ashalmawia.coriolan.ui.learning.LearningActivity
import com.ashalmawia.coriolan.ui.main.DomainActivity
import kotlinx.android.synthetic.main.learning.decksList
import org.joda.time.DateTime
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

private const val TAG = "LearningFragment"

private const val ARGUMENT_DOMAIN_ID = "domain_id"

class DecksListFragment : BaseFragment(), TodayChangeListener, DataFetcher, BeginStudyListener {

    companion object {
        fun create(domain: Domain): DecksListFragment {
            val arguments = Bundle().also {
                it.putLong(ARGUMENT_DOMAIN_ID, domain.id)
            }
            return DecksListFragment().also { it.arguments = arguments }
        }
    }

    private val repository: Repository by inject()
    private val domain: Domain by lazy {
        val domainId = requireArguments().getLong(ARGUMENT_DOMAIN_ID)
        repository.domainById(domainId)!!
    }
    private val adapter: DecksListAdapter by inject { parametersOf(exercisesRegistry.defaultExercise(), this, this) }
    private val exercisesRegistry: ExercisesRegistry by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.learning, container, false)
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

        decksList.layoutManager = LinearLayoutManager(context)
        decksList.adapter = adapter
    }

    override fun fetchData() {
        adapter.setData(decksList())
    }

    override fun beginStudy(deck: Deck, cardType: CardType, studyOrder: StudyOrder) {
        val intent = LearningActivity.intent(requireContext(), deck, cardType, studyOrder)
        requireActivity().startActivity(intent)
    }

    override fun onDayChanged() {
        // to update pending counters on deck items
        fetchData()
    }

    private fun decksList(): List<DeckListItem> {
        val timeStart = System.currentTimeMillis()
        val decks = repository.allDecks(domain)
        Log.d(TAG, "time spend for loading decks: ${System.currentTimeMillis() - timeStart} ms")
        return decks.flatMap { listOf(
                DeckListItem(it, CardType.FORWARD),
                DeckListItem(it, CardType.REVERSE)
        ) }
    }

    private fun firstDeckView(): View? {
        return (decksList.findViewHolderForAdapterPosition(1) as? DeckViewHolder)?.text
    }
}

class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view)

class DeckViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val text = view.findViewById<TextView>(R.id.deck_list_item__text)!!
    val type = view.findViewById<TextView>(R.id.deck_list_item__type)!!
    val more = view.findViewById<ImageView>(R.id.deck_list_item__more)!!
    val pending = view.findViewById<ViewGroup>(R.id.deck_list_item__pending)!!
    val countNew = view.findViewById<TextView>(R.id.pending_counter__new)!!
    val countRelearn: TextView = view.findViewById(R.id.pending_counter__relearn)
    val countReview = view.findViewById<TextView>(R.id.pending_counter__review)!!
}

interface DataFetcher {
    fun fetchData()
}

interface BeginStudyListener {

    fun beginStudy(deck: Deck, cardType: CardType, studyOrder: StudyOrder)
}

typealias DeckDetailsDialogCreator = (DeckListItem, DateTime) -> Dialog
typealias IncreaseLimitsDialogCreator = (DeckListItem, DateTime) -> Dialog