package com.ashalmawia.coriolan.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import com.ashalmawia.coriolan.BuildConfig
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.debug.DebugIncreaseDateActivity
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.ExercisesRegistry
import com.ashalmawia.coriolan.learning.LearningFlow
import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.learning.ExerciseDescriptor
import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.learning.scheduler.TodayChangeListener
import com.ashalmawia.coriolan.learning.scheduler.TodayManager
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.util.inflate
import com.ashalmawia.coriolan.util.setStartDrawableTint
import kotlinx.android.synthetic.main.decks_list.*

class LearningFragment : Fragment(), TodayChangeListener {

    private lateinit var exercise: ExerciseDescriptor<*, *>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.decks_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        exercise = ExercisesRegistry.defaultExercise()
        initializeList()
    }

    override fun onStart() {
        super.onStart()
        fetchData()

        TodayManager.register(this)
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

    private fun fetchData() {
        (decksList.adapter as DecksAdapter<*, *>).setData(decksList())
    }

    override fun onDayChanged() {
        // to update pending counters on deck items
        fetchData()
    }

    override fun onCreateOptionsMenu(menu: Menu?, menuInflater: MenuInflater?) {
        menuInflater!!.inflate(R.menu.decks_list_menu, menu)

        if (BuildConfig.DEBUG) {
            menu!!.setGroupVisible(R.id.menu_group_debug, true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (BuildConfig.DEBUG) {
            // handle debug options
            when (item.itemId) {
                R.id.menu_debug_increase_date -> increaseDate()
            }
        }

        return true
    }

    private fun increaseDate() {
        val context = context ?: return
        DebugIncreaseDateActivity.launch(context)
    }

    private fun decksList(): List<Deck> {
        return DecksRegistry.get().allDecks()
    }
}

private class DecksAdapter<S: State, E : Exercise>(
        private val context: Context, private val exercise: ExerciseDescriptor<S, E>
) : RecyclerView.Adapter<DeckViewHolder>() {

    private val decks: MutableList<Deck> = mutableListOf()
    private val counts: MutableMap<Long, Counts> = mutableMapOf()

    fun setData(data: List<Deck>) {
        decks.clear()
        decks.addAll(data)

        decks.forEach { counts[it.id] = LearningFlow.peekCounts(context, exercise, it) }

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return decks.size
    }

    override fun onBindViewHolder(holder: DeckViewHolder?, position: Int) {
        val item = decks[position]
        holder!!.text.text = item.name
        holder.more.isClickable = true
        holder.more.setOnClickListener { showPopupMenu(item, it) }
        holder.text.setOnClickListener { studyDefault(item) }
        setPendingStatus(holder, counts[item.id]!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DeckViewHolder {
        val view = parent!!.inflate(R.layout.deck_list_item, false)
        val holder = DeckViewHolder(view)

        setTint(holder.countNew)
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
            val new = counts.countNew()
            holder.countNew.text = new.toString()

            val review = counts.countReview() + counts.countRelearn()
            holder.countReview.text = review.toString()

            holder.pending.visibility = View.VISIBLE
        } else {
            holder.pending.visibility = View.INVISIBLE
        }
    }
}

class DeckViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val text = view.findViewById<TextView>(R.id.deck_list_item__text)!!
    val more = view.findViewById<ImageView>(R.id.deck_list_item__more)!!
    val pending = view.findViewById<ViewGroup>(R.id.deck_list_item__pending)!!
    val countNew = view.findViewById<TextView>(R.id.pending_counter__new)!!
    val countReview = view.findViewById<TextView>(R.id.pending_counter__review)!!
}