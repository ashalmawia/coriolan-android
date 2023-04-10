package com.ashalmawia.coriolan.ui.main.decks_list

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.learning.DeckCountsProvider
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.ui.learning.CardTypeFilter
import com.ashalmawia.coriolan.ui.view.visible
import com.ashalmawia.coriolan.util.inflate
import com.ashalmawia.coriolan.util.setStartDrawableTint

private const val TAG = "DecksListAdapter"

private const val TYPE_HEADER = 1
private const val TYPE_ITEM = 2

class DecksListAdapter(
        private val deckCountsProvider: DeckCountsProvider,
        private val exercise: Exercise,
        private val listener: DeckListAdapterListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val decks: MutableList<DeckListItem> = mutableListOf()
    private val counts: MutableMap<DeckListItem, Counts> = mutableMapOf()

    fun setData(data: List<DeckListItem>) {
        decks.clear()
        decks.addAll(data)

        val timeStart = System.currentTimeMillis()
        decks.forEach { counts[it] = deckCountsProvider.peekCounts(exercise, it.deck, it.cardTypeFilter) }
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

        val context = holder.text.context

        holder.text.text = item.deck.name
        if (item.cardTypeFilter == CardTypeFilter.BOTH) {
            holder.type.visible = false
        } else {
            holder.type.text = item.cardTypeFilter.toCardType().toTypeStringRes().run { context.getString(this) }
        }
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

    private fun showPopupMenu(item: DeckListItem, anchor: View) {
        val menu = PopupMenu(anchor.context, anchor)
        menu.inflate(R.menu.decks_study_options_popup)
        menu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.decks_study_options_popup__straightforward -> studyStraightforward(item)
                R.id.decks_study_options_popup__random -> studyRandom(item)
                R.id.decks_study_options_popup__newest_first -> studyNewestFirst(item)
                R.id.decks_study_options_popup__study_more -> studyMore(item)
                R.id.deck_study_options_popup__details -> showDeckDetails(item)
            }
            true
        }
        menu.show()
    }

    private fun studyDefault(item: DeckListItem) {
        studyRandom(item)
    }

    private fun studyStraightforward(item: DeckListItem) {
        instantiateLearningFlow(item, StudyOrder.ORDER_ADDED)
    }

    private fun studyRandom(item: DeckListItem) {
        instantiateLearningFlow(item, StudyOrder.RANDOM)
    }

    private fun studyNewestFirst(item: DeckListItem) {
        val studyOrder = StudyOrder.NEWEST_FIRST
        instantiateLearningFlow(item, studyOrder)
    }

    private fun instantiateLearningFlow(item: DeckListItem, studyOrder: StudyOrder) {
        listener.beginStudy(item, studyOrder)
    }

    private fun studyMore(item: DeckListItem) {
        listener.showIncreaseLimitsDialog(item)
    }

    private fun showDeckDetails(item: DeckListItem) {
        listener.showDeckDetailsDialog(item)
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

interface DeckListAdapterListener {
    fun showDeckDetailsDialog(deck: DeckListItem)

    fun showIncreaseLimitsDialog(deck: DeckListItem)

    fun beginStudy(deck: DeckListItem, studyOrder: StudyOrder)
}

private fun CardType.toTypeStringRes() = when (this) {
    CardType.FORWARD -> R.string.decks__type__passive
    CardType.REVERSE -> R.string.decks__type__active
}