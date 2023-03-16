package com.ashalmawia.coriolan.ui.main.decks_list

import android.app.Dialog
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.dependencies.domainScope
import com.ashalmawia.coriolan.learning.*
import com.ashalmawia.coriolan.learning.exercise.ExercisesRegistry
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.BaseFragment
import com.ashalmawia.coriolan.ui.learning.LearningActivity
import kotlinx.android.synthetic.main.learning.*
import org.joda.time.DateTime
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import kotlin.collections.List

private const val TAG = "LearningFragment"

class DecksListFragment : BaseFragment(), TodayChangeListener, DataFetcher, BeginStudyListener {

    private val decksRegistry: DecksRegistry by lazy { domainScope().get() }
    private val adapter: DecksListAdapter by inject { parametersOf(exercisesRegistry.defaultExercise(), this, this) }
    private val exercisesRegistry: ExercisesRegistry by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.learning, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        initializeList()
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
        val decks = decksRegistry.allDecks()
        Log.d(TAG, "time spend for loading decks: ${System.currentTimeMillis() - timeStart} ms")
        return decks.flatMap { listOf(
                DeckListItem(it, CardType.FORWARD),
                DeckListItem(it, CardType.REVERSE)
        ) }
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