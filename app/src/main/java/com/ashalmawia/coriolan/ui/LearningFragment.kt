package com.ashalmawia.coriolan.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.ExercisesRegistry
import com.ashalmawia.coriolan.learning.LearningFlow
import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.learning.ExerciseDescriptor
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.learning.scheduler.TodayChangeListener
import com.ashalmawia.coriolan.learning.scheduler.TodayManager
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.view.visible
import com.ashalmawia.coriolan.util.inflate
import com.ashalmawia.coriolan.util.setStartDrawableTint
import kotlinx.android.synthetic.main.learning.*

private const val TAG = "LearningFragment"

class LearningFragment : Fragment(), TodayChangeListener, DataFetcher {

    private lateinit var exercise: ExerciseDescriptor<*, *>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.learning, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        exercise = ExercisesRegistry.defaultExercise()
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
        decksList.adapter = DecksAdapter(context, exercise)
    }

    override fun fetchData() {
        (decksList.adapter as DecksAdapter<*, *>).setData(decksList())
    }

    override fun onDayChanged() {
        // to update pending counters on deck items
        fetchData()
    }

    private fun decksList(): List<Deck> {
        val timeStart = System.currentTimeMillis()
        val decks = DecksRegistry.get().allDecks()
        Log.d(TAG, "time spend for loading decks: ${System.currentTimeMillis() - timeStart} ms")
        return decks
    }
}

private const val TYPE_HEADER = 1
private const val TYPE_ITEM = 2

private class DecksAdapter<S: State, E : Exercise>(
        private val context: Context, private val exercise: ExerciseDescriptor<S, E>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val decks: MutableList<Deck> = mutableListOf()
    private val counts: MutableMap<Long, Counts> = mutableMapOf()

    fun setData(data: List<Deck>) {
        decks.clear()
        decks.addAll(data)

        val timeStart = System.currentTimeMillis()
        decks.forEach { counts[it.id] = LearningFlow.peekCounts(context, exercise, it) }
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
        holder.more.isClickable = true
        holder.more.setOnClickListener { showPopupMenu(item, it) }
        holder.itemView.setOnClickListener { studyDefault(item) }
        setPendingStatus(holder, counts[item.id]!!)
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
            }
            true
        }
        menu.show()
    }

    private fun studyDefault(deck: Deck) {
        LearningFlow.initiate(context, deck, exercise = exercise)
    }

    private fun studyStraightforward(deck: Deck) {
        LearningFlow.initiate(context, deck, false, exercise)
    }

    private fun studyRandom(deck: Deck) {
        LearningFlow.initiate(context, deck, true, exercise)
    }

    private fun setPendingStatus(holder: DeckViewHolder, counts: Counts) {
        if (counts.isAnythingPending()) {
            holder.countNew.text = counts.countNew().toString()
            holder.countNew.visible = counts.countNew() > 0

            holder.countRelearn.text = counts.countRelearn().toString()
            holder.countRelearn.visible = counts.countRelearn() > 0

            holder.countReview.text = counts.countReview().toString()
            holder.countReview.visible = counts.countReview() > 0

            holder.pending.visibility = View.VISIBLE
        } else {
            holder.pending.visibility = View.INVISIBLE
        }
    }
}

class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view)

class DeckViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val text = view.findViewById<TextView>(R.id.deck_list_item__text)!!
    val more = view.findViewById<ImageView>(R.id.deck_list_item__more)!!
    val pending = view.findViewById<ViewGroup>(R.id.deck_list_item__pending)!!
    val countNew = view.findViewById<TextView>(R.id.pending_counter__new)!!
    val countRelearn: TextView = view.findViewById(R.id.pending_counter__relearn)
    val countReview = view.findViewById<TextView>(R.id.pending_counter__review)!!
}

interface DataFetcher {
    fun fetchData()
}