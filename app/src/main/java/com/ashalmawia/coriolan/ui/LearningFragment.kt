package com.ashalmawia.coriolan.ui

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.*
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.learning.assignment.AssignmentFactory
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.view.visible
import com.ashalmawia.coriolan.util.inflate
import com.ashalmawia.coriolan.util.setStartDrawableTint
import kotlinx.android.synthetic.main.learning.*
import org.koin.android.ext.android.inject

private const val TAG = "LearningFragment"

class LearningFragment : BaseFragment(), TodayChangeListener, DataFetcher {

    private lateinit var exercise: Exercise<*, *>

    private val repository: Repository by inject()
    private val preferences: Preferences by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.learning, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        exercise = ExercisesRegistry.get(view.context).defaultExercise()
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
        decksList.adapter = DecksAdapter(context, exercise, this, preferences, repository)
    }

    override fun fetchData() {
        (decksList.adapter as DecksAdapter).setData(decksList())
    }

    override fun onDayChanged() {
        // to update pending counters on deck items
        fetchData()
    }

    private fun decksList(): List<Deck> {
        val timeStart = System.currentTimeMillis()
        val decks = decksRegistry().allDecksForLearning()
        Log.d(TAG, "time spend for loading decks: ${System.currentTimeMillis() - timeStart} ms")
        return decks
    }
}

private const val TYPE_HEADER = 1
private const val TYPE_ITEM = 2

private class DecksAdapter(
        private val context: Context,
        private val exercise: Exercise<*, *>,
        private val dataFetcher: DataFetcher,
        private val preferences: Preferences,
        private val repository: Repository
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val journal = Journal.get(context)
    private val assignmentFactory = AssignmentFactory.get(context)
    private val deckCountsProvider = DeckCountsProvider.get(context)

    private val decks: MutableList<Deck> = mutableListOf()
    private val counts: MutableMap<Deck, Counts> = mutableMapOf()

    fun setData(data: List<Deck>) {
        decks.clear()
        decks.addAll(data)

        val timeStart = System.currentTimeMillis()
        decks.forEach { counts[it] = deckCountsProvider.peekCounts(exercise, it) }
        Log.d(TAG, "time spend for loading decks states: ${System.currentTimeMillis() - timeStart} ms")

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return decks.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_ITEM
    }

    private fun positionToIndex(position: Int): Int = position - 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_HEADER) {
            // skip
            return
        }

        holder as DeckViewHolder
        val item = decks[positionToIndex(position)]

        holder.text.text = item.name
        holder.type.text = item.type.toTypeStringRes()?.run { context.getString(this) } ?: ""
        holder.more.isClickable = true
        holder.more.setOnClickListener { showPopupMenu(item, it) }
        holder.itemView.setOnClickListener { studyDefault(item) }
        setPendingStatus(holder, counts.getValue(item))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            createHeaderViewHolder(parent)
        } else {
            createItemViewHolder(parent)
        }
    }

    private fun createHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return HeaderViewHolder(parent.inflate(R.layout.learning_list_header, false))
    }

    private fun createItemViewHolder(parent: ViewGroup): DeckViewHolder {
        val view = parent.inflate(R.layout.deck_list_item, false)
        val holder = DeckViewHolder(view)

        setTint(holder.countNew)
        setTint(holder.countRelearn)
        setTint(holder.countReview)

        return holder
    }

    private fun setTint(view: TextView) {
        view.setStartDrawableTint(R.color.pending_item__foreground)
    }

    private fun showPopupMenu(deck: Deck, anchor: View) {
        val menu = PopupMenu(context, anchor)
        menu.inflate(R.menu.decks_study_options_popup)
        menu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.decks_study_options_popup__straightforward -> studyStraightforward(deck)
                R.id.decks_study_options_popup__random -> studyRandom(deck)
                R.id.decks_study_options_popup__newest_first -> studyNewestFirst(deck)
                R.id.decks_study_options_popup__study_more -> studyMore(deck)
                R.id.deck_study_options_popup__details -> showDeckDetails(deck)
            }
            true
        }
        menu.show()
    }

    private fun studyDefault(deck: Deck) {
        studyRandom(deck)
    }

    private fun studyStraightforward(deck: Deck) {
        instantiateLearningFlow(deck, StudyOrder.ORDER_ADDED)
    }

    private fun studyRandom(deck: Deck) {
        instantiateLearningFlow(deck, StudyOrder.RANDOM)
    }

    private fun studyNewestFirst(deck: Deck) {
        val studyOrder = StudyOrder.NEWEST_FIRST
        instantiateLearningFlow(deck, studyOrder)
    }

    private fun instantiateLearningFlow(deck: Deck, studyOrder: StudyOrder) {
        LearningFlow.initiate(repository, assignmentFactory, deck, studyOrder, exercise, journal)
    }

    private fun studyMore(deck: Deck) {
        val dialog = IncreaseLimitsDialog(context, deck, exercise, today(), repository, preferences).build()
        dialog.setOnDismissListener { dataFetcher.fetchData() }
        dialog.show()
    }

    private fun showDeckDetails(deck: Deck) {
        val dialog = DeckDetailsDialog(context, deck, exercise, today(), repository)
        dialog.show()
    }

    private fun setPendingStatus(holder: DeckViewHolder, counts: Counts) {
        if (counts.isAnythingPending()) {
            holder.countNew.text = counts.new.toString()
            holder.countNew.visible = counts.new > 0

            holder.countRelearn.text = counts.relearn.toString()
            holder.countRelearn.visible = counts.relearn > 0

            holder.countReview.text = counts.review.toString()
            holder.countReview.visible = counts.review > 0

            holder.pending.visibility = View.VISIBLE
        } else {
            holder.pending.visibility = View.INVISIBLE
        }
    }
}

private fun CardType.toTypeStringRes() = when (this) {
    CardType.UNKNOWN -> null
    CardType.FORWARD -> R.string.decks__type__passive
    CardType.REVERSE -> R.string.decks__type__active
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